package uk.co.jakebreen.sendgridandroid;

import android.util.Base64;
import android.util.Base64OutputStream;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

class FileEncoder {

    private static final int BYTE_BUFFER_SIZE = 4096;

    static String encodeFileToBase64(File file) {
        try {
            InputStream inputStream = null;
            inputStream = new FileInputStream(file.getAbsolutePath());

            byte[] buffer = new byte[BYTE_BUFFER_SIZE];
            int bytesRead;
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            Base64OutputStream output64 = new Base64OutputStream(output, Base64.NO_WRAP);

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

}
