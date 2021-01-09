package rc5;

import java.util.Arrays;
import java.nio.ByteBuffer;

public class RC5 {
    private long P; // magic P
    private long Q; // magic Q
    private int w; // word size
    private int r; // rounds number
    private int b; // key size
    private byte[] rawKey; // user key
    private long[] S; // subkey
    private Block[] blocks;

    private int c; // L iterations
    private int t; // S iterations

    public RC5(byte[] key, int rounds) {
        if (key.length > 255) {
            throw new RuntimeException("Key length > 255");
        }
        if (rounds > 255 || rounds < 0) {
            throw new RuntimeException("Invalid round number");
        }

        rawKey = key;
        w = 64;
        r = rounds;
        b = key.length;
        P = getP(w);
        Q = getQ(w);

        S = mixSubKey();
    }

//    public byte[] encrypt(byte[] raw){
//
//    }

    public byte[] encryptBlock(byte[] rawBlock) {
        if (rawBlock.length != (2 * w / 8)) {
            throw new RuntimeException("Invalid block size");
        }

        Block block = initBlock(rawBlock);

        block.A = ((block.A + S[0]) % (long) (Math.pow(2, w)));
        block.B = ((block.B + S[1]) % (long) (Math.pow(2, w)));
        for (int i = 1; i <= r; i++) {
            block.A = (rotateLeft(block.A ^ block.B, block.B)) + S[2 * i];
            block.B = (rotateLeft(block.B ^ block.A, block.A)) + S[2 * i + 1];
        }

        return concatenateParts(block);
    }

    public byte[] decryptBlock(byte[] encBlock) {
        Block block = initBlock(encBlock);

        for (int i = r; i >= 1; i--) {
            block.B = rotateRight(block.B - S[2 * i + 1], block.A) ^ block.A;
            block.A = rotateRight(block.A - S[2 * i], block.B) ^ block.B;
        }
        block.B = ((block.B - S[1]) % (long) (Math.pow(2, w)));
        block.A = ((block.A - S[0]) % (long) (Math.pow(2, w)));

        return concatenateParts(block);
    }

    private byte[] concatenateParts(Block block){
        byte[] A = longToBytes(block.A);
        byte[] B = longToBytes(block.B);
        int length = A.length;
        byte[] result = new byte[2*length];

        System.arraycopy(A, 0, result, 0, length);
        System.arraycopy(B, 0, result, length, length);

        return result;
    }

    private Block initBlock(byte[] rawBlock) {
        Block block = new Block();

        block.A = convertToLong(Arrays.copyOfRange(rawBlock, 0, rawBlock.length / 2)); // divide parts
        block.B = convertToLong(Arrays.copyOfRange(rawBlock, rawBlock.length / 2, rawBlock.length));

        return block;
    }

    public static long rotateRight(long i, long distance) {
        return (i >>> distance) | (i << -distance);
    }

    public static long rotateLeft(long i, long distance) {
        return (i << distance) | (i >>> -distance);
    }

    public static long convertToLong(byte[] array) {
        ByteBuffer buffer = ByteBuffer.wrap(array);
        return buffer.getLong();
    }

    public byte[] longToBytes(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(x);
        return buffer.array();
    }

    private long getP(int w) { // TODO remove 16-32 or add support
        return switch (w) {
            case 16 -> 0xb7e1;
            case 32 -> 0xb7e15163;
            case 64 -> 0xb7e151628aed2a6bL;
            default -> throw new RuntimeException("Invalid word size in P");
        };
    }

    private long getQ(int w) { // TODO remove 16-32 or add support
        return switch (w) {
            case 16 -> 0x9e37;
            case 32 -> 0x9e3779b9;
            case 64 -> 0x9e3779b97f4a7c15L;
            default -> throw new RuntimeException("Invalid word size in Q");
        };
    }

    private long[] mixSubKey() {
        int i = 0, j = 0;
        long A = 0, B = 0;

        long[] L = initL();
        long[] S = initS();

        for (int k = 0; k < 3 * Math.max(t, c); k++) {
            A = S[i] = (S[i] + A + B) << 3;
            B = L[j] = (L[j] + A + B) << (A + B);
            i = (i + 1) % t;
            j = (j + 1) % c;
        }

        return S;
    }

    private long[] initS() {
        t = 2 * (r + 1);
        long[] S = new long[t];

        S[0] = P;
        for (int i = 1; i < t; i++) {
            S[i] = S[i - 1] + Q;
        }

        return S;
    }

    private long[] initL() {
        int u = w / 8;
        c = b % u > 0 ? b / u + 1 : b / u;
        long[] L = new long[c];

        for (int i = b - 1; i >= 0; i--) {
            L[i / u] = (L[i / u] << 8) + rawKey[i];
        }

        return L;
    }
}
