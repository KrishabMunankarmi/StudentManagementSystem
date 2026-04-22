import json
import pickle
import os
import numpy as np
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.pipeline import FeatureUnion
from sklearn.linear_model import LogisticRegression
from sklearn.svm import LinearSVC
from sklearn.naive_bayes import MultinomialNB
from sklearn.ensemble import RandomForestClassifier
from sklearn.pipeline import Pipeline
from sklearn.model_selection import cross_val_score, StratifiedKFold
from sklearn.preprocessing import LabelEncoder
from chatbot.preprocessor import preprocess


DATA_PATH   = 'data/intents.json'
MODEL_PATH  = 'models/chatbot_model.pkl'
LABELS_PATH = 'models/labels.pkl'


def load_training_data(path: str):
    with open(path, 'r') as f:
        data = json.load(f)

    X, y = [], []
    for intent in data['intents']:
        for pattern in intent['patterns']:
            X.append(preprocess(pattern))
            y.append(intent['tag'])
    return X, y


def build_vectorizer():
    """
    Advanced TF-IDF vectorizer using FeatureUnion to combine:
    - Word n-grams (1,2): captures phrases like "my attendance"
    - Character n-grams (3,5): handles typos and morphological variations
    """
    word_vectorizer = TfidfVectorizer(
        analyzer='word',
        ngram_range=(1, 2),
        max_features=3000,
        sublinear_tf=True,
        min_df=1
    )

    char_vectorizer = TfidfVectorizer(
        analyzer='char_wb',
        ngram_range=(3, 5),
        max_features=3000,
        sublinear_tf=True,
        min_df=1
    )

    return FeatureUnion([
        ('word', word_vectorizer),
        ('char', char_vectorizer)
    ])


def build_models(vectorizer):
    """
    Returns a dictionary of all models to evaluate.
    SVM is the primary recommendation from literature for text classification.
    """
    return {
        'SVM (LinearSVC)': Pipeline([
            ('tfidf', vectorizer),
            ('clf',   LinearSVC(C=1.0, max_iter=2000))
        ]),
        'Logistic Regression': Pipeline([
            ('tfidf', build_vectorizer()),
            ('clf',   LogisticRegression(C=5.0, max_iter=1000, solver='lbfgs'))        ]),
        'Naive Bayes': Pipeline([
            ('tfidf', build_vectorizer()),
            ('clf',   MultinomialNB(alpha=0.1))
        ]),
        'Random Forest': Pipeline([
            ('tfidf', build_vectorizer()),
            ('clf',   RandomForestClassifier(n_estimators=200, random_state=42))
        ]),
    }


def evaluate_models(models: dict, X: list, y: list) -> dict:
    """
    Run stratified 5-fold cross validation on all models.
    Returns accuracy scores for each model.
    """
    cv = StratifiedKFold(n_splits=5, shuffle=True, random_state=42)
    results = {}

    print("\n" + "="*55)
    print(f"{'Model':<25} {'Accuracy':>10} {'Std Dev':>10}")
    print("="*55)

    for name, pipeline in models.items():
        scores = cross_val_score(pipeline, X, y, cv=cv, scoring='accuracy')
        results[name] = {
            'mean':     scores.mean(),
            'std':      scores.std(),
            'pipeline': pipeline
        }
        print(f"{name:<25} {scores.mean():>9.2%} {scores.std():>9.2%}")

    print("="*55)
    return results


def train():
    os.makedirs('models', exist_ok=True)

    X, y = load_training_data(DATA_PATH)
    print(f"Training on {len(X)} samples across {len(set(y))} intents.")

    vectorizer = build_vectorizer()
    models     = build_models(vectorizer)

    #Evaluate all models
    print("\nEvaluating all models with 5-fold cross validation...")
    results = evaluate_models(models, X, y)

    #Pick the best model
    best_name = max(results, key=lambda k: results[k]['mean'])
    best      = results[best_name]
    print(f"\nBest model: {best_name} ({best['mean']:.2%} accuracy)")

    #Train best model on full dataset
    best['pipeline'].fit(X, y)
    labels = sorted(set(y))

    #Save model and labels
    with open(MODEL_PATH,  'wb') as f: pickle.dump(best['pipeline'], f)
    with open(LABELS_PATH, 'wb') as f: pickle.dump(labels, f)

    print(f"Model saved to {MODEL_PATH}")
    print(f"Labels saved to {LABELS_PATH}")

    return best['pipeline'], labels


if __name__ == '__main__':
    train()
