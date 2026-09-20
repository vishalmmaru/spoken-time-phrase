package com.time.spokentimephrase.strategy;

import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class BritishTwelveHourEnglishTimePhraseStrategy extends TwelveHourEnglishStrategy {

    private static final Map<String, String> FIXED;
    private static final Map<Integer, String> HOURS;
    private static final Map<Integer, String> MINUTES;

    static {
        FIXED = Map.of("00:00", "midnight", "12:00", "noon");

        HOURS = Map.ofEntries(Map.entry(0, "twelve"));

        MINUTES = Map.ofEntries(
                    Map.entry(0, "o'clock"),
                    Map.entry(5, "five past"),
                    Map.entry(10, "ten past"),
                    Map.entry(15, "quarter past"),
                    Map.entry(20, "twenty past"),
                    Map.entry(25, "twenty-five past"),
                    Map.entry(30, "half past"),
                    Map.entry(35, "twenty-five to"),
                    Map.entry(40, "twenty to"),
                    Map.entry(45, "quarter to"),
                    Map.entry(50, "ten to"),
                    Map.entry(55, "five to"));
    }

    @Override
    public String generateSpokenPhrase(String time) {
        if (!isValidTimeFormat(time)) throw new IllegalArgumentException(String.format("Invalid Time, should be in 12-hour format as '00:00', requested: '%s'", time));
        time = time.trim();

        // get hour and minute from the requested time
        int[] timeArray = validateTime(time);
        int hour = timeArray[0];
        int minute = timeArray[1];

        // normalize time and find
        // if a fixed phrase for the whole time can be returned
        time = String.format("%02d:%02d", hour, minute);
        if (FIXED.containsKey(time)) return FIXED.get(time);

        String hourPhrase = getHourPhrase(hour, minute);
        String minutesPhrase = getMinutesPhrase(minute);

        // set the order of phrases based on the minutes
        boolean minutesFirst = minute != 0 && MINUTES.containsKey(minute);
        return minutesFirst ? minutesPhrase + " " + hourPhrase : hourPhrase + " " + minutesPhrase;
    }

    // validate hour and minute value for 12-hour format
    // and return them in an array {hour, minutes}
    @Override
    public int[] validateTime(String time) {
        String[] parts = time.split(":");

        int hour = Integer.parseInt(parts[0]);
        if (hour > 12 || hour < 0) throw new IllegalArgumentException("Invalid Time Requested with hour: " + hour);

        int minutes = Integer.parseInt(parts[1]);
        if (minutes > 59 || minutes < 0) throw new IllegalArgumentException("Invalid Time Requested with minutes: " + minutes);

        return new int[]{hour, minutes};
    }

    // get the spoken phrase for minutes
    private String getMinutesPhrase(int minutes) {
        return MINUTES.getOrDefault(minutes, convert(minutes));
    }

    // get the spoken phrase for hours
    private String getHourPhrase(int hour, int minutes) {
        hour = (minutes > 30 && MINUTES.containsKey(minutes)) ? (hour % 12) + 1 : hour;
        return HOURS.getOrDefault(hour, convert(hour));
    }


}
