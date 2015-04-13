package com.echen.androidcommon.Crypto;

import com.echen.androidcommon.FileHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by echen on 2015/2/13.
 */
public class AESUtility {
    public static final String ALGORITHM = "AES";
    public static final int KEY_SIZE = 128;

    public static Cipher initAESCipher(String key, int cipherMode) {
        KeyGenerator keyGenerator = null;
        Cipher cipher = null;
        try {
            keyGenerator = KeyGenerator.getInstance(ALGORITHM);
            keyGenerator.init(KEY_SIZE, new SecureRandom(key.getBytes()));
            SecretKey secretKey = keyGenerator.generateKey();
            byte[] codeFormat = secretKey.getEncoded();
            SecretKeySpec secretKeySpec = new SecretKeySpec(codeFormat, ALGORITHM);
            cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(cipherMode, secretKey);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        return cipher;
    }

    public static File encryptFile(File sourceFile, String directory, String encryptedFileName, String key)
    {
        File encryptedfile = null;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = new FileInputStream(sourceFile);
            encryptedfile = FileHelper.createNewFile(directory, encryptedFileName);
            outputStream = new FileOutputStream(encryptedfile);
            Cipher cipher = initAESCipher(key, Cipher.ENCRYPT_MODE);
            CipherInputStream cipherInputStream = new CipherInputStream(inputStream, cipher);
            byte[] cache = new byte[1024];
            int nRead = 0;
            while ((nRead = cipherInputStream.read(cache)) > 0) {
                outputStream.write(cache, 0, nRead);
                outputStream.flush();
            }
            cipherInputStream.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        return encryptedfile;
    }

    public static File encryptFile(File sourceFile, String prefix, String fileSuffix,File directory, String key) {
        File encryptedfile = null;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = new FileInputStream(sourceFile);
            encryptedfile = File.createTempFile(prefix, fileSuffix,directory);
            outputStream = new FileOutputStream(encryptedfile);
            Cipher cipher = initAESCipher(key, Cipher.ENCRYPT_MODE);
            CipherInputStream cipherInputStream = new CipherInputStream(inputStream, cipher);
            byte[] cache = new byte[1024];
            int nRead = 0;
            while ((nRead = cipherInputStream.read(cache)) > 0) {
                outputStream.write(cache, 0, nRead);
                outputStream.flush();
            }
            cipherInputStream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        return encryptedfile;
    }

    public static File encryptFile(File sourceFile, String fileSuffix, String key) {
        return encryptFile(sourceFile,sourceFile.getName(), fileSuffix, null, key);
    }

    public static File decryptFile(File sourceFile, String directory, String decryptedFileName, String key)
    {
        File decryptedfile = null;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            decryptedfile = FileHelper.createNewFile(directory, decryptedFileName);
            Cipher cipher = initAESCipher(key, Cipher.DECRYPT_MODE);
            inputStream = new FileInputStream(sourceFile);
            outputStream = new FileOutputStream(decryptedfile);
            CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, cipher);
            byte[] cache = new byte[1024];
            int nRead = 0;
            while ((nRead = inputStream.read(cache)) >= 0) {
                cipherOutputStream.write(cache, 0, nRead);
            }
            cipherOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        return decryptedfile;
    }

    public static File decryptFile(File sourceFile, String prefix, String fileSuffix,File directory, String key)
    {
        File decryptedfile = null;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            decryptedfile = File.createTempFile(prefix, fileSuffix, directory);
            Cipher cipher = initAESCipher(key, Cipher.DECRYPT_MODE);
            inputStream = new FileInputStream(sourceFile);
            outputStream = new FileOutputStream(decryptedfile);
            CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, cipher);
            byte[] cache = new byte[1024];
            int nRead = 0;
            while ((nRead = inputStream.read(cache)) >= 0) {
                cipherOutputStream.write(cache, 0, nRead);
            }
            cipherOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        return decryptedfile;
    }

    public static File decryptFile(File sourceFile, String fileSuffix, String key) {
        return decryptFile(sourceFile,sourceFile.getName(), fileSuffix, null, key);
    }
}
