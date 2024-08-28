# RoomMaster Backend

![CI Badge](https://github.com/AhmadAddas/RoomMaster-Backend/actions/workflows/maven.yml/badge.svg?branch=main)

## Overview

RoomMaster is a conference room booking system built with a Dockerized Spring Boot application, JWT, and PostgreSQL database. It features a robust API for managing room bookings, maintenance schedules, and user authentication.

# Features

- **Autogenerate Sample Data**: Automatically generates conference rooms Amaze, Beauty, Inspire, and Strive and assign maintenance timings (9:00-9:15 AM, 1:00-1:15 PM, 5:00-5:15 PM) for them for an instant run.
- **Same-Day Bookings:** Allows bookings only for the current server day.
- **Overlapping Bookings Handling:** Rejects bookings that overlap with existing ones.
- **Maintenance Time Checks:** Prevents bookings during room maintenance periods.
- **Validation Checks:** Ensures valid time inputs and adheres to room capacity.
- **Transaction Management:** Ensures consistency in managing maintenance times.
- **Dockerized Setup:** Plug-and-play installation with Docker and Docker Compose.
- **Great Coverage and Unit Tested**: The application includes extensive unit tests, ensuring high code quality and reliable functionality.
- **Edge Cases Handled:**
  1. **Race Conditions:** Implemented pessimistic locking to prevent race conditions.
  2. **Overlapping Bookings:** Checks for existing bookings before confirming a new one.
  3. **Overlapping with Maintenance Times:** Cross-references booking times with maintenance schedules.
  4. **Invalid Time Inputs:** Ensures bookings are made in 15-minute intervals with valid start and end times.
  5. **Booking on the Boundary of Maintenance Time:** Prevents overlaps with maintenance times.
  6. **Booking Across Midnight:** Restricts bookings to within the same day.
  7. **Last-Minute Bookings:** Enforces a minimum lead time of 15 minutes for bookings.
  8. **Deletion of Maintenance Times:** Checks for existing bookings before allowing deletion.
  9. **Multiple Admins Managing Maintenance Times:** Implements transaction management for consistency.
  10. **User Cancels Booking Last Minute:** Implements a cancellation policy preventing last-minute cancellations.
  11. **Handling Maximum Capacity:** Enforces room capacity limits.
  12. **Long-Running Bookings:** Prevents long-running bookings from overlapping with maintenance times or blocking other users.
  
# Future Features Plan

1. Limit API access to prevent backend overload.
2. Allow users to access only their bookings instead of admin/manager access.
3. Use the UserDetailsService in a manually instantiated DaoAuthenticationProvide to address the AuthenticationProvider bean warning.
4. Address the warnings that occur only on the first run for DB constrains.
5. Hide certain user details in responses.
6. Create a sample frontend with OpenAPI docs.
7. Align standard timezone for consistent bookings across timezones so it won't conflict with frontend timezone.
8. Make the application more containerized for microservices architecture.
9. Create different profiles like dev and Amazon profile for deployment on Amazon and other providers.

## Known Bugs

1. Booking other users' rooms with another user's bearer token.
2. Public registration allows setting role as ADMIN or MANAGER.
3. Double creation of room/maintenance times with the same name, capacity, and times not in 15-minute intervals.
4. endTime can be set before startTime in room maintenance times, leading to invalid time intervals.
5. Need to use other dependency packages to resolve security vulnerabilities for the old dependency packages.


# Setup Instructions

## Prerequisites

- **Docker**: Install Docker by following the instructions for your OS below.
- **Docker Compose**: Install Docker Compose as part of Docker Desktop on Windows and Mac, or manually on Linux.

# Installation Instructions

## Docker Installation

- **Windows and Mac:**
<br> <br>
  - Download and install Docker Desktop from the [Docker website](https://www.docker.com/products/docker-desktop).
  <br><br>
- **Linux (Debian):**
<br> <br>
  - Add Docker's official GPG key & add it to sources:
     ```bash
    sudo apt-get update
    sudo apt-get install ca-certificates curl
    sudo install -m 0755 -d /etc/apt/keyrings
    sudo curl -fsSL https://download.docker.com/linux/debian/gpg -o /etc/apt/keyrings/docker.asc
    sudo chmod a+r /etc/apt/keyrings/docker.asc
    echo \
      "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.asc] https://download.docker.com/linux/debian \
      $(. /etc/os-release && echo "$VERSION_CODENAME") stable" | \
      sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
    sudo apt-get update
    ```
  - Install Docker using the package manager:
    ```bash
    sudo apt-get update
    sudo apt-get install docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin
    ```
  - Install Docker Compose v2.29.1:
    ```bash
    curl -SL https://github.com/docker/compose/releases/download/v2.29.1/docker-compose-linux-x86_64 -o /usr/local/bin/docker-compose
    sudo chmod +x /usr/local/bin/docker-compose
    sudo ln -s /usr/local/bin/docker-compose /usr/bin/docker-compose
    ```
    

## Running the Application

1. Clone the repository:
   ```bash
   git clone https://github.com/AhmadAddas/RoomMaster-Backend.git
   cd RoomMaster-Backend
   ```

2. Ensure the `.env` file is in `src/main/resources/.env` is unmodified and correctly set up with the appropriate environment variables (Sample is provided for convenience).

3. Build and run the Docker containers:
   ```bash
   docker-compose up --build
   ```

4. The application will be available at `http://localhost:8081`.
   <br><br>

# API Documentation

This section provides examples of how to interact with the RoomMaster API using Postman or any other HTTP client.
<br><br>
## Authentication
<br>

### Register a User (PUBLIC)
**Endpoint:** `POST /api/v1/auth/register`

**Example Request:**
```json
{
  "firstname": "Joe",
  "lastname": "Doe",
  "email": "joe@roommaster.ae",
  "password": "password",
  "role": "ADMIN"
}
```

<br>

### Authenticate a User (PUBLIC)
**Endpoint:** `POST /api/v1/auth/authenticate`

**Example Request:**
```json
{
  "email": "joe@roommaster.ae",
  "password": "password"
}
```

<br>

### Change Password (ADMIN OR MANAGER OR USER)
**Endpoint:** `PATCH /api/v1/users`

**Headers:**
- `Authorization: Bearer {{auth-token}}`

**Example Request:**
```json
{
  "currentPassword": "password",
  "newPassword": "newPassword",
  "confirmationPassword": "newPassword"
}
```

<br>

## User Management

<br>

### Query All Users (ADMIN OR MANAGER)
**Endpoint:** `GET /api/v1/users`

**Headers:**
- `Authorization: Bearer {{auth-token}}`

<br>

### Query Specific User by ID (ADMIN OR MANAGER)
**Endpoint:** `GET /api/v1/users/{userID}`

**Headers:**
- `Authorization: Bearer {{auth-token}}`

<br>

## Room Management

<br>

### Query All Rooms (PUBLIC)
**Endpoint:** `GET /api/v1/rooms/`

<br>

### Query Specific Room by ID (PUBLIC)
**Endpoint:** `GET /api/v1/rooms/{roomID}`

<br>

### Create a New Room without Maintenance Time (You can add if you want) (ADMIN OR MANAGER)
**Endpoint:** `POST /api/v1/rooms`

**Headers:**
- `Authorization: Bearer {{auth-token}}`

**Example Request:**
```json
{
  "name": "Conference Room A",
  "capacity": 20
}
```

<br>

### Delete a Room by ID (ADMIN OR MANAGER)
**Endpoint:** `DELETE /api/v1/rooms/{roomID}`

**Headers:**
- `Authorization: Bearer {{auth-token}}`

<br>

## Booking Management

<br>

### Create a Booking (ADMIN OR MANAGER OR USER)
**Endpoint:** `POST /api/v1/bookings`

**Headers:**
- `Authorization: Bearer {{auth-token}}`

**Example Request:**
```json
{
  "roomId": 1,
  "userId": 1,
  "startTime": "2024-08-28T10:00:00",
  "endTime": "2024-08-28T12:00:00",
  "numberOfPeople": 5
}
```

<br>

### Query Booking by ID (ADMIN OR MANAGER)
**Endpoint:** `GET /api/v1/bookings/{bookingID}`

**Headers:**
- `Authorization: Bearer {{auth-token}}`

<br>

### Query All Bookings (ADMIN OR MANAGER)
**Endpoint:** `GET /api/v1/bookings`

**Headers:**
- `Authorization: Bearer {{auth-token}}`

<br>

### Query All Bookings from a Specific User by ID (ADMIN OR MANAGER)
**Endpoint:** `GET /api/v1/bookings/user/{userID}`

**Headers:**
- `Authorization: Bearer {{auth-token}}`

<br>

### Query Available Rooms for Booking (PUBLIC)
**Endpoint:** `GET /api/v1/bookings/available-rooms`

**Query Parameters with example data:**
- `startTime=2024-08-28T16:00:00`
- `endTime=2024-08-28T16:15:00`

**Example Request:**

``GET http://localhost:8081/api/v1/bookings/available-rooms?startTime=2024-08-28T16:00:00&endTime=2024-08-28T16:15:00``

<br>

## Maintenance Time Management

<br>

### Query Maintenance Time for a Room by ID (PUBLIC)
**Endpoint:** `GET /api/v1/maintenance-times/room/{roomID}`


<br>

### Create Maintenance Time for a Room (ADMIN OR MANAGER)
**Endpoint:** `POST /api/v1/maintenance-times/add`

**Headers:**
- `Authorization: Bearer {{auth-token}}`

**Query Parameters with example data:**
- `room=1`
- `startTime=18:00`
- `endTime=18:15`

**Example Request:**

``POST http://localhost:8081/api/v1/maintenance-times/add?room=1&startTime=18:00&endTime=18:15``


<br>

### Delete Maintenance Time by ID (ADMIN OR MANAGER)
**Endpoint:** `DELETE /api/v1/maintenance-times/remove/{maintenanceTimeID}`

**Headers:**
- `Authorization: Bearer {{auth-token}}`

<br>

## Miscellaneous

<br>

### Query the Demo Hidden Endpoint from Public (ADMIN OR MANAGER OR USER)
**Endpoint:** `GET /api/v1/demo-controller`

**Headers:**
- `Authorization: Bearer {{auth-token}}`


<br>

# Testing with IntelliJ IDEA Ultimate
1. Open the `RESTfulApiTest.http` file in [IntelliJ IDEA Ultimate](https://www.jetbrains.com/idea/).
2. Right-click on the file and select "Run" to execute the HTTP requests.
3. Note that it might fail due to time differences as booking cannot be in the past.


# Additional Information

- The backend uses port `8081` & `5430` by default to avoid conflict with existing Spring Boot application & PostgreSQL service in host machine.
- A sample `.env` file is provided for plug-and-play installation.

# License
This project is licensed under the Apache License v2 - see the [LICENSE](LICENSE) file for details.

# Contact
For any inquiries or issues, please make a PR request.
