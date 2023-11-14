package com.sorych.gifs.service;

import com.sorych.gifs.service.dto.GifSearchResult;
import java.util.List;

public interface GifsService {

  List<GifSearchResult> findGifs(List<String> searchTerms);
}
