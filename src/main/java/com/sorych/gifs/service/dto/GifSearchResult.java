package com.sorych.gifs.service.dto;

import java.util.List;

public class GifSearchResult {
  private String searchTerm;

  private List<Gif> gifs;

  public String getSearchTerm() {
    return searchTerm;
  }

  public void setSearchTerm(String searchTerm) {
    this.searchTerm = searchTerm;
  }

  public List<Gif> getGifs() {
    return gifs;
  }

  public void setGifs(List<Gif> gifs) {
    this.gifs = gifs;
  }
}
