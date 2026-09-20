package com.time.spokentimephrase.strategy;

import io.micrometer.common.util.StringUtils;

public interface TimePhraseStrategy {
    String TIME_FORMAT_REGEX = "^([01]?\\d|2[0-3]):[0-5]\\d$";

    // default request time format validation
    // only allowed -> 09:00 or 9:00
    default boolean isValidTimeFormat(String time) {
        return !StringUtils.isBlank(time) && time.trim().matches(TIME_FORMAT_REGEX);
    }

    String generateSpokenPhrase(String time);
    int[] validateTime(String time);
}
