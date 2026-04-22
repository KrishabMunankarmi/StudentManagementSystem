from dotenv import load_dotenv
load_dotenv()   # loads API_KEY from .env file automatically

from flask import Flask, request, jsonify, session
from flask_cors import CORS
from chatbot.classifier import predict_intent
from chatbot.response_handler import get_response
from chatbot.summarizer import summarize_document   
import os

app = Flask(__name__)
app.secret_key = os.environ.get('SECRET_KEY', 'dev-secret-change-in-prod')
CORS(app)


#Existing: Chat endpoint
@app.route('/chat', methods=['POST'])
def chat():
    data = request.get_json()

    if not data or 'message' not in data:
        return jsonify({'error': 'No message provided'}), 400

    message    = data['message'].strip()
    student_id = data.get('student_id', 1)   # replace with session auth in production

    if not message:
        return jsonify({'error': 'Empty message'}), 400

    intent, confidence = predict_intent(message)
    response = get_response(intent, student_id)

    return jsonify({
        'intent':     intent,
        'confidence': round(confidence, 4),
        'response':   response,
        'student_id': student_id,
    })


#NEW: Summarize endpoint
@app.route('/summarize', methods=['POST'])
def summarize():
  
    if 'file' not in request.files:
        return jsonify({'error': 'No file uploaded. Send the file under the key "file".'}), 400

    uploaded_file = request.files['file']
    filename      = uploaded_file.filename or ''
    style         = request.form.get('style', 'brief')

    if style not in ('brief', 'detailed', 'bullet'):
        style = 'brief'

    file_bytes = uploaded_file.read()

    try:
        result = summarize_document(file_bytes, filename, style)
    except ValueError as e:
        # Bad file type or unreadable content
        return jsonify({'error': str(e)}), 422
    except RuntimeError as e:
        # Anthropic API failure
        return jsonify({'error': str(e)}), 500

    return jsonify(result)


#Existing: Health check
@app.route('/health', methods=['GET'])
def health():
    return jsonify({'status': 'ok'})


if __name__ == '__main__':
    app.run(debug=True, port=5000)