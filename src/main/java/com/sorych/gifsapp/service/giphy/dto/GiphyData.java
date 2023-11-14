package com.sorych.gifsapp.service.giphy.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GiphyData {

  private String id;

  @JsonProperty("images")
  private GiphyImages images;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public GiphyImages getImages() {
    return images;
  }

  public void setImages(GiphyImages images) {
    this.images = images;
  }
}
