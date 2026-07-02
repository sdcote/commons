## Architectural and Coding Style Models

### Database Access Pattern
* Do not invent new database access logic or patterns.

### Code Style and Conventions
* Mimic the naming conventions, logging configurations, and Java 26 idiomatic patterns established in other classes of this project.
* All generated code must contain complete code documentation
* When creating  new `coyote.commons.security.scanner.DetectionStrategy` implementations, do not use "DetectionStrategy" in the new class name, but use simply "Strategy" and keep the names as short as possible while still being readable.

### Unit Testing
* All generated code must contain full unit testing coverage
* All unit tests must be written in JUnit 5
