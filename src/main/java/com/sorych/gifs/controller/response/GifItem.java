package com.sorych.gifs.controller.response;

public class GifItem {
  private String gifId;
  private String url;

  public GifItem(String gifId, String url) {
    this.gifId = gifId;
    this.url = url;
  }

  public String getGifId() {
    return gifId;
  }

  public void setGifId(String gifId) {
    this.gifId = gifId;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }
}
