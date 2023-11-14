package com.sorych.gifsapp.controller.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

public class GifSearchResponse {
  @JsonProperty("search_term")
  private String searchTerm;

  private List<GifItem> gifs = new ArrayList<>();

  public GifSearchResponse(String searchTerm, List<GifItem> gifs) {
    this.searchTerm = searchTerm;
    this.gifs = gifs;
  }

  public GifSearchResponse() {}

  public String getSearchTerm() {
    return searchTerm;
  }

  public void setSearchTerm(String searchTerm) {
    this.searchTerm = searchTerm;
  }

  public List<GifItem> getGifs() {
    return gifs;
  }

  public void setGifs(List<GifItem> gifs) {
    this.gifs = gifs;
  }
}
