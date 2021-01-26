package main;

import java.math.BigInteger;
import java.util.Arrays;

public class CorrelationAnalyzer {
    public static byte[] rotateRight(byte[] src) {
        byte[] result = new byte[src.length];

        byte lastBit;
        for (int i = 0; i < src.length; i++) {
            if (i == 0) {
                lastBit = (byte) ((int) src[src.length - 1] & 1);
            } else {
                lastBit = (byte) ((int) src[i - 1] & 1);
            }

            result[i] = (byte) ((((int) src[i] >> 1) & ~(byte) 128) | ((int) lastBit << 7));
        }

        return result;
    }

    public static double countCorrelation(byte[] first, byte[] second) {
        int count = 0;
        int size = first.length;
        for (int i = 0; i < size; i++) {
            int inputByte = first[i] & 0xFF;
            int outputByte = second[i] & 0xFF;
            for (int j = 0; j < 8; j++) {
                int tmpIn = (inputByte >>> j) & 1;
                int tmpOut = (outputByte >>> j) & 1;
//                count += (2 * tmpIn - 1) * (2 * tmpOut - 1);
                if (tmpIn == tmpOut) count++;
            }
        }
        double N = size * 8;

        return (count - (N - count)) / (N);
    }

//    private int correlation(BigInteger first, BigInteger second, int N) {
//        double result = 0;
//        int count = 0;
//        int count1 = 0;
//        BigInteger test = BigInteger.valueOf(1);
//
//        for (int i = 0; i < N; i++) {
//            if (first.testBit(i) == second.testBit(i)) count++;
////            if(first.shiftRight(i).and(test).equals(second.shiftRight(i).and(test))) count1++;
//        }
//
////        BitSet one = BitSet.valueOf(encryptedData);
////        BitSet two = BitSet.valueOf(decryptedData);
////
////        for (int i = 0; i < one.length(); i++) {
////            if (one.get(i) == two.get(i)) count1++;
////        }
//
////        int dispCounter = (first.bitCount()) - count;
////        cout << "Coincidence Counter: " << coincCounter << endl;
////        System.out.println("Coincidence Counter: " + count);
////        cout << "Discrepancy Counter: " << dispCounter << endl;
////        System.out.println("Discrepancy Counter: " + dispCounter);
////        System.out.println("TEST(: " + (double) count1 / one.length());
//        return count;
//    }

    public static double[] autoCorrelation(byte[] data) {
        int size = data.length;
        double[] corelTest = new double[size * 8 + 1];
        byte[] shifted = Arrays.copyOf(data, data.length);

        for (int k = 0; k <= size * 8; k++) {
            corelTest[k] = countCorrelation(shifted, data);
            shifted = rotateRight(shifted);
        }

        System.out.println(Arrays.toString(corelTest));

        return corelTest;
    }

    public static int countOnes(byte[] data) {
        int result = 0;
        int sourceByte;
        int counter;

        for (byte encryptedDatum : data) {
            sourceByte = encryptedDatum & 0xFF;
            counter = 0;

            for (int i = 0; i < 8; i++) {
                sourceByte = (sourceByte >>> 1);
                if ((sourceByte & 1) == 1) {
                    counter++;
                }
            }
            result += counter;
        }

        return result;
    }

    public static int countZeros(byte[] data) {
        int result = 0;
        int sourceByte;
        int counter;

        for (byte encryptedDatum : data) {
            sourceByte = encryptedDatum & 0xFF;
            counter = 0;

            for (int i = 0; i < 8; i++) {
                sourceByte = (sourceByte >>> 1);
                if ((sourceByte & 1) == 0) {
                    counter++;
                }
            }
            result += counter;
        }

        return result;
    }
}
