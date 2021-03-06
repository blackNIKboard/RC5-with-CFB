package main;

import rc5.SequenceEncrypter;
import rc5.RC5;

import java.util.ArrayList;
import java.util.Arrays;

import static main.Analyzer.padBytes;
import static main.FileEncrypter.*;
import static main.StringGenerator.generateString;

public class CypherTester {
    private static final boolean debug = true;

    static void testECBImage() {
        String key = generateString(2, 255);
        String initVector = generateString(16);

        String rawImage = "image\\Tux.bmp";
        String encImage = "image\\encECBTux.bmp";
        String decImage = "image\\decECBTux.bmp";
        encryptFile(key, initVector, rawImage, encImage, debug, "ECB");
        decryptFile(key, initVector, encImage, decImage, debug, "ECB");

        if (Arrays.equals(readTxtBytes(rawImage), readTxtBytes(decImage))) {
            System.out.println("PASSED\n");
        }
    }

    static void testCFBImage() {
        String key = generateString(2, 255);
        String initVector = generateString(16);

        String rawImage = "image\\Tux.bmp";
        String encImage = "image\\encCFBTux.bmp";
        String decImage = "image\\decCFBTux.bmp";
        encryptFile(key, initVector, rawImage, encImage, debug, "CFB");
        decryptFile(key, initVector, encImage, decImage, debug, "CFB");

        if (Arrays.equals(readTxtBytes(rawImage), readTxtBytes(decImage))) {
            System.out.println("PASSED\n");
        }
    }

    static void testECBTxt() {
        String key = generateString(2, 255);
        String initVector = generateString(16);
        String rawTxt = "image\\number.txt";
        String encTxt = "image\\encECBNumber.txt";
        String decTxt = "image\\decECBNumber.txt";
        encryptFile(key, initVector, rawTxt, encTxt, debug, "ECB");
        decryptFile(key, initVector, encTxt, decTxt, debug, "ECB");

        if (Arrays.equals(readTxtBytes(rawTxt), readTxtBytes(decTxt))) {
            System.out.println("PASSED\n");
        }
    }

    static void testCFBTxt() {
        String key = generateString(2, 255);
        String initVector = generateString(16);
        String rawTxt = "image\\number.txt";
        String encTxt = "image\\encCFBNumber.txt";
        String decTxt = "image\\decCFBNumber.txt";
        encryptFile(key, initVector, rawTxt, encTxt, debug, "CFB");
        decryptFile(key, initVector, encTxt, decTxt, debug, "CFB");

        if (Arrays.equals(readTxtBytes(rawTxt), readTxtBytes(decTxt))) {
            System.out.println("PASSED\n");
        }
    }

    static void testCFB(int rounds) {
        System.out.println("Testing random strings enc/dec");
        String initVector = generateString(16);
        String key = generateString(2, 255);
        System.out.println("Key is: " + key);

        ArrayList<String> cases = new ArrayList<>();
        int minLength = 3;
        int maxLength = 53;

        for (int i = 0; i < rounds; i++) {
            cases.add(generateString(minLength, maxLength));
        }

        SequenceEncrypter test = new SequenceEncrypter(key.getBytes(), initVector);

        for (String arg : cases) {
            if (debug) {
                System.out.print(arg);
            }
            if (!Arrays.equals(test.decipherCFB(test.encipherCFB(arg.getBytes())), arg.getBytes())) {
                throw new RuntimeException("Mismatching for: " + arg);
            }
            if (debug) {
                System.out.println("  |  PASSED");
            }
        }
    }

    private static void manualTestCFB() {
        String initVector = generateString(16);
        String key1 = "sdjashfdkjagtejhwabkjgshag";
        var test32 = "tX5JrH7O".getBytes();
        var test128 = "tX5JrH7OiDXEpsrmtX5JrH7OiDXEpsrmtX5JrH7OiDXEpsrmtX5JrH7OiDXEpsrm".getBytes();
        RC5 test = new RC5(key1.getBytes(), 12);
        SequenceEncrypter test1 = new SequenceEncrypter(key1.getBytes(), initVector);
//        byte[] encBlock = test.encryptBlock(test64);
        byte[] encBlock = test1.encipherCFB(test32);
//        byte[] decBlock = test.decryptBlock(encBlock);
        byte[] decBlock = test1.decipherCFB(encBlock);

        System.out.println(new String(test128));
        System.out.println(new String(encBlock));
        System.out.println(new String(decBlock));
    }
}
