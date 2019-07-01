package com.hb.coop.utils.otp;

import org.apache.commons.codec.binary.Base32;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.lang.reflect.UndeclaredThrowableException;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;

public class TOTP {

    private TOTP() {
    }
    /**
     * This method uses the JCE to provide the crypto
     * algorithm.
     * HMAC computes a Hashed Message Authentication Code with the
     * crypto hash algorithm as a parameter.
     *
     * @param crypto   the crypto algorithm (HmacSHA1, HmacSHA256,
     *                 HmacSHA512)
     * @param keyBytes the bytes to use for the HMAC key
     * @param text     the message or text to be authenticated.
     */
    private static byte[] hmac_sha1(String crypto, byte[] keyBytes,
                                    byte[] text) {
        try {
            Mac hmac;
            hmac = Mac.getInstance(crypto);
            SecretKeySpec macKey =
                    new SecretKeySpec(keyBytes, "RAW");
            hmac.init(macKey);
            return hmac.doFinal(text);
        } catch (GeneralSecurityException gse) {
            throw new UndeclaredThrowableException(gse);
        }
    }


    /**
     * This method converts HEX string to Byte[]
     *
     * @param valueInt the HEX string
     * @return A byte array
     */
    private static byte[] int2Bytes(String valueInt) {

        Integer i = Integer.parseInt(valueInt);
        ByteBuffer buffer = ByteBuffer.allocate(8);
        while (i != 0) {
            buffer.put((byte) (i & 0xff));
            i >>= 8;
        }
        byte[] data = buffer.array();
        reverse(data);
        return data;
    }

    private static void reverse(byte[] data) {
        int left = 0;
        int right = data.length - 1;

        while( left < right ) {
            // swap the values at the left and right indices
            byte temp = data[left];
            data[left] = data[right];
            data[right] = temp;

            // move the left and right index pointers in toward the center
            left++;
            right--;
        }
    }


    private static final int[] DIGITS_POWER = {
            1, // ^0
            10, // ^1
            100, // ^2
            1_000, // ^3
            10_000, // ^4
            100_000, //^5
            1_000_000, //^6
            10_000_000,
            100_000_000
    };


    /**
     * This method generates an TOTP value for the given
     * set of parameters.
     *
     * @param key          the shared secret, HEX encoded
     * @param time         a value that reflects a time
     * @param returnDigits number of digits to return
     * @return A numeric String in base 10 that includes
     */
    public static String generateTOTP(String key,
                                      String time,
                                      String returnDigits) {
        return generateTOTP(key, time, returnDigits, "HmacSHA1");
    }


    /**
     * This method generates an TOTP value for the given
     * set of parameters.
     *
     * @param key          the shared secret, HEX encoded
     * @param time         a value that reflects a time
     * @param returnDigits number of digits to return
     * @return A numeric String in base 10 that includes
     */
    public static String generateTOTP256(String key,
                                         String time,
                                         String returnDigits) {
        return generateTOTP(key, time, returnDigits, "HmacSHA256");
    }


    /**
     * This method generates an TOTP value for the given
     * set of parameters.
     *
     * @param key          the shared secret, HEX encoded
     * @param time         a value that reflects a time
     * @param returnDigits number of digits to return
     * @return A numeric String in base 10 that includes
     */
    public static String generateTOTP512(String key,
                                         String time,
                                         String returnDigits) {
        return generateTOTP(key, time, returnDigits, "HmacSHA512");
    }



    /**
     * This method generates an TOTP value for the given
     * set of parameters.
     *
     * @param key          the shared secret, HEX encoded
     * @param time         a value that reflects a time
     * @param returnDigits number of digits to return
     * @param crypto       the crypto function to use
     * @return A numeric String in base 10 that includes
     */
    private static String generateTOTP(String key,
                                       String time,
                                       String returnDigits,
                                       String crypto) {

        int codeDigits = Integer.decode(returnDigits);
        StringBuilder result;
        byte[] hash;

        // Using the counter
        // First 8 bytes are for the movingFactor
        // Complaint with base RFC 4226 (HOTP)
        StringBuilder timeBuilder = new StringBuilder(time);
        while (timeBuilder.length() < 16)
            timeBuilder.insert(0, "0");
        time = timeBuilder.toString();

        byte[] msg = int2Bytes(time);
//        Timber.d("Input: %s", toHexadecimal(msg, msg.length));

        // Adding one byte to get the right conversion
        byte[] k = decodeKey(key);
//        Timber.d("Key: %s", toHexadecimal(k, k.length));
        hash = hmac_sha1(crypto, k, msg);

        // put selected bytes into result int
        int offset = hash[hash.length - 1] & 0xf;

        int binary =
                ((hash[offset] & 0x7f) << 24) |
                        ((hash[offset + 1] & 0xff) << 16) |
                        ((hash[offset + 2] & 0xff) << 8) |
                        (hash[offset + 3] & 0xff);

        int otp = binary % DIGITS_POWER[codeDigits];

        result = new StringBuilder(Integer.toString(otp));
        while (result.length() < codeDigits) {
            result.insert(0, "0");
        }
        return result.toString();
    }


    private static byte[] decodeKey(String key) {
        Base32 base32 = new Base32();
        try {
            return base32.decode(key.toUpperCase().getBytes("utf-8"));
        } catch (Exception e) {
            return base32.decode(key.toUpperCase().getBytes());
        }
    }

    private static String toHexadecimal(byte[] digest, int length) {
        StringBuilder hash = new StringBuilder();

        for (int i = 0; i < length; i++) {
            byte aux = digest[i];
            int b = aux & 0xff;
            if (Integer.toHexString(b).length() == 1) hash.append("0");
            hash.append(Integer.toHexString(b));
        }
        return hash.toString();
    }

}
