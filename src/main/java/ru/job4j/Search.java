package ru.job4j;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class Search {

    private static ArgsName validation(String[] args) {
        ArgsName param = ArgsName.of(args);
        if (args.length != 4) {
            throw new IllegalArgumentException("The wrong number of parameters is set");
        }
        File file = new File(param.get("d"));
        if (!file.exists()) {
            throw new IllegalArgumentException(String.format("Not exist %s", file.getAbsoluteFile()));
        }
        if (!file.isDirectory()) {
            throw new IllegalArgumentException(String.format("Not directory %s", file.getAbsoluteFile()));
        }
        if (!(param.get("t").equals("mask") || param.get("t").equals("name") || param.get("t").equals("regex"))) {
            throw new IllegalArgumentException("The type of search is not set correctly");
        } else {
            if (param.get("t").equals("name")) {
                File fileName = new File(param.get("n"));
                if (!fileName.exists()) {
                    throw new IllegalArgumentException("The file name for search is not set correctly");
                }
            }
            if (param.get("t").equals("mask")) {
                if (!(param.get("n").contains("?") || param.get("n").contains("*"))) {
                    throw new IllegalArgumentException("The mask for search is not set correctly");
                }
            }
            if (param.get("t").equals("regex")) {
                try {
                    Pattern pattern = Pattern.compile(param.get("n"));
                } catch (PatternSyntaxException e) {
                    System.out.println("The regex for search is not set correctly");
                }
            }

        }
        if (!param.get("o").endsWith(".txt")) {
            throw new IllegalArgumentException("The file name is not set correctly");
        }
        return param;
    }

    public static List<Path> search(Path root, ArgsName param) throws IOException {
        Predicate<Path> condition = n -> {
            String regex = param.get("n");
            if (param.get("t").equals("mask")) {
                regex = regex.replace(".", "[.]")
                        .replace("*", ".*")
                        .replace("?", ".");
            }
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(n.toString());
            return matcher.matches();
        };
        SearchFiles searcher = new SearchFiles(condition);
        Files.walkFileTree(root, searcher);
        return searcher.getPaths();
    }

    public static void record(ArgsName param) throws IOException {

        try (FileWriter writer = new FileWriter(param.get("o"), true)) {
            if (param.get("t").equals("name")) {
                writer.write(new File(param.get("n")).toString());
            } else {
                List<Path> paths = search(Path.of(param.get("d")), param);
                for (Path p : paths) {
                    writer.write(p.toString());
                    writer.write(System.lineSeparator());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        ArgsName param = validation(args);
        record(param);
    }
}
