package com.echen.androidcommon;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;

/**
 * Created by echen on 2015/2/15.
 */
public class FileHelper {
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

    public static File createNewFile(String directory, String fileName)
    {
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

    public static void DeleteFile(File file){
        if(file.isFile()){
            file.delete();
            return;
        }
        if(file.isDirectory()){
            File[] childFile = file.listFiles();
            if(childFile == null || childFile.length == 0){
                file.delete();
                return;
            }
            for(File f : childFile){
                DeleteFile(f);
            }
            file.delete();
        }
    }
}
