package main;

import rc5.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Main {
    private static final boolean debug = true;

    public static void main(String[] args) throws FileNotFoundException {
        // write your code here
        testImage();
        testTxt();
//        testCFB(20);
    }

    private static void testImage() throws FileNotFoundException {
        String key = generateString(2, 255);
        String rawImage = "C:\\Users\\blackNIKboard\\IdeaProjects\\untitled\\src\\main\\image\\Tux.bmp";
        String encImage = "C:\\Users\\blackNIKboard\\IdeaProjects\\untitled\\src\\main\\image\\encTux.bmp";
        String decImage = "C:\\Users\\blackNIKboard\\IdeaProjects\\untitled\\src\\main\\image\\decTux.bmp";
        encryptFile(key, rawImage, encImage, debug);
        decryptFile(key, encImage, decImage, debug);

        if (Arrays.equals(readTxtBytes(rawImage), readTxtBytes(decImage))) {
            System.out.println("PASSED\n");
        }

    }

    private static void testTxt() throws FileNotFoundException {
        String key = generateString(2, 255);
        String rawTxt = "C:\\Users\\blackNIKboard\\IdeaProjects\\untitled\\src\\main\\image\\number.txt";
        String encTxt = "C:\\Users\\blackNIKboard\\IdeaProjects\\untitled\\src\\main\\image\\encNumber.txt";
        String decTxt = "C:\\Users\\blackNIKboard\\IdeaProjects\\untitled\\src\\main\\image\\decNumber.txt";
        encryptFile(key, rawTxt, encTxt, debug);
        decryptFile(key, encTxt, decTxt, debug);

        if (Arrays.equals(readTxtBytes(rawTxt), readTxtBytes(decTxt))) {
            System.out.println("PASSED\n");
        }

    }

    private static void encryptFile(String key, String inputFilename, String outputFilename, boolean debug) {
        byte[] rawBytes = readTxtBytes(inputFilename);
        if (key == null) {
            System.out.println("Generating key");
            key = generateString(2, 255);
        }
        CFB encrypter = new CFB(key.getBytes(), 12);


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

    private static void decryptFile(String key, String inputFilename, String outputFilename, boolean debug) throws FileNotFoundException {
        byte[] rawBytes = readTxtBytes(inputFilename);
        CFB encrypter = new CFB(key.getBytes(), 12);

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

    private static byte[] readTxtBytes(String filename) {
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

    private static void writeTxtBytes(byte[] bytes, String filename) {
        try (FileOutputStream fos = new FileOutputStream(filename)) {
            fos.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void testCFB(int rounds) {
        String key = generateString(2, 255);
        System.out.println("Key is: " + key);

        ArrayList<String> cases = new ArrayList<>();
        int minLength = 3;
        int maxLength = 53;

        for (int i = 0; i < rounds; i++) {
            cases.add(generateString(minLength, maxLength));
        }

        CFB test = new CFB(key.getBytes(), 12);

        for (String arg : cases) {
            System.out.print(arg);
            if (!Arrays.equals(test.decipherCFB(test.encipherCFB(arg.getBytes())), arg.getBytes())) {
                throw new RuntimeException("Mismatching for: " + arg);
            }
            System.out.println("  |  PASSED");
        }
    }

    private static String generateString(int minLength, int maxLength) {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = minLength + new Random().nextInt(Math.abs(maxLength - minLength));
        Random random = new Random();
        StringBuilder buffer = new StringBuilder(targetStringLength);
        for (int j = 0; j < targetStringLength; j++) {
            int randomLimitedInt = leftLimit + (int)
                    (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }

        return buffer.toString();
    }

    private static String generateString(int length) {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = new Random().nextInt(length);
        Random random = new Random();
        StringBuilder buffer = new StringBuilder(targetStringLength);
        for (int j = 0; j < targetStringLength; j++) {
            int randomLimitedInt = leftLimit + (int)
                    (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }

        return buffer.toString();
    }

    private static void manualTestCFB() {
        String key1 = "sdjashfdkjagtejhwabkjgshag";
        var test32 = "tX5JrH7O".getBytes();
        var test128 = "tX5JrH7OiDXEpsrmtX5JrH7OiDXEpsrmtX5JrH7OiDXEpsrmtX5JrH7OiDXEpsrm".getBytes();
        RC5 test = new RC5(key1.getBytes(), 12);
        CFB test1 = new CFB(key1.getBytes(), 12);
//        byte[] encBlock = test.encryptBlock(test64);
        byte[] encBlock = test1.encipherCFB(test32);
//        byte[] decBlock = test.decryptBlock(encBlock);
        byte[] decBlock = test1.decipherCFB(encBlock);

        System.out.println(new String(test128));
        System.out.println(new String(encBlock));
        System.out.println(new String(decBlock));
    }
}
