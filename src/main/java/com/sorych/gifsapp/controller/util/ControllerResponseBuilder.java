package com.sorych.gifsapp.controller.util;

import com.sorych.gifsapp.controller.response.GifItem;
import com.sorych.gifsapp.controller.response.GifSearchResponse;
import com.sorych.gifsapp.controller.response.GifsControllerResponse;
import com.sorych.gifsapp.service.dto.Gif;
import com.sorych.gifsapp.service.dto.SearchResult;
import java.util.List;
import java.util.stream.Collectors;

public class ControllerResponseBuilder {

  public static GifsControllerResponse buildResponse(List<SearchResult> searchResults) {
    GifsControllerResponse response = new GifsControllerResponse();
    List<GifSearchResponse> data =
        searchResults.stream()
            .map(ControllerResponseBuilder::toSearchResponse)
            .collect(Collectors.toList());
    response.setData(data);
    return response;
  }

  private static GifSearchResponse toSearchResponse(SearchResult searchResult) {
    List<GifItem> gifs =
        searchResult.getGifs().stream()
            .map(ControllerResponseBuilder::toGifItem)
            .collect(Collectors.toList());
    return new GifSearchResponse(searchResult.getSearchTerm(), gifs);
  }

  private static GifItem toGifItem(Gif gif) {
    return new GifItem(gif.getId(), gif.getUrl());
  }
}
