package com.sorych.gifsapp.service.giphy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sorych.gifsapp.service.GifsService;
import com.sorych.gifsapp.service.dto.SearchResult;
import com.sorych.gifsapp.service.giphy.util.GiphyApiCaller;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

@Service
public class GiphyService implements GifsService {
  private Integer maxThreadsCount;
  private GiphyApiCaller giphyApiCaller;

  private static final Logger logger = LoggerFactory.getLogger(GiphyService.class);

  @Autowired
  public GiphyService(
      @Value("${giphy.api.maxThreadsCount}") Integer maxThreadsCount,
      GiphyApiCaller giphyApiCaller) {
    this.maxThreadsCount = maxThreadsCount;
    this.giphyApiCaller = giphyApiCaller;
  }

  @Override
  public List<SearchResult> findGifs(List<String> searchTerms) {
    ExecutorService executorService = Executors.newFixedThreadPool(maxThreadsCount);

    List<CompletableFuture<SearchResult>> futures =
        searchTerms.stream()
            .map(
                searchTerm ->
                    CompletableFuture.supplyAsync(
                            () -> {
                              try {
                                return giphyApiCaller.searchGifsByTerm(searchTerm);
                              } catch (JsonProcessingException e) {
                                logger.error(
                                    "JsonProcessingException during CompletableFuture: " + e);
                                throw new RuntimeException("Error during giphyApiCaller call", e);
                              }
                            },
                            executorService)
                        .exceptionally(
                            ex -> {
                              logger.error("Exception during CompletableFuture: " + ex.toString());
                              if (ex.getCause() instanceof RestClientException) {
                                throw new RestClientException(ex.getCause().getMessage());
                              }
                              throw new RuntimeException("Error during giphyApiCaller call", ex);
                            }))
            .toList();

    List<SearchResult> results =
        futures.stream()
            .map(CompletableFuture::join) // Throws exception if any occurred during execution
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    executorService.shutdown();
    return results;
  }
}
