package com.time.spokentimephrase.service;

import com.time.spokentimephrase.model.PhraseRequest;

public interface TimePhraseGenerationService {
    String generatePhraseForRequest(PhraseRequest request);
}
