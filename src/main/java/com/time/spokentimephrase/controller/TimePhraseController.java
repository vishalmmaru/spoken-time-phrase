package com.time.spokentimephrase.controller;

import com.time.spokentimephrase.model.PhraseRequest;
import com.time.spokentimephrase.service.TimePhraseGenerationService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/timephrase")
public class TimePhraseController {

    private final TimePhraseGenerationService phraseGenerationService;
    private static final Logger log = LoggerFactory.getLogger(TimePhraseController.class);

    @PostMapping
    public ResponseEntity<String> generateSpokenTimePhrase(@RequestBody PhraseRequest request) {
        log.info("Received time phrase request: time='{}', language='{}'", request.getTime(), request.getLanguage());
        String response = phraseGenerationService.generatePhraseForRequest(request);
        log.info("Returning generated phrase: '{}'", response);
        return ResponseEntity.ok().body(response);
    }
}
