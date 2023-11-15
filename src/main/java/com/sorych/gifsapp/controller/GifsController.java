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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GifsController {

  private GifsService gifsService;

  private static final Logger logger = LoggerFactory.getLogger(GifsController.class);

  @Autowired
  public GifsController(GifsService gifsService) {
    this.gifsService = gifsService;
  }

  @GetMapping("/query")
  public ResponseEntity<GifsControllerResponse> query(@RequestParam List<String> searchTerm) {
    if (searchTerm.isEmpty()) {
      logger.debug("SearchTerms are empty");
      return ResponseEntity.badRequest().build();
    }
    List<String> uniqueTerms = searchTerm.stream().distinct().collect(Collectors.toList());
    List<SearchResult> searchResults = gifsService.findGifs(uniqueTerms);
    return ResponseEntity.ok(ControllerResponseBuilder.buildResponse(searchResults));
  }
}
