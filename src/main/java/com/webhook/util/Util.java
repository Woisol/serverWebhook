package com.webhook.util;

import java.time.Duration;

public class Util {
    public static String formatDuration(long millis) {
        if (millis < 0) millis = 0;
        Duration duration = Duration.ofMillis(millis);
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();

        StringBuilder sb = new StringBuilder();
        if (hours > 0) {
            sb.append(hours).append("h");
        }
        if (minutes > 0) {
            sb.append(minutes).append("min");
        }
        sb.append(seconds).append("s");

        return sb.toString();
    }
}
