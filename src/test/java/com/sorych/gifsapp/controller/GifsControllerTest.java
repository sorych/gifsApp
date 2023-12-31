package com.sorych.gifsapp.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sorych.gifsapp.controller.response.GifsControllerResponse;
import com.sorych.gifsapp.service.GifsService;
import com.sorych.gifsapp.service.dto.Gif;
import com.sorych.gifsapp.service.dto.SearchResult;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@SpringBootTest
@ActiveProfiles("test")
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
    assertThat(response.getData()).isNotNull();

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
    List<String> uniqueTerms = searchTerms.stream().distinct().collect(Collectors.toList());
    when(gifsService.findGifs(uniqueTerms))
        .thenReturn(createMockResponseForSearchTerms(uniqueTerms));
    ResultActions result = mvc.perform(MockMvcRequestBuilders.get("/query").params(params));
    result.andExpect(status().isOk());
    result.andExpect(content().contentType(MediaType.APPLICATION_JSON));

    GifsControllerResponse response =
        objectMapper.readValue(
            result.andReturn().getResponse().getContentAsString(), GifsControllerResponse.class);

    assertThat(response.getData()).isNotNull();
    assertThat(response.getData().size()).isEqualTo(1);
    assertThat(response.getData().get(0).getSearchTerm()).isEqualTo(searchTerm);
    assertThat(response.getData().get(0).getGifs()).isNotNull();
  }

  private MultiValueMap<String, String> prepareQueryParams(List<String> searchTerms) {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    searchTerms.forEach(s -> params.add("searchTerm", s));

    when(gifsService.findGifs(searchTerms))
        .thenReturn(createMockResponseForSearchTerms(searchTerms));
    return params;
  }

  private List<SearchResult> createMockResponseForSearchTerms(List<String> terms) {
    List<SearchResult> results = new ArrayList<>();
    for (String term : terms) {
      SearchResult searchResult = new SearchResult();
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
