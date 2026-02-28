# Streampulse

<div align="center">

![Node.js](https://img.shields.io/badge/Node.js-339933?style=for-the-badge&logo=node.js&logoColor=white)
![Express](https://img.shields.io/badge/Express-000000?style=for-the-badge&logo=express&logoColor=white)
![EJS](https://img.shields.io/badge/EJS-B4CA65?style=for-the-badge&logo=ejs&logoColor=black)
![Passport.js](https://img.shields.io/badge/Passport.js-34E27A?style=for-the-badge&logo=passport&logoColor=white)
![Bcrypt](https://img.shields.io/badge/Bcrypt-003A70?style=for-the-badge&logo=letsencrypt&logoColor=white)

**A movie streaming platform with user authentication, personal watchlists, and TMDB API integration. Your own Netflix-style experience.**

[Features](#what-it-does) • [Tech Stack](#tech-stack) • [Quick Start](#getting-started)

</div>

---

## What It Does

This is a full-featured movie streaming platform built with Node.js and Express. It integrates with The Movie Database (TMDB) API to provide a massive catalog of movies with details, trailers, and ratings.

**The good stuff:**
- **Movie Catalog:** Browse thousands of movies with posters, ratings, and release dates
- **User Authentication:** Secure registration/login system with bcrypt password hashing
- **Personal Watchlists:** Save movies to your watchlist for later viewing
- **Movie Details:** View comprehensive information (cast, trailers, images, ratings)
- **User Reviews:** Read and write reviews for movies
- **Session Management:** Passport.js for robust authentication sessions
- **Responsive Design:** Clean UI that works on desktop, tablet, and mobile
- **Protected Routes:** Watchlist and account features require authentication
- **Flash Messages:** User feedback for actions (login success, errors, etc.)
- **Persistent Storage:** User accounts and watchlists saved to JSON files

**What makes it different:**
- Production-ready authentication with industry-standard bcrypt and Passport.js
- Real-time TMDB API integration for up-to-date movie data
- Secure password handling (hashed with bcrypt, never stored in plaintext)
- Session-based authentication with middleware protection
- Scalable architecture - easy to swap JSON storage for a real database
- Built-in flash messaging for better UX

The platform runs on Express 4 with Passport.js for authentication and EJS for server-side rendering. Ready to be extended with video streaming capabilities (HLS/DASH) and a proper database.

---

## Tech Stack

**Backend:** Node.js + Express 4.21  
**Templating:** EJS (Embedded JavaScript)  
**Authentication:** Passport.js + Passport-Local + bcrypt  
**Session Management:** express-session + express-flash  
**External API:** The Movie Database (TMDB) API  
**Storage:** JSON files (upgradeable to MongoDB/PostgreSQL)  
**Environment:** dotenv for configuration

### Architecture

Classic Express MVC architecture with session-based authentication:

```
Client (Browser)
       ↓
Express Routes (GET/POST endpoints)
       ↓
Passport.js Middleware (authentication)
       ↓
Route Handlers (business logic)
       ↓
TMDB API Integration (fetch movie data)
       ↓
JSON Storage (user accounts, watchlists)
       ↓
EJS Templates (server-side rendering)
```

**Authentication Flow:**

```
User Registration
       ↓
Password Hashing (bcrypt, 10 rounds)
       ↓
Store in accounts.json {email, hashedPassword, watchlist}
       ↓
Passport.js Session Creation
       ↓
Session Cookie (secure, httpOnly)
       ↓
Protected Route Access (checkAuthenticated middleware)
```

**How the core features work:**

**User Authentication:**
- Passwords hashed with bcrypt (10 salt rounds) before storage
- Passport-local strategy validates credentials against stored hashes
- Sessions persist across requests via express-session
- Middleware functions (`checkAuthenticated`, `checkNotAuthenticated`) protect routes

**Watchlist Management:**
1. User adds movie to watchlist (authenticated route)
2. Server reads `accounts.json`, finds user by email
3. Appends movie ID to user's watchlist array
4. Writes updated data back to `accounts.json`
5. On watchlist page load, fetch full movie details from TMDB for each ID
6. Render EJS template with movie data

**TMDB API Integration:**
```javascript
// Fetch movie details
fetch(`https://api.themoviedb.org/3/movie/${movieId}?api_key=${apiKey}`)
    .then(res => res.json())
    .then(movie => {
        // Extract poster, title, rating, release date, etc.
        // Render in EJS template
    });
```

**Route Protection:**
```javascript
function checkAuthenticated(req, res, next) {
    if (req.isAuthenticated()) return next();
    res.redirect('/account');
}
```

---

## Project Structure

```
Streampulse/
├── index.js                 # Main Express application
├── passport-config.js       # Passport.js strategy configuration
├── package.json            # Dependencies and scripts
├── .env                    # Environment variables (API keys, secrets)
├── views/
│   ├── index.ejs           # Homepage
│   ├── about.ejs           # About page
│   ├── movielist.ejs       # Browse movies
│   ├── detail.ejs          # Movie details page
│   ├── watchlist.ejs       # User's saved movies
│   ├── reviews.ejs         # Movie reviews
│   ├── account.ejs         # Login/register
│   ├── js/
│   │   └── accounts.json   # User storage (JSON)
│   └── ... (other assets)
└── scratch/                # Development files
```

**Key Files:**
- **index.js:** Express server, routes, middleware, authentication setup
- **passport-config.js:** Passport.js local strategy for username/password auth
- **accounts.json:** User data (email, hashed password, watchlist movie IDs)

---

## Getting Started

**What you need:**
- Node.js 14+ and npm
- TMDB API key (free at https://www.themoviedb.org/settings/api)

**Setup (5 minutes):**

```bash
# 1. Navigate to project
cd "d:\Personal-Projects\Web projects\Streampulse"

# 2. Install dependencies
npm install

# 3. Create .env file with your credentials
# Add these to .env:
# SESSION_SECRET=your_random_secret_here
# TMDB_API_KEY=your_tmdb_api_key_here

# 4. Update API key in index.js
# Line 12: const apiKey = "YOUR_TMDB_API_KEY";

# 5. Run the server
npm start
# Or for development with auto-reload:
npx nodemon index.js
```

**Access the platform:**
- Main app: `http://localhost:4000`
- Browse movies, create account, build your watchlist

**First time setup:**
1. Go to `http://localhost:4000/account`
2. Register a new account with email and password
3. Login with your credentials
4. Browse movies at `/movielist`
5. Click "Add to Watchlist" on any movie
6. View your watchlist at `/watchlist`

**Environment Variables:**
Create a `.env` file in the project root:
```env
SESSION_SECRET=a_very_long_random_string_here
NODE_ENV=development
```

---

## API Integration

**The Movie Database (TMDB):**

Get your free API key:
1. Sign up at https://www.themoviedb.org/
2. Go to Settings → API
3. Request an API key (Developer)
4. Copy the API Key (v3 auth)
5. Add to `index.js` line 12 or use environment variable

**Example API calls used:**
```javascript
// Get movie details
GET https://api.themoviedb.org/3/movie/{movie_id}?api_key={key}&append_to_response=casts,videos,images,releases

// Search movies
GET https://api.themoviedb.org/3/search/movie?api_key={key}&query={search}

// Discover movies
GET https://api.themoviedb.org/3/discover/movie?api_key={key}&sort_by=popularity.desc
```

---

## Authentication Details

**Password Security:**
- Passwords never stored in plaintext
- Bcrypt hashing with 10 salt rounds (industry standard)
- Constant-time comparison prevents timing attacks

**Session Security:**
- express-session with secure cookie storage
- Session secret from environment variable
- Sessions invalidate on logout
- `httpOnly` cookies prevent XSS attacks

**Registration:**
```javascript
app.post('/register', async (req, res) => {
    const hashedPassword = await bcrypt.hash(password, 10);
    users.push({ id, email, password: hashedPassword, watchlist: [] });
    // Save to accounts.json
});
```

**Login:**
```javascript
app.post('/login', passport.authenticate('local', {
    successRedirect: '/main/',
    failureRedirect: '/account',
    failureFlash: true
}));
```

---

## What's Next

**Planned improvements:**
- Replace JSON storage with MongoDB or PostgreSQL
- Add actual video streaming (HLS/DASH with ffmpeg)
- Implement movie recommendations based on watchlist
- Add social features (share watchlists, friend system)
- Build advanced search and filtering
- Add user profiles with avatars
- Implement rating system (star ratings)
- Add TV shows support (TMDB has TV API)
- Build admin panel for content management
- Add email verification on registration
- Implement "Continue Watching" feature with playback progress
- Add multi-language subtitle support

**Want to contribute?**
The codebase is standard Express.js - easy to understand. Focus areas: database migration, video streaming, UI/UX improvements.

---

## Deployment

**Heroku:**

```bash
# 1. Create Heroku app
heroku create streampulse

# 2. Set environment variables
heroku config:set SESSION_SECRET=your_secret
heroku config:set TMDB_API_KEY=your_tmdb_key

# 3. Deploy
git push heroku main

# 4. Open app
heroku open
```

**Railway:**

```bash
# 1. Install Railway CLI
npm install -g @railway/cli

# 2. Login and deploy
railway login
railway init
railway up
```

**Docker:**

```dockerfile
FROM node:18-alpine
WORKDIR /app
COPY package*.json ./
RUN npm ci --production
COPY . .
EXPOSE 4000
ENV NODE_ENV=production
CMD ["node", "index.js"]
```

```bash
docker build -t streampulse .
docker run -p 4000:4000 \
  -e SESSION_SECRET=your_secret \
  -e TMDB_API_KEY=your_key \
  streampulse
```

**Environment Variables for Production:**
- `SESSION_SECRET` - Long random string for session encryption
- `TMDB_API_KEY` - Your TMDB API key
- `NODE_ENV` - Set to `production`
- `PORT` - Server port (default: 4000)

**Database Migration (Recommended for Production):**

Replace JSON storage with MongoDB:

```javascript
// Install: npm install mongoose
const mongoose = require('mongoose');
mongoose.connect(process.env.MONGODB_URI);

const UserSchema = new mongoose.Schema({
    email: String,
    password: String,
    watchlist: [Number]
});
const User = mongoose.model('User', UserSchema);
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

- **Movie Catalog Browsing** - Explore TMDB movie collection with posters and ratings
- **User Authentication** - Register account, secure login with bcrypt
- **Movie Details** - View comprehensive movie info, trailers, cast
- **Personal Watchlist** - Add/remove movies to watchlist
- **User Reviews** - Read and write movie reviews
- **Search Functionality** - Find specific movies

### Recording Setup

**Prerequisites:**
```bash
# Install dependencies
npm install

# Create .env file with TMDB API key
echo "TMDB_API_KEY=your_api_key_here" > .env
echo "SESSION_SECRET=random_secret_string" >> .env

# Start the application
npm start
```

**OBS Studio Settings:**
- Resolution: 1920x1080 (1080p)
- FPS: 30
- Format: MP4 (H.264)
- Audio: Include microphone narration (optional)

**Steps:**
1. Start the application: `npm start`
2. Open OBS Studio and set up screen capture
3. Navigate to `http://localhost:3000`
4. Record the demonstration following the timeline above
5. Save video as `demo.mp4` in the project root directory
6. (Optional) Upload to YouTube and update README with embed link

### Quick Demo Commands

```bash
# Start application
cd "d:\Personal-Projects\Web projects\Streampulse"
npm start

# Access the app
# Open browser to http://localhost:3000
```

**Video file:** Once recorded, save as `demo.mp4` in this directory.

---

## License

**Proprietary Software** - All rights reserved.

This software is provided for personal use and evaluation only. No license is granted for commercial use, modification, or distribution without explicit written permission from the author.

For licensing inquiries, please contact the repository owner.