package com.sorych.gifsapp.controller;

import com.sorych.gifsapp.controller.response.GifsControllerResponse;
import com.sorych.gifsapp.controller.util.ControllerResponseBuilder;
import com.sorych.gifsapp.service.GifsService;
import com.sorych.gifsapp.service.dto.SearchResult;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GifsController {

  private GifsService gifsService;

  private int maxParameterCount;

  private int searchTermLengthLimit;

  private static final Logger logger = LoggerFactory.getLogger(GifsController.class);

  @Autowired
  public GifsController(
      GifsService gifsService,
      @Value("${server.tomcat.max-parameter-count:10000}") int maxParameterCount,
      @Value("${app.searchTermLengthLimit:50}") int searchTermLengthLimit) {
    this.gifsService = gifsService;
    this.maxParameterCount = maxParameterCount;
    this.searchTermLengthLimit = searchTermLengthLimit;
  }

  @GetMapping("/query")
  public ResponseEntity<GifsControllerResponse> query(@RequestParam List<String> searchTerm) {
    if (searchTerm.isEmpty()) {
      logger.info("SearchTerms are empty");
      return ResponseEntity.badRequest().build();
    }
    List<String> uniqueTerms = searchTerm.stream().distinct().collect(Collectors.toList());
    if (uniqueTerms.size() > maxParameterCount) {
      String msg = "received too much params, limit is " + maxParameterCount;
      logger.debug(msg);
      throw new GifsControllerBadRequestException(msg);
    }
    if (uniqueTerms.stream().anyMatch(term -> term.length() > searchTermLengthLimit)) {
      String msg = "the search term lenght limit is " + searchTermLengthLimit;
      logger.debug(msg);
      throw new GifsControllerBadRequestException(msg);
    }

    List<SearchResult> searchResults = gifsService.findGifs(uniqueTerms);
    return ResponseEntity.ok(ControllerResponseBuilder.buildResponse(searchResults));
  }

  public class GifsControllerBadRequestException extends RuntimeException {
    public GifsControllerBadRequestException(String message) {
      super(message);
    }
  }
}
