package be.twofold.valen.core.util.logging;

import java.io.*;
import java.time.*;
import java.time.format.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.*;
import java.util.logging.Formatter;

abstract class AbstractFormatter extends Formatter {
    private static final Map<String, String> LOGGER_NAMES = new ConcurrentHashMap<>();

    private static final DateTimeFormatter TIME_FORMAT =
        DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    @Override
    public final String format(LogRecord record) {
        var time = Instant.ofEpochMilli(record.getMillis())
            .atZone(ZoneId.systemDefault())
            .format(TIME_FORMAT);

        var level = record.getLevel().getName();
        var thread = Thread.currentThread().getName();
        var logger = record.getLoggerName();
        var message = record.getMessage();

        // Shorten logger name
        logger = LOGGER_NAMES.computeIfAbsent(logger, s -> shorten(s, 40));

        var throwable = "";
        if (record.getThrown() != null) {
            var sw = new StringWriter();
            var pw = new PrintWriter(sw);
            pw.println();
            record.getThrown().printStackTrace(pw);
            pw.close();
            throwable = sw.toString();
        }

        return String.format("%s %s%-5s%s --- [%15.15s] %s%-40s%s : %s%s%n",
            time,
            levelColor(level), level, reset(),
            thread,
            loggerColor(), logger, reset(),
            message, throwable
        );
    }

    private String shorten(String logger, int maxLength) {
        var split = List.of(logger.split("\\."));
        var totalLength = (split.size() - 1) * 2 + split.getLast().length();

        var last = split.size() - 1;
        for (var i = split.size() - 2; i >= 0; i--) {
            totalLength += split.get(i).length() - 2;
            if (totalLength > maxLength) {
                break;
            }
            last = i;
        }

        var builder = new StringBuilder();
        for (var i = 0; i < last; i++) {
            builder.append(split.get(i).charAt(0)).append('.');
        }
        for (var i = last; i < split.size() - 1; i++) {
            builder.append(split.get(i)).append('.');
        }
        return builder.append(split.getLast()).toString();
    }

    abstract String levelColor(String level);

    abstract String loggerColor();

    abstract String reset();
}
