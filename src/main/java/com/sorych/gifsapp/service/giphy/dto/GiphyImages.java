package com.sorych.gifsapp.service.giphy.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GiphyImages {

  @JsonProperty("fixed_height")
  private GiphyImageData fixedHeight;

  public GiphyImageData getFixedHeight() {
    return fixedHeight;
  }

  public void setFixedHeight(GiphyImageData fixedHeight) {
    this.fixedHeight = fixedHeight;
  }
}