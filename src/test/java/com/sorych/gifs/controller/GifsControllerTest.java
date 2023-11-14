package com.sorych.gifs.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sorych.gifs.controller.response.GifsControllerResponse;
import com.sorych.gifs.service.GifsService;
import com.sorych.gifs.service.dto.Gif;
import com.sorych.gifs.service.dto.GifSearchResult;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@SpringBootTest
@AutoConfigureMockMvc
public class GifsControllerTest {
  @Autowired private MockMvc mvc;
  @Autowired private ObjectMapper objectMapper;

  @MockBean private GifsService gifsService;

  @Test
  public void testShouldReturnJson() throws Exception {
    List<String> searchParams = Arrays.asList("a", "b");
    MultiValueMap<String, String> params = prepareQueryParams(searchParams);

    ResultActions result = mvc.perform(MockMvcRequestBuilders.get("/query").params(params));

    result.andExpect(status().isOk());
    result.andExpect(content().contentType(MediaType.APPLICATION_JSON));

    GifsControllerResponse response =
        objectMapper.readValue(
            result.andReturn().getResponse().getContentAsString(), GifsControllerResponse.class);

    assert response.getData() != null;

    response
        .getData()
        .forEach(
            gifResponse -> {
              assert gifResponse.getSearchTerm() != null;
              assert gifResponse.getGifs() != null;
            });
    verify(gifsService).findGifs(searchParams);
  }

  @Test
  public void testNoQueryParamsShouldReturnBadRequest() throws Exception {
    mvc.perform(MockMvcRequestBuilders.get("/query")).andExpect(status().isBadRequest());
  }

  @Test
  public void testDuplicatedQueryParamsShouldReturnSingleResult() throws Exception {
    String searchTerm = "a";
    List<String> searchTerms = Arrays.asList(searchTerm, searchTerm, searchTerm);
    MultiValueMap<String, String> params = prepareQueryParams(searchTerms);
    ResultActions result = mvc.perform(MockMvcRequestBuilders.get("/query").params(params));

    List<String> uniqueTerms = searchTerms.stream().distinct().collect(Collectors.toList());
    when(gifsService.findGifs(uniqueTerms))
        .thenReturn(createMockResponseForSearchTerms(uniqueTerms));
    result.andExpect(status().isOk());
    result.andExpect(content().contentType(MediaType.APPLICATION_JSON));

    GifsControllerResponse response =
        objectMapper.readValue(
            result.andReturn().getResponse().getContentAsString(), GifsControllerResponse.class);

    assert response.getData() != null;
    assert response.getData().size() == 1;
    assert response.getData().get(0).getSearchTerm().equals(searchTerm);
    assert response.getData().get(0).getGifs() != null;
  }

  private MultiValueMap<String, String> prepareQueryParams(List<String> searchTerms) {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    searchTerms.forEach(s -> params.add("searchTerm", s));

    when(gifsService.findGifs(searchTerms))
        .thenReturn(createMockResponseForSearchTerms(searchTerms));
    return params;
  }

  private List<GifSearchResult> createMockResponseForSearchTerms(List<String> terms) {
    List<GifSearchResult> results = new ArrayList<>();
    for (String term : terms) {
      GifSearchResult searchResult = new GifSearchResult();
      searchResult.setSearchTerm(term);
      List<Gif> gifs = new ArrayList<>();
      Gif gif = new Gif("SomeGifId", "SomeUrl");
      gifs.add(gif);
      searchResult.setGifs(gifs);
      results.add(searchResult);
    }
    return results;
  }
}
