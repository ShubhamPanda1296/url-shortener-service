package com.shubhampanda.urlshortener.controller;

import com.shubhampanda.urlshortener.dto.CreateUrlRequest;
import com.shubhampanda.urlshortener.entity.Url;
import com.shubhampanda.urlshortener.repository.UrlRepository;
import com.shubhampanda.urlshortener.util.ShortCodeGenerator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UrlControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UrlRepository repository;
    @MockitoSpyBean
    private ShortCodeGenerator codeGenerator;

    @BeforeEach
    void clearDatabase() {
        repository.deleteAll();
    }

    @ParameterizedTest
    @ValueSource(strings = {"https://www.example.com/some/long/path", "http://example.com/path?x=1#part"})
    void createsUrlAndPersistsIt(String originalUrl) throws Exception {
        String body = mockMvc.perform(post("/api/urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CreateUrlRequest(originalUrl))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        JsonNode response = objectMapper.readTree(body);
        assertThat(response.size()).isEqualTo(2);
        assertThat(response.get("originalUrl").asText()).isEqualTo(originalUrl);
        assertThat(response.get("shortCode").asText()).matches("[A-Za-z0-9]{6}");
        assertThat(repository.findAll()).singleElement().satisfies(saved -> {
            assertThat(saved.getId()).isNotNull();
            assertThat(saved.getOriginalUrl()).isEqualTo(originalUrl);
            assertThat(saved.getShortCode()).isEqualTo(response.get("shortCode").asText());
            assertThat(saved.getCreatedAt()).isNotNull();
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{}", "{\"url\":null}", "{\"url\":\"\"}", "{\"url\":\"   \"}",
            "{\"url\":\"not-a-url\"}", "{\"url\":\"ftp://example.com/file\"}",
            "{\"url\":\"https:///path\"}", "{\"url\":\"https://example.com/a b\"}",
            "{\"url\":\"https://example.com:99999/path\"}"
    })
    void rejectsInvalidUrlWithoutPersisting(String request) throws Exception {
        mockMvc.perform(post("/api/urls")
                        .contentType(MediaType.APPLICATION_JSON).content(request))
                .andExpect(status().isBadRequest());
        assertThat(repository.count()).isZero();
    }

    @Test
    void retriesAfterDatabaseShortCodeCollision() throws Exception {
        repository.saveAndFlush(new Url("https://example.com/existing", "aB3xY7"));
        doReturn("aB3xY7", "zZ9kL2").when(codeGenerator).generate();

        String body = mockMvc.perform(post("/api/urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\":\"https://example.com/new\"}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        assertThat(objectMapper.readTree(body).get("shortCode").asText()).isEqualTo("zZ9kL2");
        assertThat(repository.findAll()).extracting(Url::getShortCode)
                .containsExactlyInAnyOrder("aB3xY7", "zZ9kL2");
        verify(codeGenerator, times(2)).generate();
    }
}
