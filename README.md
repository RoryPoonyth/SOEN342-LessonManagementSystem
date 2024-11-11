# SOEN-342 Sections H and II: Software Requirements and Deployment Fall 2024

## Team 5 Members

- **Rory Poonyth**  
  - Student ID: 40226938  
  - Email: rory.poonyth@gmail.com  
  - Section: H

- **Desire Ouattara**  
  - Student ID: 40136181  
  - Email: desire_ouattara@hotmail.ca  
  - Section: II

## Project Overview

This project was created as part of the SOEN-342 course requirements for Software Requirements and Deployment in Fall 2024. It focuses on the design, requirements gathering, and deployment of a software solution that meets specific requirements provided during the course.

## Features

- **User Management**: Allows administrators to create, update, and delete users, including clients, instructors, and dependents.
- **Booking System**: Clients can book lessons for themselves or their dependents, while instructors can view and manage their lesson offerings.
- **Lesson Management**: Administrators can create, update, or delete lessons based on the requirements.
- **Location Management**: Administrators can manage the locations where lessons are offered, including adding and removing locations.

## Technologies Used

- **Java**: Primary programming language for implementing the backend operations.
- **SQLite**: Used as the database for storing user, lesson, booking, and location information.
- **Scanner**: Used for user input handling in a console-based interface.
- **Collections**: Java collections such as `List` for managing users, bookings, lessons, and locations.

## Installation

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/yourusername/soen342-team5.git
   ```

2. **Navigate to the Project Directory**:
   ```bash
   cd soen342-team5
   ```

3. **Compile the Project**:
   Use your preferred IDE or compile via command line:
   ```bash
   javac -d bin src/com/Team5/operations/*.java
   ```

4. **Run the Project**:
   ```bash
   java -cp bin com.Team5.operations.Main
   ```

## Usage

The application can be run in a console environment, allowing users to interactively create, modify, and manage lessons, bookings, users, and locations. The user interface is menu-driven, prompting users for input based on the roles they are assigned.

- **Administrator**: Manages locations, lessons, and users.
- **Client**: Registers dependents and creates bookings.
- **Instructor**: Views and manages their assigned lessons.

## Demo Video

[![Demo Video](https://img.youtube.com/vi/qav7BYgqjI)/0.jpg)](https://www.youtube.com/watch?v=qav7BYgqjI))

*Note: Replace `VIDEO_ID` with the actual YouTube video ID once the demo video is uploaded.*

## Contribution Guidelines

Team members actively collaborated on the design, development, and testing of the project.
- **Code Contribution**: Contributions were made by each team member to implement different aspects of the project, such as user management, lesson management, and booking system.
- **Testing**: Each feature was thoroughly tested by the team to ensure smooth functionality and compliance with requirements.

## Contact
For further information or questions, feel free to reach out to any of the team members at the provided emails.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

