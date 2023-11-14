package com.sorych.gifsapp.controller;

import com.sorych.gifsapp.controller.response.GifsControllerResponse;
import com.sorych.gifsapp.controller.util.ControllerResponseBuilder;
import com.sorych.gifsapp.service.GifsService;
import com.sorych.gifsapp.service.dto.SearchResult;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GifsController {

  private GifsService gifsService;

  @Autowired
  public GifsController(GifsService gifsService) {
    this.gifsService = gifsService;
  }

  @GetMapping("/query")
  public ResponseEntity<GifsControllerResponse> query(@RequestParam List<String> searchTerm) {
    if (searchTerm.isEmpty()) {
      return ResponseEntity.badRequest().build();
    }
    List<String> uniqueTerms = searchTerm.stream().distinct().collect(Collectors.toList());
    List<SearchResult> searchResults = gifsService.findGifs(uniqueTerms);
    return ResponseEntity.ok(ControllerResponseBuilder.buildResponse(searchResults));
  }
}
