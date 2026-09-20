package com.time.spokentimephrase.controller;

import com.time.spokentimephrase.model.PhraseRequest;
import com.time.spokentimephrase.service.TimePhraseGenerationService;
import lombok.AllArgsConstructor;
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

    @PostMapping
    public ResponseEntity<String> generateSpokenTimePhrase(@RequestBody PhraseRequest request) {
        String response = phraseGenerationService.generatePhraseForRequest(request);
        return ResponseEntity.ok().body(response);
    }
}
