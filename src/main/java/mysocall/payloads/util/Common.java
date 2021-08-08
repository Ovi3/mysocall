package mysocall.payloads.util;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;

public class Common {
    public static String randString(int length) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        int total = str.length();
        Random random = new Random();
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(total);
            stringBuffer.append(str.charAt(number));
        }
        return stringBuffer.toString();
    }

    public static byte[] replaceBytes(byte[] source, byte[] oldBytes, byte[] newBytes) {
        return replaceBytesN(source, oldBytes, newBytes, -1);
    }

    public static byte[] replaceBytesN(byte[] source, byte[] oldBytes, byte[] newBytes, int replaceCount){
        if(oldBytes == null || oldBytes.length == 0 || replaceCount == 0) {
            return Arrays.copyOfRange(source, 0, source.length);
        }
        if(newBytes == null) {
            newBytes = new byte[0];
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        int i = 0;
        int replace = 0;
        while(i < source.length){
            boolean equal = true;
            if (i + oldBytes.length <= source.length) {
                for(int j = 0; j < oldBytes.length; j++){
                    if(source[i+j] != oldBytes[j]){
                        equal = false;
                        break;
                    }
                }
            } else {
                equal = false;
            }
            if(equal) {
                for (byte newByte : newBytes) {
                    bos.write(newByte);
                }
                i += oldBytes.length;


                replace += 1;
                if (replaceCount >= 1 && replace >= replaceCount) { // replace done
                    for (int k = i; k < source.length; k+=1) {
                        bos.write(source[k]);
                    }
                    break;
                }

            } else {
                bos.write(source[i]);
                i += 1 ;
            }
        }

        return bos.toByteArray();
    }


    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static String base64Encode(byte[] bytes){
        byte[] d = Base64.getEncoder().encode(bytes);
        return new String(d);
    }

    public static void main(String []args){
        byte[] data = {(byte)0x01, (byte)0x02, (byte)0x01, (byte)0x02, (byte)0x01, (byte)0x02};
        byte[] oldUid = {(byte) 0x01, (byte) 0x02};
        byte[] newUid = {(byte) 0x11, (byte) 0x22, (byte) 0x33};
        data = replaceBytes(data, oldUid, newUid);
        System.out.println(bytesToHex(data));

    }
}
