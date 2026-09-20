package com.time.spokentimephrase.controller;

import com.time.spokentimephrase.service.TimePhraseGenerationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TimePhraseController.class)
class TimePhraseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TimePhraseGenerationService phraseGenerationService;

    @Test
    void validRequestReturns200WithPhraseBody() throws Exception {
        given(phraseGenerationService.generatePhraseForRequest(any())).willReturn("nine o'clock");

        mockMvc.perform(post("/api/v1/timephrase")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"time\":\"09:00\",\"language\":\"BRITISH_ENGLISH\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("nine o'clock"));
    }

    @ParameterizedTest
    @CsvSource({
            "10:35, BRITISH_ENGLISH, 'twenty-five to eleven'",
            "00:00, BRITISH_ENGLISH, midnight",
            "12:00, BRITISH_ENGLISH, noon"
    })
    void variousValidRequestsReturn200(String time, String language, String expectedPhrase) throws Exception {
        given(phraseGenerationService.generatePhraseForRequest(any())).willReturn(expectedPhrase);

        mockMvc.perform(post("/api/v1/timephrase")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format("{\"time\":\"%s\",\"language\":\"%s\"}", time, language)))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedPhrase));
    }

    @Test
    void invalidTimeFormatReturns400WithErrorBody() throws Exception {
        given(phraseGenerationService.generatePhraseForRequest(any()))
                .willThrow(new IllegalArgumentException(
                        "Invalid Time, should be in 12-hour format as '00:00', requested: '25:00'"));

        mockMvc.perform(post("/api/v1/timephrase")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"time\":\"25:00\",\"language\":\"BRITISH_ENGLISH\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value(
                        "Invalid Time, should be in 12-hour format as '00:00', requested: '25:00'"));
    }

    @Test
    void hourOutOfTwelveHourRangeReturns400WithErrorBody() throws Exception {
        given(phraseGenerationService.generatePhraseForRequest(any()))
                .willThrow(new IllegalArgumentException("Invalid Time Requested with hour: 13"));

        mockMvc.perform(post("/api/v1/timephrase")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"time\":\"13:00\",\"language\":\"BRITISH_ENGLISH\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Invalid Time Requested with hour: 13"));
    }

    @Test
    void unsupportedLanguageReturns400WithErrorBody() throws Exception {
        given(phraseGenerationService.generatePhraseForRequest(any()))
                .willThrow(new IllegalArgumentException("Unsupported language: FRENCH"));

        mockMvc.perform(post("/api/v1/timephrase")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"time\":\"09:00\",\"language\":\"FRENCH\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Unsupported language: FRENCH"));
    }

    @Test
    void missingLanguageReturns400WithErrorBody() throws Exception {
        given(phraseGenerationService.generatePhraseForRequest(any()))
                .willThrow(new IllegalArgumentException("Language must not be null or blank"));

        mockMvc.perform(post("/api/v1/timephrase")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"time\":\"09:00\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Language must not be null or blank"));
    }

    @Test
    void malformedJsonReturns400() throws Exception {
        mockMvc.perform(post("/api/v1/timephrase")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{not valid json"))
                .andExpect(status().isBadRequest());
    }
}