package com.smartsampa.utils;

import java.io.File;

/**
 * Created by ruan0408 on 11/03/2016.
 */
public class SmartSampaDir {

    private static final String DIR_NAME;
    private static final String PATH;
    private static final File DIRECTORY;

    static {
        DIR_NAME = "SmartSampa";
        PATH = System.getProperty("user.home")+"/"+ DIR_NAME;
        DIRECTORY = new File(PATH);
    }

    public static boolean hasSubDir(String dirName) {
        return new File(getPath()+"/"+dirName).exists();
    }

    public static String getPath() {
        return DIRECTORY.getPath();
    }
}
