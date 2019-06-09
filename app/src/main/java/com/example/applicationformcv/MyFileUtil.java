package com.example.applicationformcv;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;


public class MyFileUtil {

    private static final String TAG = "MY-APP";


    public static File createTempFile(Context context, Uri uri, String type) {

        File tempFile = null;

        Log.d(TAG, "createTempFile: " + type);


        String[] parts = type.split("/");

        try {
            // DO FILE I/O IN A BACKGROUND THREAD

            InputStream is = context.getContentResolver().openInputStream(uri);

            // getApplicationContext().getCacheDir()        # App's temporary cache directory

            // create a temp file
            tempFile = File.createTempFile("doc", "." + parts[1], context.getCacheDir());

            FileOutputStream fos = new FileOutputStream(tempFile);

            IOUtils.copyLarge(is, fos);


            Log.d(TAG, "original: " + (tempFile.length() / 1024) + "KB");

            // close both the streams when done
            is.close();
            fos.close();

            // delete the temp file at the end
        }
        catch (Exception ex) {
            Log.d(TAG, "Error: " + ex.getMessage());
        }

        // return the temp file
        return tempFile;
    }



    public static boolean closeTempFile(Context context, String fileName) {
        if (context != null) {
            return context.deleteFile(fileName);
        }

        return false;
    }
}
