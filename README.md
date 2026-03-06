# Movietracker 🎬

A simple Spring Boot web application to manage and track movies you have watched.

![MovieList Screenshot](screenshots/MovieList.png)

## Overview

MovieTracker is a user-friendly application that allows you to:
- Add movies to your watchlist
- Track movies you have watched
- Search for watched movies
- Update movie details
- Delete movies from your list

### Technology Stack

### Backend
- **Spring Boot** - Web Framework
- **Spring Data JPA** - ORM and Data Access
- **H2 Database** - Profile folder H2 Database
- **Thymeleaf** - Template Engine

### Frontend
- **HTML5** - Markup
- **Bootstrap** - CSS Framework
- **JavaScript** - Interactivity

### Getting Started
1. **Start the application**
    - Run the `MovietrackerApplication` class in your IDE

2. **Access the application**
   - Open your browser and navigate to: `http://localhost:8080`

### Docker
Package movietracker
```
./mvnw clean package
```
Build docker image
```
docker build -t movietracker:latest .
```
Run docker container
```
docker run --rm -p 8080:8080 movietracker:latest
```

