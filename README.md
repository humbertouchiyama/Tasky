# Tasky - Offline-First Task Manager

Tasky is an offline-first task management app similar to Google Calendar, designed to help users manage their events, tasks, and reminders efficiently. Built with modern Android technologies, Tasky ensures a seamless user experience even without an internet connection.

[![GitHub Stars](https://img.shields.io/github/stars/humbertouchiyama/Tasky?style=social)](https://github.com/humbertouchiyama/Tasky/stargazers)  
[![GitHub Issues](https://img.shields.io/github/issues/humbertouchiyama/Tasky)](https://github.com/humbertouchiyama/Tasky/issues)  
[![License](https://img.shields.io/badge/License-MIT-green.svg)](https://opensource.org/licenses/MIT)

---

## Features

- **Authentication**: Secure login and registration system.
- **SplashScreen**: Redirects users to the appropriate screen based on their login status.
- **Home Screen**: Displays Events, Reminders, and Tasks in an organized manner.
- **Event Management**:
  - Create, update, and delete events.
  - Add details like title, time, photos, and attendees.
- **Task and Reminder Management**:
  - Create, update, and delete tasks and reminders.
- **Offline-First Design**:
  - Save data locally using Room Database.
  - Sync with the API when online.
- **Push Notifications**: Receive timely reminders for events, tasks and reminders.
- **Background Sync**: Uses WorkManager and AlarmManager for reliable background synchronization.
- **Modern UI**: Built with Jetpack Compose for a sleek and responsive user interface.

---

## Technologies Used

- **Kotlin**: Primary programming language.
- **Architecture**: MVVM/MVI with Clean Architecture and SOLID principles.
- **Coroutines**: Used for asynchronous programming and managing background tasks.
- **Flow**: For reactive streams and state management.
- **UI**: Jetpack Compose for modern, declarative UI development.
- **Networking**: Retrofit for API communication.
- **Database**: Room for local data persistence.
- **Dependency Injection**: Hilt for simplified dependency management.
- **Image Loading**: Coil for efficient image loading.
- **Background Tasks**: WorkManager and AlarmManager for scheduling and syncing.
- **Navigation**: Compose Navigation for seamless in-app navigation.

---
