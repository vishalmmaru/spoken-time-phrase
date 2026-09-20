package com.time.spokentimephrase.factory;

import com.time.spokentimephrase.constants.Languages;
import com.time.spokentimephrase.strategy.BritishTwelveHourEnglishTimePhraseStrategy;
import com.time.spokentimephrase.strategy.TimePhraseStrategy;
import io.micrometer.common.util.StringUtils;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class TimePhraseStrategyFactory {

    private final BritishTwelveHourEnglishTimePhraseStrategy britishEnglishStrategy;
    private static final Logger log = LoggerFactory.getLogger(TimePhraseStrategyFactory.class);

    public TimePhraseStrategy getTimePhraseStrategyForLanguage(String lang) {
        if (StringUtils.isBlank(lang)) {
            log.warn("Rejected request: language was null or blank");
            throw new IllegalArgumentException("Language must not be null or blank");
        }

        Languages language;
        try {
            language = Languages.valueOf(lang.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("Rejected request: unsupported language '{}'", lang);
            throw new IllegalArgumentException("Unsupported language: " + lang);
        }
        log.debug("Resolved language '{}' to strategy enum {}", lang, language);
        return switch (language) {
            case BRITISH_ENGLISH -> this.britishEnglishStrategy;
        };
    }
}
