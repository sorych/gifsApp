package com.sorych.gifsapp.controller.response;

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

  public GifsControllerResponse() {
  }

  public GifsControllerResponse(List<GifSearchResponse> data) {
    this.data = data;
  }

  private List<GifSearchResponse> data = new ArrayList<>();

  public List<GifSearchResponse> getData() {
    return data;
  }

  public void setData(List<GifSearchResponse> data) {
    this.data = data;
  }

}
