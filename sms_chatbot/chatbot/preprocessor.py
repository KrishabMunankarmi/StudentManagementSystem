import nltk
import string
from nltk.tokenize import word_tokenize
from nltk.corpus import stopwords
from nltk.stem import WordNetLemmatizer
from spellchecker import SpellChecker

nltk.download('punkt', quiet=True)
nltk.download('stopwords', quiet=True)
nltk.download('wordnet', quiet=True)
nltk.download('punkt_tab', quiet=True)

lemmatizer  = WordNetLemmatizer()
stop_words  = set(stopwords.words('english'))
spell       = SpellChecker()

#Words to always keep even if they are stop words
KEEP_WORDS = {'when', 'who', 'what', 'where', 'how', 'next', 'my', 'due'}

#Domain specific words the spell checker should not try to correct
DOMAIN_WORDS = {
    'attendance', 'timetable', 'grades', 'exams', 'assignments',
    'fees', 'subjects', 'semester', 'cgpa', 'gpa', 'marks',
    'teacher', 'lecturer', 'module', 'schedule', 'deadline',
    'submitted', 'pending', 'paid', 'unpaid', 'programme'
}

#Add domain words to spell checker dictionary so they are never changed
spell.word_frequency.load_words(DOMAIN_WORDS)


def correct_spelling(tokens: list) -> list:
    """
    Correct spelling mistakes in a list of tokens.
    Skips words that are already correct or are domain-specific terms.
    """
    corrected = []
    for token in tokens:
        #Skip short words, numbers and domain words
        if len(token) <= 2 or token.isdigit() or token in DOMAIN_WORDS:
            corrected.append(token)
            continue

        #If the word is unknown (likely misspelled), correct it
        if token in spell.unknown([token]):
            correction = spell.correction(token)
            corrected.append(correction if correction else token)
        else:
            corrected.append(token)

    return corrected


def preprocess(text: str) -> str:
    """
    Full NLP pipeline:
    1. Lowercase
    2. Tokenise
    3. Remove punctuation
    4. Spell correction
    5. Remove stopwords (keeping meaningful question words)
    6. Lemmatise
    Returns cleaned string for vectorisation.
    """
    text   = text.lower()
    tokens = word_tokenize(text)

    #Remove punctuation tokens
    tokens = [t for t in tokens if t not in string.punctuation]

    #Correct spelling mistakes
    tokens = correct_spelling(tokens)

    #Remove stopwords and lemmatize
    cleaned = []
    for token in tokens:
        if token in KEEP_WORDS:
            cleaned.append(lemmatizer.lemmatize(token))
            continue
        if token not in stop_words:
            cleaned.append(lemmatizer.lemmatize(token))

    return ' '.join(cleaned)