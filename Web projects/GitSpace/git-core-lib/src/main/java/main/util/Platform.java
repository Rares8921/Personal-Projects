package main.util;

public class Platform {

    public static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }

}
