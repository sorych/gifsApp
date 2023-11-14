package com.sorych.gifsapp.service.giphy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sorych.gifsapp.service.GifsService;
import com.sorych.gifsapp.service.dto.Gif;
import com.sorych.gifsapp.service.dto.SearchResult;
import com.sorych.gifsapp.service.giphy.dto.*;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class GiphyService implements GifsService {
  private String apiUrl;
  private String apiKey;
  private Integer maxThreadsCount;
  private Integer gifsSearchLimit;
  private RestTemplate restTemplate;
  private ObjectMapper objectMapper;

  @Autowired
  public GiphyService(
      @Value("${giphy.api.url}") String apiUrl,
      @Value("${giphy.api.apiKey}") String apiKey,
      @Value("${giphy.api.maxThreadsCount}") Integer maxThreadsCount,
      @Value("${app.gifsSearchLimit}") Integer gifsSearchLimit,
      RestTemplate restTemplate,
      ObjectMapper objectMapper) {
    this.apiUrl = apiUrl;
    this.apiKey = apiKey;
    this.maxThreadsCount = maxThreadsCount;
    this.gifsSearchLimit = gifsSearchLimit;
    this.restTemplate = restTemplate;
    this.objectMapper = objectMapper;
  }

  @Override
  public List<SearchResult> findGifs(List<String> searchTerms) {
    ExecutorService executorService = Executors.newFixedThreadPool(maxThreadsCount);
    List<Future<SearchResult>> futures =
        searchTerms.stream()
            .map(searchTerm -> executorService.submit(() -> searchGifsByTerm(searchTerm)))
            .toList();
    List<SearchResult> results =
        futures.stream()
            .map(
                future -> {
                  try {
                    return future.get();
                  } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                    // todo log
                    return null;
                  }
                })
            .filter(Objects::nonNull)
            .toList();
    executorService.shutdown();
    return results;
  }

  private SearchResult searchGifsByTerm(String searchTerm) {
    SearchResult result = new SearchResult();
    result.setSearchTerm(searchTerm);
    List<Gif> gifs = Collections.emptyList();
    // TODO what if delay, service unavailable, etc
    try {
      String jsonResponse = callGiphyApi(searchTerm);
      GiphyApiResponse giphyApiResponse =
          objectMapper.readValue(jsonResponse, GiphyApiResponse.class);
      gifs = processApiCallResult(giphyApiResponse);
    } catch (RestClientException e) {
      // todo log
    } catch (JsonMappingException e) {
      // todo log
    } catch (JsonProcessingException e) {
      // todo log
    }
    result.setGifs(gifs);
    return result;
  }

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
                    .filter(Objects::nonNull)
                    .map(
                        giphyData -> {
                          Optional<String> id = Optional.of(giphyData).map(GiphyData::getId);
                          Optional<String> gifUrl =
                              Optional.of(giphyData)
                                  .map(GiphyData::getImages)
                                  .map(GiphyImages::getFixedHeight)
                                  .map(GiphyImageData::getUrl);
                          if (id.isPresent() && gifUrl.isPresent()) {
                            return new Gif(id.get(), gifUrl.get());
                          } else {
                            return null;
                          }
                        })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList()))
        .orElse(Collections.emptyList());
  }
}
