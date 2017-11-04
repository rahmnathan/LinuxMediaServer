package com.github.rahmnathan.localmovies.utils;

import java.io.File;

public class PathUtils {

    public static boolean isTopLevel(String currentPath){
        return currentPath.split(File.separator).length == 2;
    }

    public static File getParentFile(String path){
        int directoryDepth = path.split(File.separator).length;
        if(!isTopLevel(path))
            directoryDepth -= 2;

        File file = new File(path);
        for(int i = 0; i < directoryDepth; i++){
            file = file.getParentFile();
        }

        return file;
    }

    public static String getTitle(String fileName){
        if (fileName.charAt(fileName.length() - 4) == '.') {
            return fileName.substring(0, fileName.length() - 4);
        }

        return fileName;
    }
}
