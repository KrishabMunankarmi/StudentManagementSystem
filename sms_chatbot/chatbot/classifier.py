import pickle
from chatbot.preprocessor import preprocess
from difflib import get_close_matches

MODEL_PATH  = 'models/chatbot_model.pkl'
LABELS_PATH = 'models/labels.pkl'

_pipeline = None
_labels   = None


def _load():
    global _pipeline, _labels
    if _pipeline is None:
        with open(MODEL_PATH,  'rb') as f: _pipeline = pickle.load(f)
        with open(LABELS_PATH, 'rb') as f: _labels   = pickle.load(f)


#Keyword map for fuzzy fallback
#If the model is not confident enough we check for keywords directly
KEYWORD_MAP = {
    'attendance':  ['attendance', 'attend', 'class', 'missed', 'present', 'absent', 'percentage'],
    'grades':      ['grade', 'grades', 'mark', 'marks', 'result', 'score', 'gpa', 'cgpa', 'performance'],
    'timetable':   ['timetable', 'schedule', 'class', 'lecture', 'time', 'today', 'tomorrow', 'when'],
    'exams':       ['exam', 'exams', 'test', 'finals', 'midterm', 'quiz', 'assessment'],
    'assignments': ['assignment', 'assignments', 'homework', 'submit', 'due', 'deadline', 'task'],
    'teacher':     ['teacher', 'lecturer', 'professor', 'instructor', 'tutor', 'who', 'teaches'],
    'fees':        ['fee', 'fees', 'payment', 'paid', 'unpaid', 'balance', 'owe', 'amount'],
    'greeting':    ['hello', 'hi', 'hey', 'help', 'morning', 'evening', 'what', 'can'],
}


def _fuzzy_fallback(cleaned_text: str) -> str:
    
    words = cleaned_text.lower().split()
    scores = {intent: 0 for intent in KEYWORD_MAP}

    for word in words:
        for intent, keywords in KEYWORD_MAP.items():
            #Exact keyword match
            if word in keywords:
                scores[intent] += 2
                continue
            #Fuzzy keyword match (catches typos that spell checker missed)
            close = get_close_matches(word, keywords, n=1, cutoff=0.8)
            if close:
                scores[intent] += 1

    best_intent = max(scores, key=scores.get)
    best_score  = scores[best_intent]

    #Only return a match if we found at least one keyword hit
    if best_score > 0:
        return best_intent

    return 'unknown'


def predict_intent(text: str, threshold: float = 0.25):
    """
    Returns (intent_tag, confidence_score).
    Uses fuzzy keyword fallback if model confidence is below threshold.
    Lowered threshold from 0.35 to 0.25 to be more flexible.
    """
    _load()
    cleaned    = preprocess(text)
    proba      = _pipeline.predict_proba([cleaned])[0]
    max_idx    = proba.argmax()
    confidence = proba[max_idx]

    #If model is confident enough use its prediction
    if confidence >= threshold:
        return _labels[max_idx], confidence

    #Otherwise try keyword fallback
    fallback = _fuzzy_fallback(cleaned)
    if fallback != 'unknown':
        return fallback, confidence

    return 'unknown', confidence