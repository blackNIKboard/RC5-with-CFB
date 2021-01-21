package main;

import rc5.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Main {

    public static void main(String[] args) {
        // write your code here

//        testCFB(20);
    }

    private static void encryptFile(String filename){

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

    private static void manualTestCFB() {
        String key = "testo4kozhopa";
        String key1 = "sdjashfdkjagtejhwabkjgshag";
        var test32 = "tX5JrH7O".getBytes();
        var test64 = "tX5JrH7OiDXEpsrm".getBytes();
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
