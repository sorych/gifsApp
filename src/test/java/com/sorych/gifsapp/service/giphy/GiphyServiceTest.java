package com.sorych.gifsapp.service.giphy;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sorych.gifsapp.service.dto.Gif;
import com.sorych.gifsapp.service.dto.SearchResult;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.client.RestTemplate;

@SpringBootTest(classes = GiphyService.class)
@AutoConfigureWebClient
class GiphyServiceTest {

  @Autowired private GiphyService giphyService;

  @Autowired private ObjectMapper objectMapper;

  @MockBean private RestTemplate restTemplate;

  @Autowired private ResourceLoader resourceLoader;

  @Value("${app.gifsSearchLimit}")
  Integer gifsSearchLimit;

  @Value("${giphy.api.url}")
  String apiUrl;

  @Value("${giphy.api.apiKey}")
  String apiKey;

  @Test
  void testFindGifs() throws IOException {
    String jsonResponse =
        new String(
            resourceLoader
                .getResource("classpath:giphy-response.json")
                .getInputStream()
                .readAllBytes(),
            StandardCharsets.UTF_8);
    String searchTerm = "term1";

    String expectedUri =
        apiUrl + "?api_key=" + apiKey + "&q=" + searchTerm + "&limit=" + gifsSearchLimit;

    when(restTemplate.getForObject(eq(expectedUri), eq(String.class))).thenReturn(jsonResponse);

    List<String> searchTerms = List.of(searchTerm);
    List<SearchResult> result = giphyService.findGifs(searchTerms);
    assert result.size() == searchTerms.size();
    SearchResult gifSearchResult = result.get(0);
    assert gifSearchResult.getSearchTerm().equals(searchTerm);
    assert gifSearchResult.getGifs().size() == 3;

    List<String> receivedIds = gifSearchResult.getGifs().stream().map(Gif::getId).toList();
    List<String> expectedIds =
        List.of("hryis7A55UXZNCUTNA", "k5m2Gwn83tHo08dNRg", "3oD3YJDA4I6J4PmvpS");
    assertThat(expectedIds, Matchers.containsInAnyOrder(receivedIds.toArray()));

    List<String> receivedUrls = gifSearchResult.getGifs().stream().map(Gif::getUrl).toList();
    List<String> expectedUrls =
        List.of(
            "https://media0.giphy.com/media/3oD3YJDA4I6J4PmvpS/200.gif?cid=60ca844dro9y9tqx3ozgb2j0jhz633t4x0inedwtf3yk0dpq&ep=v1_gifs_search&rid=200.gif&ct=g",
            "https://media3.giphy.com/media/k5m2Gwn83tHo08dNRg/200.gif?cid=60ca844dro9y9tqx3ozgb2j0jhz633t4x0inedwtf3yk0dpq&ep=v1_gifs_search&rid=200.gif&ct=g",
            "https://media2.giphy.com/media/hryis7A55UXZNCUTNA/200.gif?cid=60ca844dro9y9tqx3ozgb2j0jhz633t4x0inedwtf3yk0dpq&ep=v1_gifs_search&rid=200.gif&ct=g");
    assertThat(expectedUrls, Matchers.containsInAnyOrder(receivedUrls.toArray()));
  }
}
