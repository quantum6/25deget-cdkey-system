/**
 * 请遵守量子开源协议(Quantum6 Open Source License)。
 * 
 * 作者：柳鲲鹏
 * 
 */

package net.quantum6.cdkey;

import java.math.BigInteger;

final class DecimalKit
{
    final static char[]  DECIMAL_DIGIT_34 = {
            'A', 'B', 'C', '1', 'D', 'E', 'F', '2',
            'G', 'H', 'I', '3', 'J', 'K', 'L', '4',
            'M', 'N', 'P', '5', 'Q', 'R', '6', 'S',
            'T', '7', 'U', 'V', '8', 'W', 'X', '9',
            'Y', 'Z'};
    
    final static char[]  DECIMAL_DIGIT_64 = {
                    'A', 'z', 'B', 'y', '0', 'C', 'x', 'D',
                    'w', '1', 'E', 'v', 'F', 'u', '2', 'G',
                    't', 'H', 's', '3', 'I', 'r', 'J', 'q',
                    '4', 'K', 'p', 'L', 'o', '5', 'M', 'n',
                    'N', 'm', '6', 'O', 'l', 'P', 'k', '7',
                    'Q', 'j', 'R', 'i', '8', 'S', 'h', 'T',
                    'g', '9', 'U', 'f', 'V', 'e', '+', 'W',
                    'd', 'X', 'c', '/', 'Y', 'b', 'Z', 'a'};

    private static final BigInteger JZ_256 = new BigInteger(String.valueOf("256"));
    private static final BigInteger JZ_34  = new BigInteger(String.valueOf(DECIMAL_DIGIT_34.length));
    private final static BigInteger JZ_64  = new BigInteger(String.valueOf(DECIMAL_DIGIT_64.length));

    /** Creates new ZGetString */
    private DecimalKit()
    {
    }


    static String jz256ToJz34(byte[] byte256)
    {
        BigInteger data = new BigInteger("0");
        for (int i=0; i<byte256.length; i++)
        {
            data = data.multiply(JZ_256);
            data = data.add(new BigInteger(String.valueOf(byte256[i] & 0xFF)));
        }

        byte[] byte34 = new byte[25];
        for (int i=0; i<byte34.length; i++)
        {
            BigInteger data2 = data.divide(JZ_34);
            data2 = data2.multiply(JZ_34);
            data = data.subtract(data2);
            byte34[i] = data.byteValue();
            
            data = data2.divide(JZ_34);
        }
        
        for (int i=0; i<byte34.length; i++)
        {
            byte34[i] = (byte)DECIMAL_DIGIT_34[(byte34[i] & 0xFF)];
        }
        
        return new String(byte34);
    }

    static byte[] jz34ToJz256(byte[] byte34, final boolean secondTime)
    {
        for (int i=0; i<byte34.length; i++)
        {
            for (int j=0; j<DECIMAL_DIGIT_34.length; j++)
            {
                if (byte34[i] == DECIMAL_DIGIT_34[j])
                {
                    byte34[i] = (byte)j;
                }
            }
        }
        
        BigInteger data = new BigInteger(secondTime ? "1" : "0");
        for (int i=byte34.length-1; i>=0; i--)
        {
            data = data.multiply(JZ_34);
            data = data.add(new BigInteger(String.valueOf(byte34[i] & 0xFF)));
        }
        
        byte[] byte256 = new byte[16];
        for (int i=byte256.length-1; i>=0; i--)
        {
            BigInteger data2 = data.divide(JZ_256);
            data2 = data2.multiply(JZ_256);
            data = data.subtract(data2);
            byte256[i] = data.byteValue();
            
            data = data2.divide(JZ_256);
        }

        return byte256;
    }
    
    /**
     * 十进制表达的字符串变64进制表达的字符串
     */
    static String z10To64(String s10)
    {
        BigInteger bi = null;
        try
        {
            bi = new BigInteger(s10);
        }
        catch(Exception e)
        {
            net.quantum6.platform.TsLog.writeLog(e);
            return "";
        }
        StringBuffer s64 = new StringBuffer();
        while (bi.compareTo(BigInteger.ZERO) > 0)
        {
            s64.append(DECIMAL_DIGIT_64[bi.mod(JZ_64).intValue()]);
            bi = bi.divide(JZ_64);
        }
        return s64.reverse().toString();
    }
    
    static boolean isValidJz64Char(final char ch)
    {
        for (int j=0; j<DECIMAL_DIGIT_64.length; j++)
        {
            if (ch == DECIMAL_DIGIT_64[j])
            {
                return true;
            }
        }
        return false;
    }
    

    static String jz64ToJz10(String jz64)
    {
        int sLen = jz64.length() - 1;
        int aLen = DECIMAL_DIGIT_64.length;
        BigInteger bi0  = BigInteger.ZERO;
        BigInteger bi1 = BigInteger.ONE;
        char tempChar;
        for (int i = sLen; i >= 0; i--)
        {
            tempChar = jz64.charAt(i);
            for (int j = 1; j < aLen; j++)
            {
                if (tempChar == DECIMAL_DIGIT_64[j])
                {
                    bi0 = bi0.add(bi1.multiply(BigInteger.valueOf(j)));
                    break;
                }
            }
            bi1 = bi1.multiply(JZ_64);
        }
        return bi0.toString();
    }

    /**
     * 为了让CDKEY更混乱，加密后的数据交换一下顺序。
     */
    static void changeOrder(byte[] data)
    {
        int len = data.length;
        for (int i=0; i<len/4; i++)
        {
            int pos1 = 2*i;
            int pos2 = pos1+len/2;
            
            byte temp  = data[pos1];
            data[pos1] = (byte)(~data[pos2]);
            data[pos2] = (byte)(~temp);
        }
    }

}
