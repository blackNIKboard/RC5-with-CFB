package rc5;

import main.AnalysisTools;

import java.util.AbstractMap;
import java.util.Arrays;

import static rc5.CypherTools.*;

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
        P = getP();
        Q = getQ();

        S = mixSubKey();
    }

    public byte[] encryptBlock(byte[] rawBlock) {
        if (rawBlock.length != (2 * w / 8)) {
            throw new RuntimeException("Invalid block size");
        }

        Block block = initBlock(rawBlock);

        block = encryptBlock(block);

        return concatenateParts(block);
    }

    public Block encryptBlock(Block block) {
        block.A = ((block.A + S[0]) % (long) (Math.pow(2, w)));
        block.B = ((block.B + S[1]) % (long) (Math.pow(2, w)));
        for (int i = 1; i <= r; i++) {
            block.A = (rotateLeft(block.A ^ block.B, block.B)) + S[2 * i];
            block.B = (rotateLeft(block.B ^ block.A, block.A)) + S[2 * i + 1];
        }

        return block;
    }

    public AbstractMap.SimpleEntry<int[], byte[]> encryptBlockWithStats(byte[] rawBlock) {
        if (rawBlock.length != (2 * w / 8)) {
            throw new RuntimeException("Invalid block size");
        }

        Block block = initBlock(rawBlock);
        int[] changes = new int[16 * 8];
        Arrays.fill(changes, 0);

        byte[] src = concatenateParts(block);

        block.A = ((block.A + S[0]) % (long) (Math.pow(2, w)));
        block.B = ((block.B + S[1]) % (long) (Math.pow(2, w)));
        for (int i = 1; i <= r; i++) {
            block.A = (rotateLeft(block.A ^ block.B, block.B)) + S[2 * i];
            block.B = (rotateLeft(block.B ^ block.A, block.A)) + S[2 * i + 1];

            int[] iterationChanges = AnalysisTools.countChanges(src, concatenateParts(block));
            for (int j = 0; j < iterationChanges.length; j++) {
                changes[j] += iterationChanges[j];
            }
        }

        return new AbstractMap.SimpleEntry<>(changes, concatenateParts(block));
    }

    public byte[] decryptBlock(byte[] encBlock) {
        Block block = initBlock(encBlock);

        block = decryptBlock(block);

        return concatenateParts(block);
    }

    public Block decryptBlock(Block block) {
        for (int i = r; i >= 1; i--) {
            block.B = rotateRight(block.B - S[2 * i + 1], block.A) ^ block.A;
            block.A = rotateRight(block.A - S[2 * i], block.B) ^ block.B;
        }
        block.B = ((block.B - S[1]) % (long) (Math.pow(2, w)));
        block.A = ((block.A - S[0]) % (long) (Math.pow(2, w)));

        return block;
    }

    private long getP() {
        return 0xb7e151628aed2a6bL;
    }

    private long getQ() {
        return 0x9e3779b97f4a7c15L;
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
