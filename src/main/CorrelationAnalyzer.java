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

    public void countOnes() {
        int result = 0;
        int sourceByte;
        int counter;

        for (byte encryptedDatum : encryptedData) {
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
        ones = result;
    }

    public void countZeros(){
        int result = 0;
        int sourceByte;
        int counter;

        for (byte encryptedDatum : encryptedData) {
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
        zeros = result;
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
