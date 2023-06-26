/**
 * 请遵守量子开源协议(Quantum6 Open Source License)。
 * 
 * 作者：柳鲲鹏
 * 
 */

package net.quantum6.cdkey;

import java.math.BigInteger;

import net.quantum6.platform.TsLog;

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

    private DecimalKit()
    {
    }

    private static BigInteger divideInteger(byte[] dst, int i, BigInteger integerSrc, BigInteger jz)
    {
        //256的整数倍
        BigInteger integer256 = integerSrc.divide(jz);
        
        // 如果是0，说明剩下最后一位了。
        if (integer256.compareTo(BigInteger.ZERO) == 0)
        {
            dst[i] = integerSrc.byteValue();
            return null;
        }
        
        // 余数
        dst[i] = integerSrc.subtract(integer256.multiply(jz)).byteValue();
        
        // 为下次循环使用。
        integerSrc = integer256;

        return integerSrc;
    }
    
    private static BigInteger multiIntgeger(BigInteger integerDst, byte v, BigInteger jz)
    {
        integerDst = integerDst.multiply(jz);
        integerDst = integerDst.add(new BigInteger(String.valueOf(v & 0xFF)));
        return integerDst;
    }
    
    private static void byteToJz(byte[] dst, char[] jz)
    {
        for (int i=0; i<dst.length; i++)
        {
            for (int j=0; j<jz.length; j++)
            {
                if (dst[i] == jz[j])
                {
                    dst[i] = (byte)j;
                }
            }
        }
    }
    
    /**
     * 高字节是高位。
     */
    static String jz256ToJz34(byte[] byte256)
    {
        BigInteger keyInteger = new BigInteger("0");
        for (int i=byte256.length-1; i>=0; i--)
        {
            keyInteger = multiIntgeger(keyInteger, byte256[i], JZ_256);
        }

        byte[] byte34 = new byte[25];
        for (int i=byte34.length-1; i>=0; i--)
        {
            keyInteger = divideInteger(byte34, i, keyInteger, JZ_34);
            if (keyInteger == null)
            {
                break;
            }
        }
        
        for (int i=0; i<byte34.length; i++)
        {
            byte34[i] = (byte)DECIMAL_DIGIT_34[(byte34[i] & 0xFF)];
        }
        
        return new String(byte34);
    }

    /**
     * 与jz256ToJz34对应：
     * 循环方式的差异。
     * 此处的乘除，对应前面的除乘
     */
    static byte[] jz34ToJz256(byte[] byte34, final boolean secondTime)
    {
        byteToJz(byte34, DECIMAL_DIGIT_34);
        
        BigInteger keyInteger = new BigInteger(secondTime ? "1" : "0");
        for (int i=0; i<byte34.length; i++)
        {
            keyInteger = multiIntgeger(keyInteger, byte34[i], JZ_34);
        }

        byte[] byte256 = new byte[16];
        for (int i=0; i<byte256.length; i++)
        {
            keyInteger = divideInteger(byte256, i, keyInteger, JZ_256);
            if (keyInteger == null)
            {
                break;
            }
        }

        return byte256;
    }
    
    /**
     * 十进制表达的字符串变64进制表达的字符串
     */
    static String jz10ToJz64(String s10)
    {
        BigInteger bi = null;
        try
        {
            bi = new BigInteger(s10);
        }
        catch(Exception e)
        {
            TsLog.writeLog(e);
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
        int aLen = DECIMAL_DIGIT_64.length;
        BigInteger bi0  = BigInteger.ZERO;
        BigInteger bi1 = BigInteger.ONE;
        for (int i = jz64.length()-1; i >= 0; i--)
        {
            char tempChar = jz64.charAt(i);
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
     * 为了让CDKEY更混乱，前半部分和后半部分交换一下
     */
    static void swapHalf(byte[] data)
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
