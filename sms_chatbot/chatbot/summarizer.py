import io
import re
import string
import requests
from collections import Counter


OLLAMA_URL   = "http://localhost:11434/api/generate"
OLLAMA_MODEL = "llama3.2"

STOP_WORDS = {
    'a', 'an', 'the', 'and', 'or', 'but', 'in', 'on', 'at', 'to', 'for',
    'of', 'with', 'by', 'from', 'is', 'are', 'was', 'were', 'be', 'been',
    'being', 'have', 'has', 'had', 'do', 'does', 'did', 'will', 'would',
    'could', 'should', 'may', 'might', 'shall', 'can', 'that', 'this',
    'these', 'those', 'it', 'its', 'they', 'them', 'their', 'we', 'our',
    'you', 'your', 'he', 'she', 'his', 'her', 'i', 'me', 'my', 'as',
    'if', 'not', 'no', 'so', 'up', 'out', 'about', 'into', 'than',
    'then', 'also', 'just', 'which', 'who', 'what', 'when', 'where',
    'how', 'all', 'each', 'more', 'other', 'such', 'only', 'after',
    'before', 'between', 'through', 'during', 'while', 'although',
    'however', 'therefore', 'thus', 'hence', 'because', 'since',
}


#Extract text from PDF using pdfplumber with font size filtering
def extract_text_from_pdf(file_bytes: bytes) -> str:
    import pdfplumber
    paragraphs = []
    with pdfplumber.open(io.BytesIO(file_bytes)) as pdf:
        for page in pdf.pages:
            words = page.extract_words(
                x_tolerance=3, y_tolerance=3,
                keep_blank_chars=False, use_text_flow=True,
                extra_attrs=["size"]
            )
            if not words:
                continue
            sizes = [round(w.get("size", 0)) for w in words if w.get("size", 0) > 0]
            if not sizes:
                continue
            body_size = max(set(sizes), key=sizes.count)
            body_words = [w["text"] for w in words if abs(w.get("size", 0) - body_size) <= 1.5]
            if body_words:
                paragraphs.append(" ".join(body_words))
    return "\n".join(paragraphs)


#Extract text from DOCX
def extract_text_from_docx(file_bytes: bytes) -> str:
    from docx import Document
    doc = Document(io.BytesIO(file_bytes))
    return "\n".join([p.text.strip() for p in doc.paragraphs if p.text.strip()])


#Clean raw extracted text
def preprocess_text(text: str) -> str:
    lines = text.split('\n')
    clean = []
    for line in lines:
        line = line.strip()
        if not line: continue
        if re.match(r'^\d+$', line): continue
        if line.count('.') > 5 and len(line.replace('.', '').strip()) < 20: continue
        if len(line.split()) < 4: continue
        if line.isupper(): continue
        clean.append(line)
    return ' '.join(clean)


#Merge lines and split into proper sentences
def split_into_sentences(text: str) -> list:
    text = re.sub(r'\b(Mr|Mrs|Ms|Dr|Prof|etc|i\.e|e\.g)\.',
                  lambda m: m.group().replace('.', '<DOT>'), text)
    sentences = re.split(r'(?<=[.!?])\s+', text)
    sentences = [s.replace('<DOT>', '.').strip() for s in sentences]
    return [s for s in sentences if len(s.split()) >= 8]


#Compute word frequencies
def compute_word_frequencies(sentences: list) -> dict:
    all_words = []
    for s in sentences:
        words = s.lower().translate(str.maketrans('', '', string.punctuation)).split()
        all_words.extend([w for w in words if w not in STOP_WORDS and len(w) > 2])
    freq = Counter(all_words)
    max_freq = max(freq.values()) if freq else 1
    return {w: c / max_freq for w, c in freq.items()}


#Score and rank sentences by importance
def score_sentences(sentences: list, word_freq: dict) -> list:
    scored = []
    for i, sentence in enumerate(sentences):
        words = sentence.lower().translate(str.maketrans('', '', string.punctuation)).split()
        words = [w for w in words if w not in STOP_WORDS and len(w) > 2]
        if not words:
            scored.append((0.0, i, sentence))
            continue
        score = sum(word_freq.get(w, 0) for w in words) / len(words)
        if i < max(1, len(sentences) // 5):
            score *= 1.2
        scored.append((score, i, sentence))
    return scored


#YOUR OWN extractive summarization algorithm
#Picks the most important sentences from the document
def extractive_summarize(text: str, n: int) -> list:
    sentences = split_into_sentences(text)
    if not sentences:
        return []
    if len(sentences) <= n:
        return sentences
    word_freq = compute_word_frequencies(sentences)
    scored    = score_sentences(sentences, word_freq)
    top       = sorted(scored, key=lambda x: x[0], reverse=True)[:n]
    top       = sorted(top, key=lambda x: x[1])
    return [s[2] for s in top]


#Ollama refines YOUR extracted sentences into fluent output
def refine_with_ollama(extracted_sentences: list, style: str) -> str:
    raw_summary = ' '.join(extracted_sentences)

    style_instructions = {
        'brief':    "Rewrite this as a clean concise summary in 3-5 sentences. Keep all key information.",
        'detailed': "Rewrite this as a well-structured detailed summary using clear paragraphs. Keep all key information.",
        'bullet':   "Rewrite this as a clear bullet point list. Each bullet should be one key point. Aim for 5-8 bullets.",
    }
    instruction = style_instructions.get(style, style_instructions['brief'])

    prompt = (
        f"You are helping refine an automatically extracted academic summary.\n\n"
        f"Extracted key sentences:\n{raw_summary}\n\n"
        f"{instruction}\n\n"
        f"Only use the information provided above. Do not add new information. "
        f"Do not include any preamble. Go straight into the summary."
    )

    try:
        response = requests.post(
            OLLAMA_URL,
            json={"model": OLLAMA_MODEL, "prompt": prompt, "stream": False},
            timeout=120
        )
        response.raise_for_status()
        return response.json().get("response", "").strip()
    except requests.exceptions.ConnectionError:
        #If Ollama is not running, return the raw extracted sentences
        return ' '.join(extracted_sentences)
    except Exception:
        return ' '.join(extracted_sentences)


#Public function called by app.py
def summarize_document(file_bytes: bytes, filename: str, style: str = 'brief') -> dict:
    ext = filename.rsplit('.', 1)[-1].lower() if '.' in filename else ''
    if ext not in ('pdf', 'docx'):
        raise ValueError(f'Unsupported file type ".{ext}". Only PDF and DOCX are supported.')

    #Step 1: Extract raw text (your code)
    try:
        raw_text = extract_text_from_pdf(file_bytes) if ext == 'pdf' else extract_text_from_docx(file_bytes)
        #print("\n==============================")
        #print("FILE RECEIVED:", filename)
        #print("RAW WORD COUNT:", len(raw_text.split()))
    except Exception as e:
        raise ValueError(f'Failed to read file: {str(e)}')

    if not raw_text.strip():
        raise ValueError('The file appears to be empty or contains no readable text.')

    word_count = len(raw_text.split())

    #Step 2: Clean and preprocess text (your code)
    clean_text = preprocess_text(raw_text)

    #Step 3: Extract most important sentences using your algorithm
    n = {'brief': 8, 'bullet': 10, 'detailed': 15}.get(style, 8)
    extracted_sentences = extractive_summarize(clean_text, n)
    
    #print("\n SELECTED SENTENCES:")
    #for s in extracted_sentences:
        #print("-", s)

    if not extracted_sentences:
        raise ValueError('Could not extract meaningful content from this document.')

    #Step 4: Ollama refines your extracted sentences into fluent output
    summary = refine_with_ollama(extracted_sentences, style)

    #print("\n FINAL SUMMARY:\n")
    #print(extracted_sentences)
    #print("==============================\n")

    return {
        'filename':   filename,
        'file_type':  ext,
        'style':      style,
        'word_count': word_count,
        'summary':    summary,
    }

