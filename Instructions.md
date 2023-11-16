# Gifs Service Instructions

This document provides instructions on how to build, run tests, and start the service on port 8080.

## Build

1. Ensure you have Docker installed on your machine.
2. Open a terminal and navigate to the project directory.
3. Leverage Makefile:

* to build, run tests and start the service in docker available on port 8080 use
```bash
make start
```
* to build the app locally use
```bash
make build
```
* to change the app port call with param like:
```bash
make APP_PORT=8080 start
```
* to run app locally (docker still used for Redis)
```bash
make run-app
```
* to stop both containers
```bash
make stop
```

4. Test the app by making GET calls to localhost:
  
```bash
 localhost:8080/query?searchTerm=a&searchTerm=b
```

## More info
Read more details about the gifsApp in the README file

## Production Ready Improvements

1. **Update GIPHY API Key to production:**
   - read more at https://developers.giphy.com/docs/api/#quick-start-guide
2. **Secret Manager for API Key:**
   - Utilize a secret manager or a secure storage solution for managing sensitive data.
   - Consider using Kubernetes Secrets, HashiCorp Vault, or a cloud provider's secret management service.
3. **Service Authentication:**
   - Implement robust authentication mechanisms like API keys, JWT, or OAuth, depending on your use case and security requirements.
   - Ensure proper validation and authorization for incoming requests.
4. **Distributed Cache:**
   - If scaling horizontally, consider using a distributed cache solution or a Redis cluster to ensure consistent caching across multiple instances.
5. **Logging Enhancements:**
   - Configure log formats, log levels, and integrate with a centralized logging system.
   - Utilize structured logging for better log analysis and debugging.
6. **Pagination Mechanism:**
   - Implement pagination to efficiently handle large result sets.
   - Provide clients with control over the amount of data returned.
7. **Health Check Endpoint:**
   - Implement a health-check endpoint to monitor the system's status.
   - Include checks for external dependencies like the GIPHY endpoint.
8. **Two-Step Docker Image Building:**
   - Use a multi-stage Docker build to minimize the size of the final image.
   - The first stage can include building the application, and the second stage can involve copying only necessary artifacts.
