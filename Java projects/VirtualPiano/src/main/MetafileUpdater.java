package main;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class MetafileUpdater {
    public static void setLineValue(int lineNumber, String data) throws IOException {
        final String fileLocation = "metadata.txt";
        File file = new File(fileLocation);
        Scanner scan = new Scanner(file);
        List<String> lines = new LinkedList<>();
        while(scan.hasNext()) {
            String line = scan.next();
            System.out.println(line);
            if(!line.isEmpty()) {
                lines.add(line);
            }
        }
        scan.close();
        lines.set(lineNumber - 1, data);
        PrintWriter writer = new PrintWriter(fileLocation);
        for(String x : lines) {
            writer.format(x);
            System.out.println(x);
        }
        writer.close();
    }
}
