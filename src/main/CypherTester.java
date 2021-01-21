package main;

import rc5.CFB;
import rc5.RC5;

import java.util.ArrayList;
import java.util.Arrays;

import static main.FileEncrypter.*;
import static main.StringGenerator.generateString;

public class CypherTester {
    private static final boolean debug = true;

    static void testImage() {
        String key = generateString(2, 255);
        String initVector = generateString(16);

        String rawImage = "C:\\Users\\blackNIKboard\\IdeaProjects\\untitled\\src\\main\\image\\Tux.bmp";
        String encImage = "C:\\Users\\blackNIKboard\\IdeaProjects\\untitled\\src\\main\\image\\encTux.bmp";
        String decImage = "C:\\Users\\blackNIKboard\\IdeaProjects\\untitled\\src\\main\\image\\decTux.bmp";
        encryptFile(key, initVector, rawImage, encImage, debug);
        decryptFile(key, initVector, encImage, decImage, debug);

        if (Arrays.equals(readTxtBytes(rawImage), readTxtBytes(decImage))) {
            System.out.println("PASSED\n");
        }

    }

    static void testTxt() {
        String key = generateString(2, 255);
        String initVector = generateString(16);
        String rawTxt = "C:\\Users\\blackNIKboard\\IdeaProjects\\untitled\\src\\main\\image\\number.txt";
        String encTxt = "C:\\Users\\blackNIKboard\\IdeaProjects\\untitled\\src\\main\\image\\encNumber.txt";
        String decTxt = "C:\\Users\\blackNIKboard\\IdeaProjects\\untitled\\src\\main\\image\\decNumber.txt";
        encryptFile(key, initVector, rawTxt, encTxt, debug);
        decryptFile(key, initVector, encTxt, decTxt, debug);

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

        CFB test = new CFB(key.getBytes(), initVector);

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
        CFB test1 = new CFB(key1.getBytes(), initVector);
//        byte[] encBlock = test.encryptBlock(test64);
        byte[] encBlock = test1.encipherCFB(test32);
//        byte[] decBlock = test.decryptBlock(encBlock);
        byte[] decBlock = test1.decipherCFB(encBlock);

        System.out.println(new String(test128));
        System.out.println(new String(encBlock));
        System.out.println(new String(decBlock));
    }
}
