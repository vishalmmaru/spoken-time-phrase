package com.time.spokentimephrase.service.impl;

import com.time.spokentimephrase.factory.TimePhraseStrategyFactory;
import com.time.spokentimephrase.model.PhraseRequest;
import com.time.spokentimephrase.service.TimePhraseGenerationService;
import com.time.spokentimephrase.strategy.TimePhraseStrategy;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class TimePhraseGenerationServiceImpl implements TimePhraseGenerationService {

    private final TimePhraseStrategyFactory timePhraseStrategyFactory;
    private static final Logger log = LoggerFactory.getLogger(TimePhraseGenerationServiceImpl.class);

    @Override
    public String generatePhraseForRequest(PhraseRequest request) {
        log.debug("Resolving strategy for language='{}'", request.getLanguage());
        TimePhraseStrategy phraseStrategy = timePhraseStrategyFactory.getTimePhraseStrategyForLanguage(request.getLanguage());
        log.debug("Generating spoken phrase using {} for time='{}'", phraseStrategy.getClass().getSimpleName(), request.getTime());
        return phraseStrategy.generateSpokenPhrase(request.getTime());
    }
}
