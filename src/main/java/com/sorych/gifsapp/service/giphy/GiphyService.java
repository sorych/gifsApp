package com.sorych.gifsapp.service.giphy;

import com.sorych.gifsapp.service.GifsService;
import com.sorych.gifsapp.service.dto.SearchResult;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GiphyService implements GifsService {
  private Integer maxThreadsCount;
  private GiphyApiCaller giphyApiCaller;

  private static final Logger logger = LoggerFactory.getLogger(GiphyService.class);

  @Autowired
  public GiphyService(
      @Value("${giphy.api.maxThreadsCount}") Integer maxThreadsCount,
      RestTemplate restTemplate,
      GiphyApiCaller giphyApiCaller) {
    this.maxThreadsCount = maxThreadsCount;
    this.giphyApiCaller = giphyApiCaller;
  }

  @Override
  public List<SearchResult> findGifs(List<String> searchTerms) {
    ExecutorService executorService = Executors.newFixedThreadPool(maxThreadsCount);
    List<Future<SearchResult>> futures =
        searchTerms.stream()
            .map(
                searchTerm ->
                    executorService.submit(() -> giphyApiCaller.searchGifsByTerm(searchTerm)))
            .toList();
    List<SearchResult> results =
        futures.stream()
            .map(
                future -> {
                  try {
                    return future.get();
                  } catch (InterruptedException | ExecutionException e) {
                    logger.error(e.toString());
                    return null;
                  }
                })
            .filter(Objects::nonNull)
            .toList();
    executorService.shutdown();
    return results;
  }
}
