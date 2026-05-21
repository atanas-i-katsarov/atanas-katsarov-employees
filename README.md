# Employees frontend
The frontend service lives in the `frontend/` directory.

This project was generated using [Angular CLI](https://github.com/angular/angular-cli) version 21.2.11.

## Development server

To start a local development server, run:

```bash
cd frontend
ng serve
```

Once the server is running, open your browser and navigate to `http://localhost:4200/`. The application will automatically reload whenever you modify any of the source files.

## Building

To build the project run:

```bash
cd frontend
ng build
```

# Employees backend
The backend service lives in the `backend/` directory.

From the project root, run either:

```bash
cd backend
./mvnw clean install -U
./mvnw compile
./mvnw spring-boot:run
./mvnw test
```

Or invoke Maven directly from the root:

```bash
./backend/mvnw clean install -U
./backend/mvnw compile
./backend/mvnw spring-boot:run
./backend/mvnw test
```

Use `./mvnw spring-boot:run` to start the backend locally.