package com.example.ips;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileProcess {

    private static final String TAG = "FileUtils";

    public static void saveBytesToFile(Context context, byte[] bytes, String fileName) {
        File file;
        FileOutputStream fos = null;
        try {
            file = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName);
            fos = new FileOutputStream(file);
            fos.write(bytes);
            Log.d(TAG, "File saved: " + file.getAbsolutePath());
        } catch (IOException e) {
            Log.e(TAG, "Error saving file", e);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    Log.e(TAG, "Error closing FileOutputStream", e);
                }
            }
        }
    }
}
