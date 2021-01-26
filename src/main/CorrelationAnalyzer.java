package main;

import java.util.AbstractMap;
import java.util.Arrays;

import static java.lang.Math.pow;

public class CorrelationAnalyzer {
    public static int[] countChanges(byte[] src, byte[] dst) {
        int[] changes = new int[16 * 8];
        Arrays.fill(changes, 0);

        String bitsSrc = toBitString(src);
        String bitsDst = toBitString(dst);

        for (int i = 0; i < 16 * 8; i++) {
            if (bitsSrc.charAt(i) == bitsDst.charAt(i)) {
                changes[i]++;
            }
        }

        return changes;
    }

    public static AbstractMap.SimpleEntry<Double, Boolean> performSeriesTest(byte[] src) {
        int L = 2;
        int n0 = 0; // 101
        int n1 = 0; // 010
        int n2 = 0; // 1001
        int n3 = 0; // 0110
        String bits = toBitString(src);

        for (int i = 0; i < bits.length() - 2; i++) {
            if (bits.charAt(i) == '0' && bits.charAt(i + 1) == '1' && bits.charAt(i + 2) == '0') {
                n0++;
            } else if ((bits.charAt(i) == '1' && bits.charAt(i + 1) == '0' && bits.charAt(i + 2) == '1')) {
                n1++;
            }
        }
        for (int i = 0; i < bits.length() - 3; i++) {
            if (bits.charAt(i) == '0' && bits.charAt(i + 1) == '1' && bits.charAt(i + 2) == '1' && bits.charAt(i + 3) == '0') {
                n2++;
            } else if (bits.charAt(i) == '1' && bits.charAt(i + 1) == '0' && bits.charAt(i + 2) == '0' && bits.charAt(i + 3) == '1') {
                n3++;
            }
        }

        double f = pow(n0 - (bits.length() / pow(2, 3)), 2) / (bits.length() / pow(2, 3)) +
                pow(n1 - (bits.length() / pow(2, 3)), 2) / (bits.length() / pow(2, 3)) +
                pow(n2 - (bits.length() / pow(2, 4)), 2) / (bits.length() / pow(2, 4)) +
                pow(n3 - (bits.length() / pow(2, 4)), 2) / (bits.length() / pow(2, 4));

        final double Xi2 = 5.99; //Pe = 0.2, L = 2

        if (f < Xi2) {
            return new AbstractMap.SimpleEntry<>(f, true);
        } else {
            return new AbstractMap.SimpleEntry<>(f, false);
        }
    }

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
                count += (2 * tmpIn - 1) * (2 * tmpOut - 1);
//                if (tmpIn == tmpOut) count++;
            }
        }
        double N = size * 8;

//        return (count - (N - count)) / (N);
        return count / N;
    }

    public static double[] autoCorrelation(byte[] data) {
        int size = data.length;
        double[] corelTest = new double[size * 8 + 1];
        byte[] shifted = Arrays.copyOf(data, data.length);

        for (int k = 0; k <= size * 8; k++) {
            corelTest[k] = countCorrelation(shifted, data);
            shifted = rotateRight(shifted);
        }

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

    public static String toBitString(final byte[] b) {
        final char[] bits = new char[8 * b.length];
        for (int i = 0; i < b.length; i++) {
            final byte byteval = b[i];
            int bytei = i << 3;
            int mask = 0x1;
            for (int j = 7; j >= 0; j--) {
                final int bitval = byteval & mask;
                if (bitval == 0) {
                    bits[bytei + j] = '0';
                } else {
                    bits[bytei + j] = '1';
                }
                mask <<= 1;
            }
        }
        return String.valueOf(bits);
    }
}
