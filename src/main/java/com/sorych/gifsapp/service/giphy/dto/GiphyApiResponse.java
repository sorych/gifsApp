package com.sorych.gifsapp.service.giphy.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GiphyApiResponse {

  private List<GiphyData> data;

  public List<GiphyData> getData() {
    return data;
  }

  public void setData(List<GiphyData> data) {
    this.data = data;
  }
}

