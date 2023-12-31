# Set default values for environment variables
SERVER_TOMCAT_MAX_PARAMETER_COUNT ?= 42
GIPHY_API_URL ?= "https://api.giphy.com/v1/gifs/search"
GIPHY_API_APIKEY ?= "VWk9EThkl6v5V4a9S9YfoD4RalccXT4p"
GIPHY_API_MAX_THREADS_COUNT ?= 3
APP_GIFS_SEARCH_LIMIT ?= 10

IMAGE_NAME := gifsapp:1.0.0
APP_PORT := 8080
CONTAINER_NAME := gifsapp_inst
REDIS_CONTAINER_NAME := redis-container
REDIS_PORT := 6379

build:
	./gradlew bootBuildImage

run-app:
	./gradlew bootRun

NETWORK_NAME=my-network

create-network:
	@docker network inspect $(NETWORK_NAME) > /dev/null 2>&1 || docker network create $(NETWORK_NAME)

remove-network:
	@docker network inspect $(NETWORK_NAME) > /dev/null 2>&1 && docker network rm $(NETWORK_NAME) || true

# Redis
start-redis:
	docker run --rm -d --name $(REDIS_CONTAINER_NAME) --network my-network -p $(REDIS_PORT):6379 redis:alpine

stop-redis:
	docker stop $(REDIS_CONTAINER_NAME)

# Build and run Docker container
start-app:
	./gradlew bootBuildImage
	docker run -e SERVER_TOMCAT_MAX_PARAMETER_COUNT=$(SERVER_TOMCAT_MAX_PARAMETER_COUNT) \
        -e GIPHY_API_URL=$(GIPHY_API_URL) \
        -e GIPHY_API_APIKEY=$(GIPHY_API_APIKEY) \
        -e SPRING_DATA_REDIS_PORT=$(REDIS_PORT) \
        -e SPRING_DATA_REDIS_HOST=$(REDIS_CONTAINER_NAME) \
        -e GIPHY_API_MAX_THREADS_COUNT=$(GIPHY_API_MAX_THREADS_COUNT) \
        -e APP_GIFS_SEARCH_LIMIT=$(APP_GIFS_SEARCH_LIMIT) \
        -p $(APP_PORT):8080 --name $(CONTAINER_NAME) --network my-network $(IMAGE_NAME)

stop-app:
	docker stop $(CONTAINER_NAME)
	docker rm $(CONTAINER_NAME)

start: create-network start-redis start-app

stop: stop-app stop-redis remove-network