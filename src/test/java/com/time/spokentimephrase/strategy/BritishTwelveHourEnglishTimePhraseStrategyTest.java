package com.time.spokentimephrase.strategy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for BritishTwelveHourEnglishTimePhraseStrategy.
 * <p>
 * Tests are grouped into:
 *  1. Fixed phrases (midnight / noon)
 *  2. Standard "past" / "to" minute buckets
 *  3. Hour wraparound at 12 -> 1
 *  4. Input normalization (single-digit hour, whitespace)
 *  5. Format validation errors (regex layer, on the interface)
 *  6. Business/range validation errors (validateTime layer, per-strategy)
 *  7. Known gaps in current behavior (documented, not asserted as "correct")
 */
class BritishTwelveHourEnglishTimePhraseStrategyTest {

    private final BritishTwelveHourEnglishTimePhraseStrategy strategy =
            new BritishTwelveHourEnglishTimePhraseStrategy();

    // ---------- 1. Fixed phrases ----------

    @ParameterizedTest(name = "{0} -> {1}")
    @CsvSource({
            "00:00, midnight",
            "0:00, midnight",
            "12:00, noon"
    })
    @DisplayName("Fixed times resolve to midnight/noon regardless of leading zero")
    void fixedTimes(String input, String expected) {
        assertEquals(expected, strategy.generateSpokenPhrase(input));
    }

    // ---------- 2. Standard minute buckets (o'clock, past, to) ----------

    @ParameterizedTest(name = "{0} -> {1}")
    @CsvSource({
            "10:00, ten o'clock",
            "9:00, nine o'clock",
            "09:00, nine o'clock",
            "10:05, five past ten",
            "10:10, ten past ten",
            "10:15, quarter past ten",
            "10:20, twenty past ten",
            "10:25, twenty-five past ten",
            "10:30, half past ten",
            "10:35, twenty-five to eleven",
            "10:45, quarter to eleven",
            "10:50, ten to eleven",
            "10:55, five to eleven",
            "1:05, five past one",
            "01:05, five past one",
            "6:00, six o'clock"
    })
    void standardMinuteBuckets(String input, String expected) {
        assertEquals(expected, strategy.generateSpokenPhrase(input));
    }

    // ---------- 3. Hour wraparound at the 12/1 boundary ----------

    @ParameterizedTest(name = "{0} -> {1}")
    @CsvSource({
            "11:35, twenty-five to twelve",
            "12:35, twenty-five to one",
            "12:30, half past twelve",
            "0:35, twenty-five to one"
    })
    @DisplayName("Hour correctly wraps 12 -> 1 (and 0 -> 1) once minutes push into the 'to' half")
    void hourWraparound(String input, String expected) {
        assertEquals(expected, strategy.generateSpokenPhrase(input));
    }

    // ---------- 4. Input normalization ----------

    @Test
    @DisplayName("Leading/trailing whitespace around an otherwise valid time is trimmed")
    void trimsWhitespace() {
        assertEquals("nine o'clock", strategy.generateSpokenPhrase(" 09:00 "));
    }

    // ---------- 5. Format validation (regex layer, on the interface) ----------
    // The shared TIME_FORMAT_REGEX only checks *shape*: H:mm or HH:mm, hour token
    // shaped like 0-23, minutes always exactly 2 digits (0-59). It doesn't know or
    // care about 12-hour vs 24-hour semantics — that's the next layer's job.

    @ParameterizedTest
    @ValueSource(strings = {
            "24:00",   // hour token doesn't even match the 0-23 shape
            "55:00",   // same — no branch in the regex accepts a leading '5'
            "12:60",   // minute out of 0-59 regex range
            "10:5",    // minute must be exactly 2 digits
            "abc",     // not time-shaped at all
            "10-00",   // wrong separator
            "10:00:00" // seconds not allowed
    })
    @DisplayName("Strings that don't match the time SHAPE throw a format IllegalArgumentException")
    void invalidFormatThrows(String input) {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> strategy.generateSpokenPhrase(input));
        assertEquals(
                String.format("Invalid Time, should be in 12-hour format as '00:00', requested: '%s'", input),
                ex.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    @DisplayName("Null, empty, or blank input throws a format IllegalArgumentException")
    void blankOrNullThrows(String input) {
        assertThrows(IllegalArgumentException.class, () -> strategy.generateSpokenPhrase(input));
    }

    // ---------- 6. Business/range validation (validateTime layer, per-strategy) ----------
    // These values ARE shaped correctly (pass the shared regex) but violate this
    // specific strategy's 12-hour range rule, enforced in validateTime().

    @ParameterizedTest
    @ValueSource(strings = {"13:00", "19:00", "20:00", "23:00"})
    @DisplayName("Hours 13-23 pass the regex (0-23 shaped) but fail this strategy's 12-hour range check")
    void hourAboveTwelveFailsRangeCheck(String input) {
        String hourPart = input.split(":")[0];
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> strategy.generateSpokenPhrase(input));
        assertEquals("Invalid Time Requested with hour: " + Integer.parseInt(hourPart), ex.getMessage());
    }

    // ---------- 7. Known gaps in current behavior ----------
    // These document what the code does TODAY, not what it necessarily should do.
    // Flagging them here so a future fix has a test to update rather than a surprise in prod.

    @Test
    @DisplayName("KNOWN GAP: minutes that aren't multiples of 5 fall back to raw digit words " +
            "with no 'past'/'to' framing")
    void nonFiveMultipleMinutesFallBackToRawDigits() {
        assertEquals("five seven", strategy.generateSpokenPhrase("5:07"));
        assertEquals("twelve one", strategy.generateSpokenPhrase("12:01"));
    }

    @Test
    @DisplayName("KNOWN GAP: minute 59 isn't in the MINUTES map, so the hour doesn't roll forward " +
            "even though 59 is 'past the half hour'")
    void minuteFiftyNineDoesNotRollHourForward() {
        assertEquals("eleven fifty-nine", strategy.generateSpokenPhrase("11:59"));
        assertEquals("twelve fifty-nine", strategy.generateSpokenPhrase("12:59"));
    }
}