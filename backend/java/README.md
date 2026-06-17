# Backend Java

This backend project uses an embedded H2 database for persistence.

## Requirements

- Java 17 JDK
- Maven

## Build

From `backend/java`:

```bash
mvn compile
```

## Run

```bash
mvn exec:java
```

## Notes

- The H2 database files are created under `backend/java/data`.
- If you do not have `mvn` installed, import the project into an IDE such as IntelliJ IDEA or Eclipse as a Maven project.
- The main class is `com.example.backend.App`.
