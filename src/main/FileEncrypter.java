package main;

import rc5.CFB;

import java.io.*;
import java.util.Arrays;

import static main.StringGenerator.generateString;

public class FileEncrypter {
     static void encryptFile(String key, String initVector, String inputFilename, String outputFilename, boolean debug) {
        byte[] rawBytes = readTxtBytes(inputFilename);
        if (key == null) {
            System.out.println("Generating key");
            key = generateString(2, 255);
        }
        if (initVector == null || initVector.getBytes().length != 16) {
            throw new RuntimeException("Invalid initialization vector");
        }

        CFB encrypter = new CFB(key.getBytes(), initVector);


        if (inputFilename.contains(".txt")) {
            System.out.println("Encrypting txt file");
            if (debug) {
                System.out.println("Key: " + key);
                System.out.println("Content: " + new String(rawBytes) + "\n");
            }

            byte[] encrypted = encrypter.encipherCFB(rawBytes);
            writeTxtBytes(encrypted, outputFilename);

            return;
        }
        if (inputFilename.contains(".bmp")) {
            System.out.println("Encrypting bmp file");
            if (debug) {
                System.out.println("Key: " + key + "\n");
//                System.out.println("Content: " + new String(rawBytes) + "\n");
            }

            byte[] header = Arrays.copyOfRange(rawBytes, 0, 81);
            byte[] img = Arrays.copyOfRange(rawBytes, 81, rawBytes.length);
            byte[] encrypted = encrypter.encipherCFB(img);
            byte[] result = new byte[header.length + encrypted.length];

            System.arraycopy(header, 0, result, 0, header.length);
            System.arraycopy(encrypted, 0, result, header.length, encrypted.length);

            writeTxtBytes(result, outputFilename);
        }

    }

     static void decryptFile(String key, String initVector, String inputFilename, String outputFilename, boolean debug) {
        byte[] rawBytes = readTxtBytes(inputFilename);
        CFB encrypter = new CFB(key.getBytes(), initVector);
        if (key == null) {
            throw new RuntimeException("Key missing");
        }
        if (initVector == null || initVector.getBytes().length != 16) {
            throw new RuntimeException("Invalid initialization vector");
        }

        if (inputFilename.contains(".txt")) {
            System.out.println("Decrypting txt file");
            if (debug) {
                System.out.println("Key: " + key);
                System.out.println("Content: " + new String(rawBytes));
            }
            byte[] decrypted = encrypter.decipherCFB(rawBytes);
            writeTxtBytes(decrypted, outputFilename);

            return;
        }
        if (inputFilename.contains(".bmp")) {
            System.out.println("Decrypting bmp file");
            if (debug) {
                System.out.println("Key: " + key);
//                System.out.println("Content: " + new String(rawBytes));
            }

            byte[] header = Arrays.copyOfRange(rawBytes, 0, 81);
            byte[] img = Arrays.copyOfRange(rawBytes, 81, rawBytes.length);

//            Arrays.fill(img, (byte) 0xf); // test filling
            byte[] decrypted = encrypter.decipherCFB(img);
            byte[] result = new byte[header.length + decrypted.length];

            System.arraycopy(header, 0, result, 0, header.length);
            System.arraycopy(decrypted, 0, result, header.length, decrypted.length);

            writeTxtBytes(result, outputFilename);
        }
    }

     static byte[] readTxtBytes(String filename) {
        File file = new File(filename);
        byte[] fileContent = null;
        FileInputStream fin = null;
        try {
            fin = new FileInputStream(file);
            fileContent = new byte[(int) file.length()];
            // Reads an array of bytes.
            fin.read(fileContent);
        } catch (FileNotFoundException e) {
            System.out.println("File not found" + e);
        } catch (IOException ioe) {
            System.out.println("Exception while reading file " + ioe);
        } finally {
            // close streams
            try {
                if (fin != null) {
                    fin.close();
                }
            } catch (IOException ioe) {
                System.out.println("Error while closing stream: " + ioe);
            }
        }

        assert fileContent != null;
        return fileContent;
    }

     static void writeTxtBytes(byte[] bytes, String filename) {
        try (FileOutputStream fos = new FileOutputStream(filename)) {
            fos.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
