package com.sorych.gifs.controller.response;

//  Requested JSON structure:
// {
//    "data":[
//      {
//        "search_term": "a",
//        "gifs": [
//            {
//              "gif_id": "VekcnHOwOI5So",
//              "url": "{url to .gif file here}"
//           }
//        ]
//      },
//      {
//        "search_term": "b",
//        "gifs": []
//      }
//    ]
// }

import java.util.ArrayList;
import java.util.List;

public class GifsControllerResponse {
  private List<ResponseEntity> data = new ArrayList<>();

  public List<ResponseEntity> getData() {
    return data;
  }

  public void setData(List<ResponseEntity> data) {
    this.data = data;
  }

  private class ResponseEntity {
    private String searchTerm;
    private List<GifItem> gifs = new ArrayList<>();

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

  private class GifItem {
    private String gifId;
    private String url;

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
}
