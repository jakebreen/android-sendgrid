package uk.co.jakebreen.sendgridandroid;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Base64;
import android.util.Base64OutputStream;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

class FileEncoder {

    private static final int BYTE_BUFFER_SIZE = 4096;

    static String encodeFileToBase64(File file) {
        try {
            final InputStream inputStream = new FileInputStream(file.getAbsolutePath());
            final ByteArrayOutputStream output = new ByteArrayOutputStream();
            final Base64OutputStream output64 = new Base64OutputStream(output, Base64.NO_WRAP);

            byte[] buffer = new byte[BYTE_BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                output64.write(buffer, 0, bytesRead);
            }

            output64.close();
            return output.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    static String encodeFileToBase64(Context context, Uri uri) {
        try {
            final Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            if (cursor == null) return "";

            final int indexName = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            cursor.moveToFirst();

            final String fileName = cursor.getString(indexName);
            final File file = new File(context.getExternalCacheDir(), fileName);
            final OutputStream outputStream = new FileOutputStream(file);
            final InputStream inputStream = context.getContentResolver().openInputStream(uri);

            if (inputStream == null) return "";

            byte[] buffer = new byte[BYTE_BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            cursor.close();
            outputStream.close();
            inputStream.close();

            return encodeFileToBase64(file);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    static String getFileName(Context context, Uri uri) {
        String name = "";
        final Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        if (cursor != null) {
            final int indexName = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            cursor.moveToFirst();
            name = cursor.getString(indexName);
            cursor.close();
        }
        return name;
    }

}
