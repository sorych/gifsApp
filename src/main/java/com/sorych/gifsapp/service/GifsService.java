package com.sorych.gifsapp.service;

import com.sorych.gifsapp.service.dto.SearchResult;
import java.util.List;

public interface GifsService {

  List<SearchResult> findGifs(List<String> searchTerms);
}
