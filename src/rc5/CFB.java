package rc5;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

import static rc5.CypherTools.*;

public class CFB {
    private RC5 cypher;
    private ArrayList<byte[]> bytes;
    private byte pad = 0x01;
    private byte[] initializationVector;

    public CFB(byte[] key, String initializationVector) {
        cypher = new RC5(key, 12);
        this.initializationVector = initializationVector.getBytes();
    }

    public byte[] encipherCFB(byte[] text) {
        int paddingSize = (16 - (text.length % 16)) % 16;
        if (paddingSize != 0) {
            text = Arrays.copyOf(text, text.length + paddingSize);
            for (int i = text.length - 1; i >= text.length - paddingSize; i--) {
                text[i] = pad;
            }
        }

        int blockNumber = text.length - 1;
        byte[] res = new byte[blockNumber + 1];

//        Block init = new Block();
//        init.A = 55;
//        init.B = 55;
        Block initVector = cypher.encryptBlock(initBlock(initializationVector)); // TODO InitVector randomize

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
        int paddingSize = 0;
        int blockNumber = text.length - 1;
        byte[] res = new byte[blockNumber + 1];

//        Block init = new Block();
//        init.A = 55;
//        init.B = 55;
        Block initVector = cypher.encryptBlock(initBlock(initializationVector));

        for (int i = 0; i < blockNumber; ++i) {
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

        while (res[res.length - paddingSize - 1] == pad) {
            paddingSize++;
        }
        res = Arrays.copyOf(res, res.length - paddingSize);

        return res;
    }

    private static int setBlocksAndGetIndex(byte[] res, int pos, Block temp) {
        byte[] bytes = concatenateParts(temp);

        for (int i = 0; i < bytes.length; i++) {
            res[pos++] = bytes[i];
        }

        return pos - 1;
    }
}
