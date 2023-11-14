package com.sorych.gifs.controller;

import com.sorych.gifs.controller.response.GifsControllerResponse;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GifsController {

  @GetMapping("/query")
  public ResponseEntity<GifsControllerResponse> query(@RequestParam List<String> searchTerm) {
    if(searchTerm.isEmpty()) {
      return ResponseEntity.badRequest().build();
    }
    return ResponseEntity.ok(new GifsControllerResponse());
  }

}