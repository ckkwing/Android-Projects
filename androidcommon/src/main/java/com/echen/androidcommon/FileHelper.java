package com.echen.androidcommon;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;

/**
 * Created by echen on 2015/2/15.
 */
public class FileHelper {

    public static boolean isExist(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }

    public static boolean copyFile(File fromFile, File toFile) {
        boolean bRel = true;
        try {
            FileInputStream inputStream = new FileInputStream(fromFile);
            FileOutputStream outputStream = new FileOutputStream(toFile);
            byte bt[] = new byte[1024];
            int c;
            while ((c = inputStream.read(bt)) > 0) {
                outputStream.write(bt, 0, c); //Write content into new file
            }
            inputStream.close();
            outputStream.close();
        } catch (FileNotFoundException e) {
            bRel = false;
            e.printStackTrace();
        } catch (IOException e) {
            bRel = false;
            e.printStackTrace();
        }
        return bRel;
    }

    public static File createNewFile(String directory, String fileName) {
        File newFile = null;
        File tmpDirFile = new File(directory);
        if (tmpDirFile == null) {
            String tmpDir = System.getProperty("java.io.tmpdir", ".");
            tmpDirFile = new File(tmpDir);
        }
        try {
            do {
                newFile = new File(tmpDirFile, fileName);
                if (newFile.exists())
                    newFile.delete();
            } while (!newFile.createNewFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return newFile;
    }

    public static void deleteFile(File file) {
        if (file.isFile()) {
            file.delete();
            return;
        }
        if (file.isDirectory()) {
            File[] childFile = file.listFiles();
            if (childFile == null || childFile.length == 0) {
                file.delete();
                return;
            }
            for (File f : childFile) {
                deleteFile(f);
            }
            file.delete();
        }
    }

    public static StringBuffer readFromFile(String filePath) {
        FileInputStream inputStream = null;
        StringBuffer strBuffer = new StringBuffer();
        try {
            inputStream = new FileInputStream(filePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String line = null;
        BufferedReader bufferReader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            while ((line = bufferReader.readLine()) != null) {
                strBuffer.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return strBuffer;
    }

    public static boolean writeToFile(String strData, String filePath) {
        boolean bRel = true;
        try {
            BufferedReader reader = new BufferedReader(new StringReader(strData));
            BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
            int len = 0;
            char[] buffer = new char[1024];
            while ((len = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, len);
            }
            writer.flush();
            writer.close();
            reader.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            bRel = false;
        }
        return bRel;
    }
}
