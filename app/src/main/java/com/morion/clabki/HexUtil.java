package com.morion.clabki;

/**
 * Created by morion on 4/04/17.
 */

public class HexUtil {

    private final char[] hexCharacters =
            {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};

    public String byteToHexStr(byte mbyte) {
        char[] hexChars = new char[2];
        int value = mbyte & 0xFF;
        hexChars[0] = hexCharacters[value >>> 4];
        hexChars[1] = hexCharacters[value & 0x0F];
        return new String(hexChars);
    }

    public char[] byteArrayToHexCharArray(byte[] byteArray) {
        char[] hexChars = new char[byteArray.length * 2];
        int value;
        for(int i = 0; i < byteArray.length; i++) {
            value = byteArray[i] & 0xFF;
            hexChars[i * 2] = hexCharacters[value >>> 4];
            hexChars[i * 2 + 1] = hexCharacters[value & 0x0F];
        }
        return hexChars;
    }

    //Return an array with values like '0F', '1A', etc.
    public String[] byteArrayToHexStrArray(byte[] byteArray) {
        char[] twoBytes;
        char[] hexChars =  this.byteArrayToHexCharArray(byteArray);
        String[] hexStrings = new String[byteArray.length];
        for(int i = 0; i < byteArray.length; i++) {
            twoBytes = new char[]{hexChars[2 * i], hexChars[2 * i + 1]};
            String s = new String(twoBytes);
            hexStrings[i] = s;
        }
        return hexStrings;
    }

    public String byteArrayToHexStr(byte[] byteArray) {
        char[] hexChars = this.byteArrayToHexCharArray(byteArray);
        return new String(hexChars);
    }

    //Takes a string and returns a byte array with the respective values
    //Ex. "ff45" -> [0xff, 0x45]
    public byte[] hexStringToByteArray(String s) {
        final int len = s.length();
        byte[] data = new byte[len/2];
        for(int i = 0; i < len; i+=2) {
            data[i/2] = (byte) ((Character.digit(s.charAt(i),16) << 4) +
                    Character.digit(s.charAt(i+1),16));
        }
        return data;
    }
}
