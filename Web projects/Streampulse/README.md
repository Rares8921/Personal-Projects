# Streampulse

## Description

Streampulse is a web application designed to provide users with detailed information about movies and TV shows using the TheMovieDB API. The application features user authentication, a search functionality, and a detailed view for individual movies and TV shows. Testing for the API integration was performed using Postman to ensure reliability and performance.

## Features

- **User Authentication**: Secure login and registration system.
- **Search Functionality**: Users can search for movies and TV shows by title.
- **Movie/TV Show Details**: Provides detailed information including title, description, rating, and more.
- **Responsive Design**: Optimized for various screen sizes.

## Structure

### JavaScript (Node.js)

- **Server Setup**: Utilizes Express.js to set up the server and handle routes.
- **API Integration**: Fetches movie and TV show data from TheMovieDB API.
- **User Authentication**: Uses Passport.js for user authentication and session management.

### Views

- **EJS Templates**: The views are rendered using Embedded JavaScript (EJS) templates.
- **Responsive Layout**: Ensures a user-friendly experience on both desktop and mobile devices.

### Environment Variables

- **Configuration**: Uses a `.env` file to manage environment variables securely.

## Usage

1. **Install Dependencies**: Run `npm install` to install the necessary dependencies.
2. **Set Up Environment Variables**: Configure the `.env` file with your API key and other settings.
3. **Start the Server**: Run `npm start` to start the server.
4. **Access the Application**: Open your web browser and go to `http://localhost:3000`.

## Example

Here is an example of how the search results page might look:

![Streampulse Example](example.png)

## Time and Complexity Analysis

### Time Complexity

- **Search Functionality**: The time complexity for searching movies and TV shows using TheMovieDB API is O(1) for fetching data, as the API call returns results in constant time.
- **User Authentication**: The time complexity for user authentication operations (login, registration) is O(1) for database operations if the user data is indexed properly.

### Space Complexity

- **Server Setup**: The space complexity for the server is O(n), where n is the number of concurrent users, as each user session requires memory.
- **Search Results**: The space complexity for storing search results is O(k), where k is the number of results returned by TheMovieDB API.

## License

This code is proprietary and may not be copied, distributed, or modified without express written permission from the author.