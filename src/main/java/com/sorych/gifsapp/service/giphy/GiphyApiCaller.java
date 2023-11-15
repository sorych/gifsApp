package com.sorych.gifsapp.service.giphy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sorych.gifsapp.service.dto.Gif;
import com.sorych.gifsapp.service.dto.SearchResult;
import com.sorych.gifsapp.service.giphy.dto.GiphyApiResponse;
import com.sorych.gifsapp.service.giphy.dto.GiphyData;
import com.sorych.gifsapp.service.giphy.dto.GiphyImageData;
import com.sorych.gifsapp.service.giphy.dto.GiphyImages;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class GiphyApiCaller {
  private String apiUrl;
  private String apiKey;
  private Integer gifsSearchLimit;
  private RestTemplate restTemplate;

  private ObjectMapper objectMapper;
  private static final Logger logger = LoggerFactory.getLogger(GiphyApiCaller.class);

  @Autowired
  public GiphyApiCaller(
      @Value("${giphy.api.url}") String apiUrl,
      @Value("${giphy.api.apiKey}") String apiKey,
      @Value("${app.gifsSearchLimit}") Integer gifsSearchLimit,
      RestTemplate restTemplate,
      ObjectMapper objectMapper) {
    this.apiUrl = apiUrl;
    this.apiKey = apiKey;
    this.gifsSearchLimit = gifsSearchLimit;
    this.restTemplate = restTemplate;
    this.objectMapper = objectMapper;
  }

  @Cacheable(value = "gifs", key = "#searchTerm", sync = true)
  public SearchResult searchGifsByTerm(String searchTerm) {
    SearchResult result = new SearchResult();
    result.setSearchTerm(searchTerm);
    List<Gif> gifs = Collections.emptyList();
    try {
      String jsonResponse = callGiphyApi(searchTerm);
      GiphyApiResponse giphyApiResponse =
          objectMapper.readValue(jsonResponse, GiphyApiResponse.class);
      gifs = processApiCallResult(giphyApiResponse);
    } catch (RestClientException | JsonProcessingException e) {
      // TODO no return - unhealthy!
      logger.error("search for " + searchTerm + " error:" + e);
    }
    result.setGifs(gifs);
    return result;
  }

  @CacheEvict(value = "gifs", key = "#searchTerm")
  public void evictCache(String searchTerm) {}

  @Retryable(
      value = {HttpClientErrorException.class, HttpServerErrorException.class},
      maxAttempts = 3,
      backoff = @Backoff(random = true, delay = 1000, maxDelay = 5000, multiplier = 2))
  private String callGiphyApi(String searchTerm) {
    String url = apiUrl + "?api_key=" + apiKey + "&q=" + searchTerm + "&limit=" + gifsSearchLimit;
    return restTemplate.getForObject(url, String.class);
  }

  private List<Gif> processApiCallResult(GiphyApiResponse giphyApiResponse) {
    return Optional.ofNullable(giphyApiResponse)
        .map(GiphyApiResponse::getData)
        .map(
            data ->
                data.stream()
                    .flatMap(giphyData -> createGifFromData(giphyData).stream())
                    .collect(Collectors.toList()))
        .orElse(Collections.emptyList());
  }

  private Optional<Gif> createGifFromData(GiphyData giphyData) {
    return Optional.of(giphyData)
        .map(GiphyData::getId)
        .flatMap(
            id ->
                Optional.of(giphyData)
                    .map(GiphyData::getImages)
                    .map(GiphyImages::getFixedHeight)
                    .map(GiphyImageData::getUrl)
                    .map(url -> new Gif(id, url)));
  }
}
