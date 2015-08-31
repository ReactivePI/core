package nl.fizzylogic.reactivepi.i2c;

public class Convert {
    public static int wordToInt16(byte[] data) {
        return ((data[0] & 0xFF) << 8) + (data[1] & 0xFF);
    }
}
