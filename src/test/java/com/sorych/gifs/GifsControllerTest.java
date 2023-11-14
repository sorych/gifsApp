package com.sorych.gifs;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@SpringBootTest
@AutoConfigureMockMvc
public class GifsControllerTest {

  @Autowired private MockMvc mvc;

  @Test
  public void testShouldReturnJson() throws Exception {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("searchTerm", "a");
    params.add("searchTerm", "b");

    mvc.perform(MockMvcRequestBuilders.get("/query").params(params)
        )
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.data", notNullValue()));
        //todo test values
  }

  @Test
  public void testNoQueryParamsShouldReturnBadRequest() throws Exception {

    mvc.perform(MockMvcRequestBuilders.get("/query")
        )
        .andExpect(status().isBadRequest());
  }
}
