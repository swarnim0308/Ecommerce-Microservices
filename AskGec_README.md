# AskGec - College Inquiry Chatbot

AskGec is an intelligent chatbot designed to facilitate students with instant responses to college-related queries. It leverages a pre-trained **BERT (Bidirectional Encoder Representations from Transformers)** model to understand natural language and generate context-aware answers from a custom knowledge base.

The application is built using **PyTorch** and **Flask**, featuring a responsive web interface and **Ngrok** tunneling for easy cloud access.

## 🚀 Features

-   **Context-Aware AI**: Uses a fine-tuned BERT model to understand and answer questions based on context.
-   **Custom Knowledge Base**: Easily updatable `knowledgebase.txt` to provide specific information about the college.
-   **Web Interface**: Clean and responsive chat interface built with HTML, CSS, JavaScript, and jQuery.
-   **Instant Deployment**: Integrated with `flask_ngrok` to instantly expose the local server to the web.

## 🛠️ Tech Stack

-   **Backend**: Python, Flask
-   **AI/ML**: PyTorch, Transformers (BERT)
-   **Frontend**: HTML, CSS, JavaScript, jQuery
-   **Tunneling**: Ngrok

## 📂 Project Structure

-   `server.py`: The main Flask application entry point.
-   `modeling.py`: Defines the BERT model architecture.
-   `infer.py`: Handles model inference and prediction logic.
-   `knowledgebase.txt`: Text file containing the information the bot uses to answer queries.
-   `static/`: Contains frontend assets (HTML, CSS, JS).

## 🔧 Installation & Usage

1.  **Clone the Repository**
    ```bash
    git clone https://github.com/swarnim0308/AskGec.git
    cd AskGec
    ```

2.  **Install Dependencies**
    Ensure you have Python installed. You will need the following packages:
    ```bash
    pip install flask flask-ngrok torch transformers
    ```

3.  **Update Knowledge Base**
    Edit `knowledgebase.txt` with the information you want your chatbot to know.

4.  **Run the Application**
    ```bash
    python server.py
    ```
    The application will start and provide an Ngrok URL (e.g., `http://<id>.ngrok.io`). Open this URL in your browser to interact with the chatbot.

## 🧠 How It Works

1.  The server reads the `knowledgebase.txt` file to build the context.
2.  When a user asks a question via the web interface, it is sent to the Flask backend.
3.  The `InferCoQA` model (in `infer.py`) processes the question along with the context and conversation history.
4.  The model predicts the most relevant answer span from the text.
5.  The answer is returned to the frontend and displayed to the user.

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.
