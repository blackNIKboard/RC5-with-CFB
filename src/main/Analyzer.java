package main;

import rc5.RC5;
import rc5.SequenceEncrypter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.StringJoiner;

import static main.StringGenerator.generateString;

public class Analyzer {
    static void testAutoCorrelation(byte[] key, byte[] data, String filename) {
        if (data.length > 16) {
            throw new RuntimeException("data's too big for one block");
        }
        System.out.println("Generating autocorrelation dataset");
        RC5 test = new RC5(key, 12);

        byte[] decrypted = padBytes(data);
        byte[] encrypted = test.encryptBlock(decrypted);

        double[] autoCorrelation = CorrelationAnalyzer.autoCorrelation(encrypted);

        StringBuilder autoCorrelationResult = new StringBuilder();
        for (int i = 0; i < autoCorrelation.length; i++) {
            autoCorrelationResult.append(i).append(" ").append(autoCorrelation[i]).append("\n");
        }

        String frequencyResult = CorrelationAnalyzer.countOnes(encrypted) + " " +
                CorrelationAnalyzer.countZeros(encrypted);

        FileEncrypter.writeTxtBytes(frequencyResult.getBytes(), "freq"+filename);
        FileEncrypter.writeTxtBytes(autoCorrelationResult.toString().getBytes(), filename);
    }

    static void testCorrelation() {
        System.out.println("Calculating correlation and bits");
        String initVector = generateString(16);
        String key = generateString(2, 255);
        SequenceEncrypter test = new SequenceEncrypter(key.getBytes(), initVector);

//        byte[] decrypted = generateString(40, 100).getBytes();
        byte[] decrypted = generateString(16).getBytes();
        decrypted = padBytes(decrypted);
        byte[] encrypted = test.encipherCFB(decrypted);

        System.out.println("Number of ones: " + CorrelationAnalyzer.countOnes(encrypted));
        System.out.println("Number of zeros: " + CorrelationAnalyzer.countZeros(encrypted));
        System.out.println("Correlation: " + CorrelationAnalyzer.countCorrelation(decrypted, encrypted));
    }

    static void errorDistribution(int rounds) {
        System.out.println("Testing error distribution enc/dec");
        String initVector = generateString(16);
        String key = generateString(2, 255);
        System.out.println("Key is: " + key);

        SequenceEncrypter test = new SequenceEncrypter(key.getBytes(), initVector);

        ArrayList<String> cases = new ArrayList<>(); // generate raw strings for cases
        int minLength = 77;
        int maxLength = 78;

        for (int i = 0; i < rounds; i++) {
            cases.add(generateString(minLength, maxLength));
        }

        for (int i = 0; i < rounds; i++) {
            System.out.println("\n-- Round " + (i + 1));
            byte[] original = cases.get(i).getBytes(); // original
            System.out.println("Original:  " + getBlockRepresentation(original, true));

            byte[] ciphered = test.encipherCFB(cases.get(i).getBytes()); // encrypted
            System.out.println("Encrypted: " + getBlockRepresentation(ciphered, false));

            int corruptedBlock = new Random().nextInt(ciphered.length / 16); // corrupted
            int corruptedBit = new Random().nextInt(16);
            byte[] error = new byte[1];
            System.out.println("Corrupting bit " + corruptedBit + " in block " + (corruptedBlock + 1));

            new Random().nextBytes(error);
            ciphered[corruptedBlock * 16 + corruptedBit] = error[0];

            System.out.println("Corrupted: " + getBlockRepresentation(ciphered, false));

            byte[] deciphered = test.decipherCFB(ciphered); // decrypted
            System.out.println("Decrypted: " + getBlockRepresentation(deciphered, true) + "\n");

            analyseErrors(original, deciphered);
        }
    }

    private static String getBlockRepresentation(byte[] raw, boolean chars) {
        raw = padBytes(raw);

        StringJoiner joiner = new StringJoiner(" | ");
        for (int i = 0; i < raw.length; i += 16) {
            byte[] temp = Arrays.copyOfRange(raw, i, i + 16);
            if (chars) {
                joiner.add(new String(temp));
            } else {
                joiner.add(Arrays.toString(temp));
            }
        }

        return joiner.toString();
    }

    static byte[] padBytes(byte[] raw) {
        int paddingSize = (16 - (raw.length % 16)) % 16;
        if (paddingSize != 0) {
            raw = Arrays.copyOf(raw, raw.length + paddingSize);
            for (int i = raw.length - 1; i >= raw.length - paddingSize; i--) {
                raw[i] = 0x01;
            }
        }

        return raw;
    }

    private static void analyseErrors(byte[] original, byte[] deciphered) {
        original = padBytes(original);
        deciphered = padBytes(deciphered);

        int blockNumber = original.length / 16;
        int[] errorsForBlock = new int[blockNumber];

        for (int i = 0; i < Math.max(original.length, deciphered.length); i++) {
            if (original[i] != deciphered[i]) {
                errorsForBlock[(i) / 16]++;
            }
        }
        for (int i = 0; i < errorsForBlock.length; i++) {
            System.out.println("Errors for block " + (i + 1) + ": " + errorsForBlock[i]);
        }
    }
}
