package main;

public class CorrelationAnalyzer {
    private final byte[] decryptedData;
    private final byte[] encryptedData;
    private int zeros;
    private int ones;
    private double correlation;

    public CorrelationAnalyzer(byte[] decryptedData, byte[] encryptedData) {
        this.encryptedData = encryptedData;
        this.decryptedData = decryptedData;
    }

    public double countCorrelation() {
        int count = 0;
        int size = decryptedData.length;
        for (int i = 0; i < size; i++) {
            int inputByte = decryptedData[i] & 0xFF;
            int outputByte = encryptedData[i] & 0xFF;
            for (int j = 0; j < 8; j++) {
                int tmpIn = (inputByte >>> j) & 1;
                int tmpOut = (outputByte >>> j) & 1;
                count += (2 * tmpIn - 1) * (2 * tmpOut - 1);
            }
        }
        double N = size * 8;

        correlation = count / N;
        return correlation;
    }

    public void countOnesAndZeros() {
        int sourceByte;
        int counterZeros = 0;
        int counterOnes = 0;

        for (byte encryptedDatum : encryptedData) {
            sourceByte = encryptedDatum & 0xFF;
            counterOnes = 0;
            for (; sourceByte != 0; sourceByte = (sourceByte >>> 1)) {
                if ((sourceByte & 1) == 1) {
                    counterOnes++;
                } else {
                    counterZeros++;
                }
            }
            ones += counterOnes;
            zeros += counterZeros;
        }
    }

    public double getCorrelation() {
        return correlation;
    }

    public int getZeros() {
        return zeros;
    }

    public int getOnes() {
        return ones;
    }
}
