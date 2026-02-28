package com.diskanalyzer;

import com.diskanalyzer.cli.CLIApplication;
import com.diskanalyzer.gui.DiskAnalyzerApp;

public final class Main {

    public static void main(String[] args) {
        if (args.length > 0 && "--gui".equals(args[0])) {
            DiskAnalyzerApp.launch(args);
        } else if (args.length == 0) {
            CLIApplication.run(args);
        } else {
            CLIApplication.run(args);
        }
    }
}
