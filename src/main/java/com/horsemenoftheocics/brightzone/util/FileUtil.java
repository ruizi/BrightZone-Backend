package com.horsemenoftheocics.brightzone.util;

import java.io.File;

public class FileUtil {

    public static String getRootPath() {
        String dataPath = "static";
        File tempFile = new File(dataPath);
        return tempFile.getAbsolutePath();
    }
}
