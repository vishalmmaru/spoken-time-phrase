package com.time.spokentimephrase.factory;

import com.time.spokentimephrase.strategy.BritishTwelveHourEnglishTimePhraseStrategy;
import com.time.spokentimephrase.strategy.TimePhraseStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TimePhraseStrategyFactoryTest {

    private BritishTwelveHourEnglishTimePhraseStrategy britishStrategy;
    private TimePhraseStrategyFactory factory;

    @BeforeEach
    void setUp() {
        britishStrategy = new BritishTwelveHourEnglishTimePhraseStrategy();
        factory = new TimePhraseStrategyFactory(britishStrategy);
    }

    @Test
    @DisplayName("Exact-case 'BRITISH_ENGLISH' resolves to the British strategy")
    void resolvesBritishEnglishExactCase() {
        TimePhraseStrategy result = factory.getTimePhraseStrategyForLanguage("BRITISH_ENGLISH");
        assertSame(britishStrategy, result);
    }

    @ParameterizedTest
    @ValueSource(strings = {"british_english", "British_English", "BrItIsH_eNgLiSh", " BrItIsH_eNgLiSh  "})
    @DisplayName("Language lookup is case-insensitive")
    void resolvesBritishEnglishCaseInsensitive(String lang) {
        assertSame(britishStrategy, factory.getTimePhraseStrategyForLanguage(lang));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    @DisplayName("Null, empty, or blank language throws with a clear message")
    void blankOrNullLanguageThrows(String lang) {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> factory.getTimePhraseStrategyForLanguage(lang));
        assertEquals("Language must not be null or blank", ex.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"FRENCH", "AMERICAN_ENGLISH", "SPANISH", "xyz"})
    @DisplayName("Unsupported languages throw with the offending value in the message")
    void unsupportedLanguageThrows(String lang) {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> factory.getTimePhraseStrategyForLanguage(lang));
        assertEquals("Unsupported language: " + lang, ex.getMessage());
    }
}