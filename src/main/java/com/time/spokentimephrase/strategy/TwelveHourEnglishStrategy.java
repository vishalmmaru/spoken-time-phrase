package com.time.spokentimephrase.strategy;

public abstract class TwelveHourEnglishStrategy implements TimePhraseStrategy {
    private static final String[] units = {
            "", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine",
            "ten", "eleven", "twelve", "thirteen", "fourteen", "fifteen", "sixteen",
            "seventeen", "eighteen", "nineteen"
    };

    private static final String[] tens = {
            "", "", "twenty", "thirty", "forty", "fifty"
    };

    // converts any number from 0 to 59 into text
    protected String convert(int number) {
        if (number < 20) return units[number];

        int tensDigit = number / 10;
        int unitsDigit = number % 10;

        return unitsDigit == 0
                ? tens[tensDigit]
                : tens[tensDigit] + "-" + units[unitsDigit];
    }
}
