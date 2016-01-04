package kr.pe.kwonnam.spymemcached.extratranscoders;

public class IntToBytesUtils {
    public static final int INT_TO_BYTES_LENGTH = 4;

    /**
     * int value to 4 bytes array.
     */
    public static byte[] intToBytes(int value) {
        return new byte[]{(byte) (value >>> 24), (byte) (value >>> 16), (byte) (value >>> 8), (byte) (value)};
    }

    /**
     * 4 bytes array to int
     */
    public static int bytesToInt(byte[] bytes) {
        int value = ((int) bytes[0] & 0xFF) << 24;
        value += ((int) bytes[1] & 0xFF) << 16;
        value += ((int) bytes[2] & 0xFF) << 8;
        value += (int) bytes[3] & 0xFF;
        return value;
    }

}
