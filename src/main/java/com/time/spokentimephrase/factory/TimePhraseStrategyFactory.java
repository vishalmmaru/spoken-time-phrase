package com.time.spokentimephrase.factory;

import com.time.spokentimephrase.constants.Languages;
import com.time.spokentimephrase.strategy.BritishTwelveHourEnglishTimePhraseStrategy;
import com.time.spokentimephrase.strategy.TimePhraseStrategy;
import io.micrometer.common.util.StringUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class TimePhraseStrategyFactory {

    private final BritishTwelveHourEnglishTimePhraseStrategy britishEnglishStrategy;

    public TimePhraseStrategy getTimePhraseStrategyForLanguage(String lang) {
        if (StringUtils.isBlank(lang)) throw new IllegalArgumentException("Language must not be null or blank");

        Languages language;
        try {
            language = Languages.valueOf(lang.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unsupported language: " + lang);
        }

        return switch (language) {
            case BRITISH_ENGLISH -> this.britishEnglishStrategy;
        };
    }
}
