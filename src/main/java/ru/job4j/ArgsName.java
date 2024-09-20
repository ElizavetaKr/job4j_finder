package ru.job4j;

import java.util.HashMap;
import java.util.Map;

public class ArgsName {
    private final Map<String, String> values = new HashMap<>();

    public String get(String key) {
        if (!values.containsKey(key)) {
            throw new IllegalArgumentException(String.format("This key: '%s' is missing", key));
        }
        return values.get(key);
    }

    public String[] getValues() {
        return values.values().toArray(new String[0]);
    }

    private void parse(String[] args) {
        for (String param : args) {
            String[] arr = param.split("=", 2);
            values.put(arr[0].substring(1), arr[1]);
        }
    }

    public static ArgsName of(String[] args) {
        if (args.length == 0) {
            throw new IllegalArgumentException("Arguments not passed to program");
        }
        validationOfParameters(args);
        ArgsName names = new ArgsName();
        names.parse(args);
        return names;
    }

    private static void validationOfParameters(String[] args) {
        for (String param : args) {
            if (!param.contains("=")) {
                throw new IllegalArgumentException(
                        String.format("Error: This argument '%s' does not contain an equal sign", param));
            }
            if (!param.startsWith("-")) {
                throw new IllegalArgumentException(
                        String.format("Error: This argument '%s' does not start with a '-' character", param));
            }
            if (param.startsWith("-=")) {
                throw new IllegalArgumentException(
                        String.format("Error: This argument '%s' does not contain a key", param));
            }
            if (param.split("=").length < 2) {
                throw new IllegalArgumentException(
                        String.format("Error: This argument '%s' does not contain a value", param));
            }
        }

    }
}
