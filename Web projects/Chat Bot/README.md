# Chat Bot

<div align="center">

![HTML5](https://img.shields.io/badge/HTML5-E34F26?style=for-the-badge&logo=html5&logoColor=white)
![CSS3](https://img.shields.io/badge/CSS3-1572B6?style=for-the-badge&logo=css3&logoColor=white)
![JavaScript](https://img.shields.io/badge/JavaScript-F7DF1E?style=for-the-badge&logo=javascript&logoColor=black)
![Bootstrap](https://img.shields.io/badge/Bootstrap-7952B3?style=for-the-badge&logo=bootstrap&logoColor=white)
![jQuery](https://img.shields.io/badge/jQuery-0769AD?style=for-the-badge&logo=jquery&logoColor=white)

**A clean, responsive command-driven chatbot interface built with vanilla web technologies. No backend required, just pure frontend interaction.**

[Features](#what-it-does) • [Tech Stack](#tech-stack) • [Quick Start](#getting-started)

</div>

---

## What It Does

This is a lightweight chatbot interface that processes user commands through a clean, modern UI. Perfect for building command-line style bots, customer support interfaces, or interactive help systems.

**The good stuff:**
- Fully client-side operation - no server needed for basic functionality
- Command-based interaction system for structured user input
- Clean, responsive Bootstrap 4 UI that works on any device
- Animated input fields with visual feedback
- Ready for API integration (connect to OpenAI, DialogFlow, or your custom NLP backend)
- Built-in conversation history display

**What makes it different:**
- Zero dependencies on heavy frameworks - just HTML, CSS, and vanilla JavaScript
- Lightweight and fast - loads instantly
- Easy to customize and extend with your own command handlers
- Can be integrated with any NLP service via simple API calls
- Mobile-first responsive design

The interface is production-ready and can be dropped into any web project. Just connect your preferred NLP backend (OpenAI, DialogFlow, LUIS, etc.) and you're good to go.

---

## Tech Stack

**Frontend:** HTML5 + CSS3 + Vanilla JavaScript  
**UI Framework:** Bootstrap 4.3.1 + jQuery 3.3.1  
**Icons:** Font Awesome (optional integration)  
**Backend Integration:** Ready for REST API calls (fetch/axios)

### Architecture

Simple client-side architecture - perfect for static hosting:

```
User Interface (Bootstrap)
         ↓
JavaScript Event Handlers
         ↓
Command Parser (client-side logic)
         ↓
API Integration Layer (optional)
         ↓
NLP Service (OpenAI/DialogFlow/Custom)
```

**How it works:**
- User types commands in the styled input field
- JavaScript captures and processes the input
- Commands are parsed client-side or sent to backend API
- Responses are dynamically rendered in the chat interface
- Conversation history persists in the DOM (can be extended to localStorage)

**Integration Options:**
- **OpenAI API:** Stream GPT responses directly in the chat
- **DialogFlow:** Connect to Google's conversational AI
- **Custom Backend:** Simple fetch() calls to your own API
- **Local Processing:** Handle simple commands entirely client-side

---

## Project Structure

```
Chat Bot/
├── index.html           # Main chat interface
├── README.md           # This file
├── a.png               # Bot avatar image
└── sss.png             # Application icon
```

The entire chatbot is a single HTML file with embedded CSS and JavaScript. For production use, consider splitting into separate files and adding a backend API for NLP processing.

---

## Getting Started

**What you need:**
A modern web browser. That's it.

**Setup (30 seconds):**

```bash
# 1. Clone or download the project
cd "d:\Personal-Projects\Web projects\Chat Bot"

# 2. Open in browser
start index.html
```

**Or use a local server for development:**

```bash
# Python 3
python -m http.server 8000

# Node.js
npx http-server -p 8000
```

Then open `http://localhost:8000` in your browser.

**Integrating with an API:**

Add this JavaScript to connect to an NLP service:

```javascript
async function sendMessage(userMessage) {
    const response = await fetch('https://api.openai.com/v1/chat/completions', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer YOUR_API_KEY'
        },
        body: JSON.stringify({
            model: 'gpt-3.5-turbo',
            messages: [{role: 'user', content: userMessage}]
        })
    });
    const data = await response.json();
    return data.choices[0].message.content;
}
```

---

## What's Next

**Planned improvements:**
- Add conversation persistence with localStorage
- Implement typing indicators and message animations
- Add support for rich media (images, links, buttons)
- Include voice input/output with Web Speech API
- Build backend integration examples for popular NLP services
- Add multi-language support
- Implement chat export functionality

**Want to contribute?**
Feel free to fork and submit PRs. Focus areas: API integrations, UI improvements, accessibility features.

---

## Deployment

**Static Hosting (Free):**
- **Netlify:** Drag and drop the folder, instant deployment
- **Vercel:** `vercel deploy` - done in 10 seconds
- **GitHub Pages:** Push to repo, enable Pages, live in 1 minute
- **AWS S3 + CloudFront:** Static hosting with global CDN

**With Backend API:**
- Deploy the frontend to static hosting (above)
- Host your API on Heroku, Railway, or AWS Lambda
- Update API endpoints in the JavaScript

**Docker (optional):**

```dockerfile
FROM nginx:alpine
COPY . /usr/share/nginx/html
EXPOSE 80
```

```bash
docker build -t chatbot .
docker run -p 8080:80 chatbot
```

---

## 📹 Demo Video

> **Recording Instructions:** A 3-5 minute walkthrough showcasing the key features of this project.

### What to Demonstrate

**Suggested Timeline:**
- **0:00-0:30** - Project overview and startup
- **0:30-2:00** - Core features demonstration
- **2:00-3:30** - Advanced features and interactions
- **3:30-5:00** - Edge cases and wrap-up

### Features to Showcase

- **Chat Interface** - Clean, responsive Bootstrap UI
- **Command Processing** - Demonstrate various command inputs
- **Real-time Responses** - Client-side command handling
- **API Integration** - (If connected) Show OpenAI/DialogFlow responses
- **Conversation History** - Display of chat message flow
- **Mobile Responsiveness** - Show UI on different screen sizes

### Recording Setup

**Prerequisites:**
```bash
# No installation needed - runs in browser
# Simply open index.html in your browser
```

**OBS Studio Settings:**
- Resolution: 1920x1080 (1080p)
- FPS: 30
- Format: MP4 (H.264)
- Audio: Include microphone narration (optional)

**Steps:**
1. Open `index.html` in your browser
2. Open OBS Studio and set up screen capture
3. Record the demonstration following the timeline above
4. Save video as `demo.mp4` in the project root directory
5. (Optional) Upload to YouTube and update README with embed link

### Quick Demo Commands

```bash
# Navigate to project directory
cd "d:\Personal-Projects\Web projects\Chat Bot"

# Open in default browser
start index.html

# Or use a local server
python -m http.server 8000
# Then open http://localhost:8000/index.html
```

**Video file:** Once recorded, save as `demo.mp4` in this directory.

---

## License

**Proprietary Software** - All rights reserved.

This software is provided for personal use and evaluation only. No license is granted for commercial use, modification, or distribution without explicit written permission from the author.

For licensing inquiries, please contact the repository owner.