package com.sorych.gifs.controller;

import com.sorych.gifs.controller.response.GifsControllerResponse;
import com.sorych.gifs.controller.util.ControllerResponseBuilder;
import com.sorych.gifs.service.GifsService;
import com.sorych.gifs.service.dto.GifSearchResult;
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
    List<GifSearchResult> searchResults = gifsService.findGifs(uniqueTerms);
    return ResponseEntity.ok(ControllerResponseBuilder.buildResponse(searchResults));
  }
}
