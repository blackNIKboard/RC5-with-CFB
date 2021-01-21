package rc5;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class CypherTools {
    public static Block initBlock(byte[] rawBlock) {
        if (rawBlock.length != 16){
            throw new RuntimeException("sdsad");
        }
        Block block = new Block();

        block.A = convertToLong(Arrays.copyOfRange(rawBlock, 0, rawBlock.length / 2)); // divide parts
        block.B = convertToLong(Arrays.copyOfRange(rawBlock, rawBlock.length / 2, rawBlock.length));

        return block;
    }

    public static Block xorBlock(Block raw, Block processed) {
        Block result = new Block();

        result.A = processed.A ^ raw.A;
        result.B = processed.B ^ raw.B;

        return result;
    }

    public static byte[] concatenateParts(Block block) {
        byte[] A = longToBytes(block.A);
        byte[] B = longToBytes(block.B);
        int length = A.length;
        byte[] result = new byte[2 * length];

        System.arraycopy(A, 0, result, 0, length);
        System.arraycopy(B, 0, result, length, length);

        return result;
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

    public static byte[] longToBytes(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(x);
        return buffer.array();
    }
}
