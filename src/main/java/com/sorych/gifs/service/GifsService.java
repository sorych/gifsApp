package com.sorych.gifs.service;

import com.sorych.gifs.service.dto.GifSearchResult;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class GifsService {

  public List<GifSearchResult> findGifs(List<String> searchTerms) {
    return Collections.emptyList();
  }
}
