package com.sorych.gifs.service;

import com.sorych.gifs.service.dto.GifSearchResult;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class GiphyService implements GifsService {

  @Override
  public List<GifSearchResult> findGifs(List<String> searchTerms) {
    return Collections.emptyList();
  }
}
