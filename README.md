# Study Journey

Study Journey is a Java Swing desktop application for organizing academic progress and career-development activities. It combines module and skill tracking, task scheduling, activity logs, and position/application records in one interface.

This repository is a cleaned archival version of a university software-engineering team project. Generated classes, packaged binaries, IDE metadata, and generated Javadoc from the original archive are intentionally excluded.

## Features

- Manage modules, grades, credits, GPA values, and associated skills
- Create dated tasks and view reminders on a calendar
- Record activities with dates, places, descriptions, and notes
- Record positions with start and end dates, descriptions, and notes
- Store application data locally between sessions
- Navigate the main features through a Java Swing interface

## Technology

- Java 17
- Java Swing
- Maven
- LGoodDatePicker
- JUnit 5

## Build and run

Requirements:

- JDK 17 or newer
- Apache Maven 3.8 or newer

Build the executable JAR:

```bash
mvn clean package
```

Run the application:

```bash
java -jar target/java-study-journey-1.0.0.jar
```

The application creates local data files in the working directory as records are added. These runtime files are ignored by Git.

## Repository structure

```text
src/main/java/test/  # Application and data-model classes
src/test/java/test/  # Original unit tests
pom.xml              # Maven dependencies and build configuration
```

The original source used the package name `test`; it is retained here to avoid changing the archived application beyond the basic cleanup needed for publication.

## Project context

This was developed using an Agile team workflow for a software-engineering course. The repository describes functionality verifiable in the archived source code and does not assign unverified individual ownership of particular components.

