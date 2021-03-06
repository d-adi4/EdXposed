package com.elderdrivers.riru.xposed.util;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Process;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import static com.elderdrivers.riru.xposed.util.ProcessUtils.PER_USER_RANGE;

public class FileUtils {

    public static final boolean IS_USING_PROTECTED_STORAGE = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N;

    /**
     * Delete a file or a directory and its children.
     *
     * @param file The directory to delete.
     * @throws IOException Exception when problem occurs during deleting the directory.
     */
    public static void delete(File file) throws IOException {

        for (File childFile : file.listFiles()) {

            if (childFile.isDirectory()) {
                delete(childFile);
            } else {
                if (!childFile.delete()) {
                    throw new IOException();
                }
            }
        }

        if (!file.delete()) {
            throw new IOException();
        }
    }

    public static String readLine(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            return reader.readLine();
        } catch (Throwable throwable) {
            return "";
        }
    }

    public static void writeLine(File file, String line) {
        try {
            file.createNewFile();
        } catch (IOException ex) {
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(line);
            writer.flush();
        } catch (Throwable throwable) {
            Utils.logE("error writing line to file " + file + ": " + throwable.getMessage());
        }
    }

    public static String getPackageName(String dataDir) {
        if (TextUtils.isEmpty(dataDir)) {
            Utils.logE("getPackageName using empty dataDir");
            return "";
        }
        int lastIndex = dataDir.lastIndexOf("/");
        if (lastIndex < 0) {
            return dataDir;
        }
        return dataDir.substring(lastIndex + 1);
    }

    // FIXME: Although multi-users is considered here, but compat mode doesn't support other users' apps on Oreo and later yet.
    @SuppressLint("SdCardPath")
    public static String getDataPathPrefix() {
        int userId = Process.myUid() / PER_USER_RANGE;
        String format = IS_USING_PROTECTED_STORAGE ? "/data/user_de/%d/" : "/data/user/%d/";
        return String.format(format, userId);
    }
}
