# GIF Search Web Service

## Description

The GIF Search Web Service allows users to search for GIFs using multiple search terms. It leverages the Giphy API to provide GIFs in response to user queries.

## Service overview

- GIF search web service with an HTTP GET API at the path `/query` that accepts multiple search terms.
    - Example: `/query?searchTerm=a&searchTerm=b&searchTerm=c`
- The service runs on port 8080 by default.
- The service utilizes the Giphy API for fetching GIFs.
- By default no more than 10 GIFs are returned by each search term.
- GIFs returned in the dataset are of equal height 200P.
- Responses are in JSON format:
```json
{
  "data": [
    {
      "search_term": "a",
      "gifs": [
        {
          "gif_id": "VekcnHOwOI5So",
          "url": "{url to .gif file here}"
        }
      ]
    },
    {
      "search_term": "b",
      "gifs": []
    }
  ]
}
```

## How to run
See the Instructions.md file for details

## Service details and limitation
- Service returns 503 code if there is an issue contacting Giphy API. 
- Service uses caching approach with the default TTL of 10 minutes to store responses.
- Service uses the dev api key for Giphy API calls so it is rate limited to 42 reads per hour and 1000 searches/API calls per day.
- Max search term length is 50 chars.
- Default max number of query params is 42 not to exceed the giphy api limitations.
- By default, the service uses max 3 threads to make calls to giphy API not to be rate-limited.

## Service parameters
```yaml
spring:
  data:
    redis:
      host: ${SPRING_DATA_REDIS_HOST:localhost}
      port: ${SPRING_DATA_REDIS_PORT:6379}
  cache:
    redis:
      time-to-live: 600000

server:
  port: 8080
  tomcat:
    max-parameter-count: 42

giphy:
  api:
    url: https://api.giphy.com/v1/gifs/search
    apiKey: add_your_key
    maxThreadsCount: 3

app:
  gifsSearchLimit: 10
  searchTermLengthLimit: 50

```
## Known issues
1. Service fails when redis is not reachable.
2. API key is stored in the app.yaml file - not safe