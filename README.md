# AnnotationApp

A web application for managing text annotation tasks, built with Spring Boot and Thymeleaf.

## Description

This project allows administrators to assign text pair annotation tasks to annotators. Annotators can then work on these tasks via a dedicated interface. The app includes features such as:

- Managing annotators and annotation tasks
- Assigning tasks in batches based on available unassigned text pairs
- Viewing task assignments and status
- REST endpoints for task assignment operations
- Thymeleaf templates for admin pages

## Technologies Used

- Java 17+
- Spring Boot
- Spring MVC (Controllers)
- Spring Data JPA (Repositories)
- Thymeleaf (View Templates)
- Maven or Gradle (Build Tool)
- H2 / MySQL / PostgreSQL (Database)

## Features

- Display all annotators and their details
- Calculate and display unassigned pairs for task assignment
- Assign annotation tasks automatically to selected annotators
- REST API for assigning tasks with JSON request/response
- Error handling with informative messages

## Getting Started

### Prerequisites

- JDK 17 or higher
- Maven or Gradle
- Database setup (H2, MySQL, or PostgreSQL)
- Git (optional)

### Setup

1. Clone the repository

```bash
git clone https://github.com/yourusername/AnnotationApp.git
cd AnnotationApp
