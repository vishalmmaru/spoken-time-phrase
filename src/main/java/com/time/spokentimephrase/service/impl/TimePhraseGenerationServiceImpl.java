package com.time.spokentimephrase.service.impl;

import com.time.spokentimephrase.factory.TimePhraseStrategyFactory;
import com.time.spokentimephrase.model.PhraseRequest;
import com.time.spokentimephrase.service.TimePhraseGenerationService;
import com.time.spokentimephrase.strategy.TimePhraseStrategy;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class TimePhraseGenerationServiceImpl implements TimePhraseGenerationService {

    private final TimePhraseStrategyFactory timePhraseStrategyFactory;

    @Override
    public String generatePhraseForRequest(PhraseRequest request) {
        TimePhraseStrategy phraseStrategy = timePhraseStrategyFactory.getTimePhraseStrategyForLanguage(request.getLanguage());
        return phraseStrategy.generateSpokenPhrase(request.getTime());
    }
}
