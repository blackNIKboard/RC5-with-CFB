package rc5;

import java.util.ArrayList;
import java.util.Arrays;

import static rc5.CypherTools.*;

public class CFB {
    private RC5 cypher;
    private int rounds;
    private ArrayList<Block> blocks;
    private ArrayList<byte[]> bytes;

    public CFB(byte[] key, int cypherRounds, int CFBRounds) {
        cypher = new RC5(key, cypherRounds);
        rounds = CFBRounds;
    }

    public ArrayList<byte[]> encrypt(byte[] rawData) {
        bytes = new ArrayList<>();

        Block previous = null; //todo generate IV
        Block open;
        Block encrypted;

        for (int i = 0; i < rounds; i++) {
            open = initBlock(Arrays.copyOfRange(rawData, i * 16, i * 16 + 16));

            if (i == 0) {
                previous = xorBlock(open, cypher.encryptBlock(open));
            }

            encrypted = xorBlock(previous, cypher.encryptBlock(open));
            previous = encrypted;

            bytes.add(concatenateParts(encrypted));
        }

        return bytes;
    }

    public byte[] encipherCFB(byte[] text) {
        int blockNumber = text.length - 1;
        byte[] res = new byte[blockNumber+1];

        Block init = new Block();
        init.A = 55;
        init.B = 55;
        Block initVector = cypher.encryptBlock(init);

        for (int i = 0; i < blockNumber; ++i) {
            if (i > 0) {
                initVector = cypher.encryptBlock(initVector);
            }
            Block open = initBlock(Arrays.copyOfRange(text, i, i + 16));

            initVector.A = open.A ^ initVector.A;
            initVector.B = open.B ^ initVector.B;

            i = setBlocksAndGetIndex(res, i, initVector);
        }
//        res[res.length - 1] = blockDiff;
        return res;
    }

    public byte[] decipherCFB(byte[] text) {
        int blockNumber = text.length - 1;
        byte[] res = new byte[blockNumber+1];

        Block init = new Block();
        init.A = 55;
        init.B = 55;
        Block initVector = cypher.encryptBlock(init);

        for(int i = 0; i < blockNumber; ++i) {
            if (i > 0) {
                initVector = cypher.encryptBlock(initVector);
            }
            Block tempBlock = initBlock(Arrays.copyOfRange(text, i, i + 16));

            Block temp = new Block();
            temp.A = tempBlock.A ^ initVector.A;
            temp.B = tempBlock.B ^ initVector.B;

            initVector = tempBlock;

            i = setBlocksAndGetIndex(res, i, temp);
        }
//        res[res.length - 1] = blockDiff;
        return res;
    }

    public ArrayList<byte[]> decrypt(ArrayList<byte[]> encryptedData) {
        bytes = new ArrayList<>();

        Block previous = null; //todo generate IV
        Block open;
        Block encrypted;

        for (int i = 0; i < rounds; i++) {
            open = initBlock(encryptedData.get(i));

            if (i == 0) {
                previous = xorBlock(open, cypher.decryptBlock(open));
            }

            encrypted = xorBlock(previous, cypher.decryptBlock(open));
            previous = encrypted;

            bytes.add(concatenateParts(encrypted));
        }

        return bytes;
    }

    private static int setBlocksAndGetIndex(byte[] res, int pos, Block temp) {
        byte[] bytes = concatenateParts(temp);

        for (int i = 0; i < bytes.length; i++) {
            res[pos++] = bytes[i];
        }

        return pos - 1;
    }
}
