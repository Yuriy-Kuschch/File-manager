package com.company;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Scanner;
import java.util.stream.Stream;

public class Main
{
    private static File currentPath = new File("C:\\");

    public static void main( String[] args ) throws IOException {
        Scanner in = new Scanner(System.in);

        System.out.println("FILE MANAGER: CD, DIR, COPY, INFO");
        System.out.println("CD COMMAND: ENTER cd [path]");
        System.out.println("DIR COMMAND: ENTER dir");
        System.out.println("COPY COMMAND: ENTER copy [from] [to]");
        System.out.println("INFO COMMAND ONLY FOR TXT FILE: ENTER info [file path]");
        System.out.println("IF PATH CONTAINS SPACE - USE \"PA TH\"");
        while (true) {
            try {
                System.out.println("Curr directory: " + currentPath.getAbsolutePath());
                String input = in.nextLine();
                int spaceIndex = input.indexOf(" ");
                String command = null;
                if (spaceIndex != -1) {
                    command = input.substring(0, spaceIndex);
                } else {
                    command = input;
                }
                if ("cd".equals(command)) {
                    cdMethod(input, spaceIndex);
                } else if ("dir".equals(command)) {
                    dirMethod();
                } else if ("copy".equals(command)) {
                    copyMethod(input, spaceIndex);
                } else if ("info".equals(command)) {
                    String filename = checkPath(input.substring(spaceIndex + 1));
                    File f = new File(filename);
                    Files.lines(f.toPath(), Charset.forName("windows-1251")).forEach(System.out::println);
                }
            } catch (Exception e) {
                System.out.println("wrong input");
                e.printStackTrace();
            }
        }
    }





    private static void copyMethod(String input, int spaceIndex) throws IOException {
        String paramsText = input.substring(spaceIndex + 1);
        String from, to;
        if (paramsText.contains("\"")) {
            String[] params = input.substring(spaceIndex + 1).split("\"");
            from = params[0];
            to = params[2];
        } else {
            String[] params = input.substring(spaceIndex + 1).split(" ");
            from = params[0];
            to = params[1];
        }
        File fromFile = new File(checkPath(from));
        File toFile = new File(checkPath(to));
        if (fromFile.isFile() && !toFile.isDirectory()) {
            Files.copy(fromFile.toPath(), toFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } else if (fromFile.isDirectory() && !toFile.isFile()) {
            copyFolder(fromFile.toPath(), toFile.toPath());
        } else {
            System.out.println("You can copy only folder to folder or file to file");
        }
    }





    private static void dirMethod() {
        File[] files = currentPath.listFiles();
        for (File file : files) {
            String dir = file.isDirectory() ? "DIR " : "";
            String name = file.getName();
            System.out.println(dir + name);
        }
    }

    private static void cdMethod(String input, int spaceIndex) {
        String params = input.substring(spaceIndex + 1).replace("\"", "");
        File newFile;
        if ("..".equals(params)) {
            newFile = currentPath.getParentFile();
        } else {
            newFile = new File(checkPath(params));
        }
        if (newFile.exists() && newFile.isDirectory()) {
            currentPath = newFile;
        } else {
            System.out.println("This directory does not exists");
        }
    }

    private static String checkPath(String path) {
        return path.contains(":") ? path : (currentPath.getAbsolutePath() + "\\" + path);
    }


    private static void copyFolder(Path src, Path dest) throws IOException {
        try (Stream<Path> stream = Files.walk(src)) {
            stream.forEach(source -> copy(source, dest.resolve(src.relativize(source))));
        }
    }




    private static void copy(Path source, Path dest) {
        try {
            Files.copy(source, dest, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
