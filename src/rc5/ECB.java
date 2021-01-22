package rc5;

import java.util.ArrayList;
import java.util.Arrays;

import static rc5.CypherTools.concatenateParts;
import static rc5.CypherTools.initBlock;

public class ECB {
    private RC5 cypher;
    private byte pad = 0x01;

    public ECB(byte[] key) {
        cypher = new RC5(key, 12);
    }

    public byte[] encipherECB(byte[] text) {
        int paddingSize = (16 - (text.length % 16)) % 16;
        if (paddingSize != 0) {
            text = Arrays.copyOf(text, text.length + paddingSize);
            for (int i = text.length - 1; i >= text.length - paddingSize; i--) {
                text[i] = pad;
            }
        }

        int blockNumber = text.length - 1;
        byte[] res = new byte[blockNumber + 1];

        for (int i = 0; i < blockNumber; ++i) {
            Block block = initBlock(Arrays.copyOfRange(text, i, i + 16));

            block = cypher.encryptBlock(block);

            i = setBlocksAndGetIndex(res, i, block);
        }

        return res;
    }

    public byte[] decipherECB(byte[] text) {
        int paddingSize = 0;
        int blockNumber = text.length - 1;
        byte[] res = new byte[blockNumber + 1];

        for (int i = 0; i < blockNumber; ++i) {
            Block block = initBlock(Arrays.copyOfRange(text, i, i + 16));

            block = cypher.decryptBlock(block);

            i = setBlocksAndGetIndex(res, i, block);
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
