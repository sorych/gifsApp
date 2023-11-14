package com.sorych.gifs.controller.util;

import com.sorych.gifs.controller.response.GifItem;
import com.sorych.gifs.controller.response.GifSearchResponse;
import com.sorych.gifs.controller.response.GifsControllerResponse;
import com.sorych.gifs.service.dto.Gif;
import com.sorych.gifs.service.dto.GifSearchResult;
import java.util.List;
import java.util.stream.Collectors;

public class ControllerResponseBuilder {

  public static GifsControllerResponse buildResponse(List<GifSearchResult> searchResults) {
    GifsControllerResponse response = new GifsControllerResponse();
    List<GifSearchResponse> data =
        searchResults.stream()
            .map(ControllerResponseBuilder::toSearchResponse)
            .collect(Collectors.toList());
    response.setData(data);
    return response;
  }

  private static GifSearchResponse toSearchResponse(GifSearchResult searchResult) {
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
