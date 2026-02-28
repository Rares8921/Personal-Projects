# MicroSocial Platform

<div align="center">

![C#](https://img.shields.io/badge/C%23-239120?style=for-the-badge&logo=c-sharp&logoColor=white)
![ASP.NET Core](https://img.shields.io/badge/ASP.NET%20Core-512BD4?style=for-the-badge&logo=.net&logoColor=white)
![Entity Framework](https://img.shields.io/badge/Entity%20Framework-512BD4?style=for-the-badge&logo=.net&logoColor=white)
![SignalR](https://img.shields.io/badge/SignalR-68217A?style=for-the-badge&logo=.net&logoColor=white)
![SQL Server](https://img.shields.io/badge/SQL%20Server-CC2927?style=for-the-badge&logo=microsoft-sql-server&logoColor=white)
![ML.NET](https://img.shields.io/badge/ML.NET-512BD4?style=for-the-badge&logo=.net&logoColor=white)

**A feature-complete social media platform built from scratch with ASP.NET Core. Think Twitter meets Instagram, but you control the entire stack.**

[Features](#what-it-does) • [Tech Stack](#tech-stack) • [Quick Start](#getting-started)

</div>

---

## What It Does

This is a full-featured social networking platform that rivals mainstream apps. Built with enterprise-grade ASP.NET Core, it handles everything from real-time chat to AI-powered content moderation.

**The good stuff:**
- **Social Features:** Posts, comments, likes, follower system, user profiles, discovery feed
- **Real-time Communication:** Direct messaging, group chats, chatrooms via SignalR WebSockets
- **Stories:** 24-hour ephemeral content (auto-cleanup with hosted background service)
- **Notifications:** Real-time push notifications for all social interactions
- **Content Moderation:** ML.NET profanity detection middleware (automatic content filtering)
- **Authentication:** ASP.NET Identity with email verification + Google OAuth integration
- **Search Engine:** Full-text search across users, posts, and content
- **Privacy Controls:** Follow/unfollow, saved posts, notification preferences, content reports
- **Inbox System:** Consolidated message center for all conversations

**What makes it different:**
- Production-ready with proper architecture (MVC + service layer + dependency injection)
- Real-time everything - WebSockets for instant updates without page refreshes
- AI-powered content moderation using ML.NET trained models
- Enterprise authentication with OAuth 2.0 and JWT-style sessions
- Background services for automated cleanup (stories expire after 24 hours)
- Built-in reporting and moderation tools
- Fully responsive design with modern UI/UX

The platform runs on ASP.NET Core 6+ with SQL Server, fully containerized and ready for Azure deployment.

---

## Tech Stack

**Backend:** ASP.NET Core 6+ (C#) + Entity Framework Core  
**Database:** SQL Server (with EF Core migrations)  
**Authentication:** ASP.NET Identity + Google OAuth 2.0  
**Real-time:** SignalR WebSockets  
**AI/ML:** ML.NET (profanity detection)  
**Email:** SendGrid API integration  
**Frontend:** Razor Views + Bootstrap + JavaScript  
**Deployment:** Docker + Azure App Service ready

### Architecture

Classic ASP.NET Core MVC with service-oriented architecture:

```
Presentation Layer (Razor Views + SignalR Hubs)
            ↓
Controllers (23+ endpoints: Auth, Posts, Chat, etc.)
            ↓
Services Layer (ChatroomService, GroupChatService, etc.)
            ↓
Middleware (Profanity Detection, Last Active Tracking)
            ↓
Entity Framework Core (DbContext + Migrations)
            ↓
SQL Server (normalized schema with foreign keys)
```

**How the core features work:**

**Real-time Communication:**
- SignalR hubs for WebSocket connections
- Clients subscribe to chatroom/group channels
- Server broadcasts messages to all connected users
- Automatic reconnection handling

**Content Moderation:**
- ML.NET model trained on profanity datasets
- Custom middleware intercepts all POST requests
- Text content analyzed in real-time
- Flagged content blocked before database insertion

**Stories System:**
- Background hosted service runs on timer
- Scoped service cleans up 24-hour-old stories
- Dependency injection pattern for service lifetime management

**Authentication Flow:**
```
User Registration → Email Verification (SendGrid)
                 ↓
         ASP.NET Identity
                 ↓
    OAuth 2.0 (Google) OR Local Auth
                 ↓
         Session Management
```

---

## Project Structure

```
MicroSocial Platform/
├── Controllers/              # 23+ MVC controllers
│   ├── AuthController.cs     # Registration, login, email verification
│   ├── PostController.cs     # CRUD for posts
│   ├── CommentController.cs  # Nested comments
│   ├── LikeController.cs     # Post likes
│   ├── FollowController.cs   # Follow/unfollow system
│   ├── ChatroomController.cs # Public chatrooms
│   ├── GroupChatController.cs # Private group chats
│   ├── MessageController.cs  # Direct messages
│   ├── StoryController.cs    # 24-hour stories
│   ├── NotificationController.cs
│   ├── ProfileController.cs  # User profiles
│   ├── SearchEngineController.cs
│   ├── ReportController.cs   # Content moderation
│   └── ... (11+ more)
├── Models/                   # 25+ domain entities
│   ├── User.cs              # Extended IdentityUser
│   ├── Post.cs, Comment.cs, Like.cs
│   ├── Chatroom.cs, GroupChat.cs, ChatMessage.cs
│   ├── Story.cs, Notification.cs
│   ├── Follower.cs, Report.cs
│   └── ...
├── Services/                # Business logic layer
│   ├── ChatroomService.cs
│   ├── GroupChatService.cs
│   ├── StoryCleanupService.cs
│   ├── EmailSender.cs (SendGrid)
│   └── StoryCleanupHostedService.cs
├── Migrations/              # EF Core database migrations
├── Views/                   # Razor templates
├── wwwroot/                 # Static assets (CSS, JS, images)
├── AppContext.cs           # EF Core DbContext
├── Program.cs              # Application startup
├── WebSocketConfig.cs      # SignalR hubs configuration
├── ProfanityDetectionMiddleware.cs
├── UpdateLastActive.cs     # Activity tracking middleware
├── EmailSender.cs          # SendGrid integration
└── appsettings.json        # Configuration

MLModel.* files             # ML.NET trained model
```

---

## Getting Started

**What you need:**
- .NET 6+ SDK
- SQL Server (LocalDB, Express, or full version)
- Visual Studio 2022 or VS Code with C# extension
- SendGrid API key (for email verification)
- Google OAuth credentials (optional, for social login)

**Setup (10 minutes):**

```bash
# 1. Clone and navigate
cd "d:\Personal-Projects\Web projects\MicroSocial Platform"

# 2. Configure connection string
# Edit appsettings.json - update DefaultConnection to your SQL Server

# 3. Configure SendGrid (required for email verification)
# Edit appsettings.json - add your SendGrid API key

# 4. Optional: Configure Google OAuth
# Add ClientId and ClientSecret in Program.cs

# 5. Apply database migrations
dotnet ef database update

# 6. Run the application
dotnet run
```

**Or run from Visual Studio:**
1. Open `MicroSocial Platform.csproj`
2. Update connection strings in `appsettings.json`
3. Press F5 to run

**Access the platform:**
- HTTPS: `https://localhost:7283`
- HTTP: `http://localhost:5000`

**First time setup:**
1. Register a new account at `/Identity/Account/Register`
2. Check your email for verification link (check spam folder)
3. Verify email and login
4. Start posting, following users, and chatting

**Database seeding:**
The `SeedData.Initialize()` method runs on startup and creates initial data if the database is empty.

---

## Key Features Explained

**Posts & Social Interaction:**
- Create, edit, delete posts with rich text
- Nested comment threads
- Like/unlike functionality with real-time counters
- Save posts to personal collection

**Follower System:**
- Follow/unfollow users
- View follower and following lists
- Personalized discovery feed based on activity

**Real-time Chat:**
- **Chatrooms:** Public channels anyone can join
- **Group Chats:** Private groups with join requests
- **Direct Messages:** One-on-one conversations
- All powered by SignalR for instant delivery

**Stories:**
- Upload photos/videos visible for 24 hours
- Automatic cleanup via background service
- View active stories from followed users

**Notifications:**
- Real-time notifications for likes, comments, follows
- Inbox integration for messages
- Customizable notification preferences

**Content Moderation:**
- ML.NET model detects profanity automatically
- User reporting system
- Admin moderation dashboard (extendable)

---

## What's Next

**Planned improvements:**
- Add Redis caching for better performance
- Implement infinite scroll for feeds
- Add video/image processing with Azure Media Services
- Build native mobile apps with Xamarin/MAUI
- Add Elasticsearch for advanced search
- Implement rate limiting and API throttling
- Add admin dashboard for content moderation
- Integrate Azure Blob Storage for media files
- Add two-factor authentication (2FA)
- Implement GraphQL API alongside REST

**Want to contribute?**
The codebase follows standard ASP.NET Core patterns. Focus areas: performance optimization, UI/UX improvements, additional social features.

---

## Deployment

**Azure App Service (Recommended):**

```bash
# 1. Create Azure resources
az group create --name MicroSocialRG --location eastus
az sql server create --name microsocial-sql --resource-group MicroSocialRG
az sql db create --name MicroSocialDB --server microsocial-sql
az appservice plan create --name MicroSocialPlan --resource-group MicroSocialRG
az webapp create --name microsocial --plan MicroSocialPlan

# 2. Deploy
dotnet publish -c Release
az webapp deployment source config-zip --src ./publish.zip
```

**Docker Deployment:**

```dockerfile
FROM mcr.microsoft.com/dotnet/aspnet:6.0 AS base
WORKDIR /app
EXPOSE 80 443

FROM mcr.microsoft.com/dotnet/sdk:6.0 AS build
WORKDIR /src
COPY ["MicroSocial Platform.csproj", "."]
RUN dotnet restore
COPY . .
RUN dotnet build -c Release -o /app/build

FROM build AS publish
RUN dotnet publish -c Release -o /app/publish

FROM base AS final
WORKDIR /app
COPY --from=publish /app/publish .
ENTRYPOINT ["dotnet", "MicroSocial Platform.dll"]
```

```bash
docker build -t microsocial .
docker run -p 8080:80 -e "ConnectionStrings__DefaultConnection=YOUR_SQL_CONNECTION" microsocial
```

**Environment Variables:**
- `ConnectionStrings__DefaultConnection` - SQL Server connection string
- `SendGrid__ApiKey` - SendGrid API key for emails
- `Authentication__Google__ClientId` - Google OAuth client ID
- `Authentication__Google__ClientSecret` - Google OAuth secret

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

- **User Registration & Login** - Email verification, Google OAuth integration
- **Post Creation & Interaction** - Create posts, like, comment, share
- **Real-time Chat** - Direct messages and group chat via SignalR
- **Stories System** - Create 24-hour ephemeral stories
- **User Profiles** - View profiles, follow/unfollow users
- **Content Moderation** - ML.NET profanity detection in action

### Recording Setup

**Prerequisites:**
```bash
# Set up SQL Server connection string in appsettings.json
# Configure SendGrid API key for emails
# Set up Google OAuth credentials (optional)

# Apply database migrations
dotnet ef database update

# Start the application
dotnet run
```

**OBS Studio Settings:**
- Resolution: 1920x1080 (1080p)
- FPS: 30
- Format: MP4 (H.264)
- Audio: Include microphone narration (optional)

**Steps:**
1. Start the application: `dotnet run`
2. Open OBS Studio and set up screen capture
3. Navigate to `http://localhost:5000`
4. Record the demonstration following the timeline above
5. Save video as `demo.mp4` in the project root directory
6. (Optional) Upload to YouTube and update README with embed link

### Quick Demo Commands

```bash
# Start application
cd "d:\Personal-Projects\Web projects\MicroSocial Platform"
dotnet run

# Access the app
# Open browser to http://localhost:5000
```

**Video file:** Once recorded, save as `demo.mp4` in this directory.

---

## License

**Proprietary Software** - All rights reserved.

This software is provided for personal use and evaluation only. No license is granted for commercial use, modification, or distribution without explicit written permission from the author.

For licensing inquiries, please contact the repository owner.
