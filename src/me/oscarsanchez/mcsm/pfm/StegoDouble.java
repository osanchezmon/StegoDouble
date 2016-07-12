package me.oscarsanchez.mcsm.pfm;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Arrays;

import static java.lang.System.out;

/**
 * Created by osanchezmon on 11/7/16.
 */
public final class StegoDouble implements Serializable {
    // From Act I of Titus Andronicus.
    public final static String TEST = "After the death of the Emperor of Rome, his two sons, Saturninus and Bassianus, ask the masses to determine who should succeed to the throne";
    public final static Boolean DEBUG = Boolean.FALSE;

    private Double originalNumber;
    private Double stegoNumber;

    private BigDecimal originalNumberAsBigDecimal;
    private BigDecimal stegoNumberAsBigDecimal;

    private String originalPayload;
    private String payloadAsBinaryString;

    private int intFromBinaryString;

    /**
     *
     */
    public StegoDouble() {
        originalNumber = new Double(0.00);
        originalNumberAsBigDecimal = new BigDecimal(0.0);
    }

    /**
     *
     * @param number
     */
    public StegoDouble(double number) {
        originalNumber = new Double(number);
        originalNumberAsBigDecimal = new BigDecimal(String.valueOf(number));

        if (DEBUG == Boolean.TRUE)
            out.println("Debug >> " + number + " - " + originalNumber + " - " + originalNumberAsBigDecimal);
    }

    /**
     *
     * @param payload
     * @return
     */
    public String setPayload(String payload) {
        originalPayload = payload;

        //
        payloadAsBinaryString = encodeUTF8AsBinaryString(payload);

        // Transform the binary string to an unsigned integer
        intFromBinaryString = Integer.parseUnsignedInt(payloadAsBinaryString, 2);

        //
        stegoNumber = new Double(Double.parseDouble(String.valueOf(originalNumber.doubleValue()) + String.valueOf(intFromBinaryString)));
        stegoNumberAsBigDecimal = new BigDecimal(Double.parseDouble(String.valueOf(originalNumber.doubleValue()) + String.valueOf(intFromBinaryString)));

        if (DEBUG == Boolean.TRUE) {
            out.println("Debug >> Payload (" + originalPayload + ") - Payload as binary string (" + payloadAsBinaryString + ") - INT value of payload's binary string (" + intFromBinaryString + ")");
            out.println("Debug >> Stego number (" + stegoNumber + ") - Stego number as Big Decimal (" + stegoNumberAsBigDecimal + ")");
        }

        return payloadAsBinaryString;
    }

    /**
     *
     * @return
     */
    public Double getStegoNumber() {

        return stegoNumber;
    }

    /**
     *
     * @param stegoNumber
     * @return
     */
    public static String getPayload(double stegoNumber) {
        // Split the integer
        int intFromDouble = Integer.parseUnsignedInt(String.valueOf(stegoNumber).substring(String.valueOf(stegoNumber).lastIndexOf(".") + 3));
        String utf8String = decodeBinaryStringToUTF8String(Integer.toBinaryString(intFromDouble));

        if (DEBUG == Boolean.TRUE) {
            out.println("Debug >> Stego number (" + stegoNumber + ") - INT raw payload (" + intFromDouble + ") - UTF-8 calculate payload (" + utf8String + ")");
        }

        return utf8String;
    }

    /**
     *
     * @return
     */
    public int lengthOfDecimalPart() {
        int result = originalNumberAsBigDecimal.scale();

        if (DEBUG == Boolean.TRUE)
            out.println("Debug >> " + result);

        return result;
    }

    /**
     *
     * @param string
     * @return
     */
    private String encodeUTF8AsBinaryString(String string) {
        StringBuilder encodedString = new StringBuilder();

        try {
            // Double.doubleToLongBits() doesn't print the leading 0s
            for (byte b : string.getBytes("UTF-8"))
                for (int i = 7; i >= 0; i--)
                    encodedString.append((b >> i) & 1);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if (DEBUG == Boolean.TRUE)
            out.println("Debug >> " + encodedString.toString() + " | Len -> " + encodedString.toString().length());

        return encodedString.toString();
    }

    /**
     *
     * @param binaryString
     * @return
     */
    private static String decodeBinaryStringToUTF8String(String binaryString) {
        StringBuilder decodedString = new StringBuilder();

        if (binaryString.length() % 8 != 0) {
            char[] fill = new char[8 - (binaryString.length() % 8)];
            Arrays.fill(fill, '0');
            binaryString = new String(fill).concat(binaryString);
            /*StringBuilder fillZeros = new StringBuilder();
            for (int i = 0; i < 8 - (binaryString.length() % 8); i++) {
                fillZeros.append("0");
            }
            fillZeros.append(binaryString);
            binaryString = fillZeros.toString();*/
        }

        if (DEBUG == Boolean.TRUE)
            out.println("Debug >> " + binaryString + " | Len -> " + binaryString.length());

        for (int i = 0; i < binaryString.length(); i += 8) {
            decodedString.append((char) Integer.parseUnsignedInt(binaryString.substring(i, i + 8), 2));
        }

        if (DEBUG == Boolean.TRUE)
            out.println("Debug >> " + decodedString.toString());

        return decodedString.toString();
    }

    private static String toBinary(byte b) {
        // Coerces the byte b to int, then retains only lower 8 bits, and finally sets the 9th bit.
        return Integer.toBinaryString((b & 0xFF) | 0x100).substring(1);
    }

    /**
     *
     * @return
     */
    public int test() {
        String payload;
        StegoDouble number;

        int i = 0;
        do {
            try {
                number = new StegoDouble(originalNumber);
                number.setPayload(TEST.substring(0, ++i));
                payload = StegoDouble.getPayload(number.getStegoNumber());
                if (!TEST.substring(0, i).equalsIgnoreCase(payload)) {
                    i = -1;
                    break;
                }
            } catch(NumberFormatException nfe) {
                if (DEBUG == Boolean.TRUE)
                    out.println("Debug >> " + nfe.getMessage());
                i--;
                break;
            }
        } while(i < TEST.length());

        if (DEBUG == Boolean.TRUE)
            out.println("Debug >> Chars accepted --> " + i + ". If -1 payload can not be encoded as INT");

        return i;
    }

}
