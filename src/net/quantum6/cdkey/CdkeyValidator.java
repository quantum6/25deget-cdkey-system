/**
 * 请遵守量子开源协议(Quantum6 Open Source License)。
 * 
 * 作者：柳鲲鹏
 * 
 */

package net.quantum6.cdkey;

import java.security.Key;

import javax.crypto.Cipher;

public final class CdkeyValidator
{

    private static String decryptTwoTimes(final String cdkey, final boolean secondTime)
    {
        try
        {
            String cdkey34 = cdkey.replace("-", "");

            byte[] jz256 = DecimalKit.jz34ToJz256(cdkey34.getBytes(), secondTime);
            DecimalKit.swapHalf(jz256);

            //使用DES加密一遍，看看输出。
            Cipher cipher = CipherCdkey.getDecryptCipher();
            Key key = CipherCdkey.getKey();
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decryptedBytes = cipher.doFinal(jz256);
            if (decryptedBytes != null && decryptedBytes.length > 0)
            {
                return new String(decryptedBytes);
            }
        }
        catch (Exception e)
        {
            //TsLog.writeLog(e);
        }
        return null;
    }
    
    private static boolean isValidKeyInfo(final String result)
    {
        if (result == null)
        {
            return false;
        }
        
        char[] data = result.toCharArray();
        int count = data.length;
        for (int i=0; i<count; i++)
        {
            if (!DecimalKit.isValidJz64Char(data[i]))
            {
                return false;
            }
        }
        
        //注意检查范围。serialNo
        return     data[5] == data[count-1]
                && data[4] == data[count-2]
                && data[3] == data[count-3];
    }
    
    private static String decrypt(final String cdkey)
    {
        String result = decryptTwoTimes(cdkey, false);
        if (!isValidKeyInfo(result))
        {
            result = decryptTwoTimes(cdkey, true);
        }
        return result;
    }
    
    public static int validate(final String cdkey,
        int product, int version, int language)
    {
        String decryptedText = decrypt(cdkey);
        if (decryptedText == null)
        {
            return -1;
        }
        
        String product2  = DecimalKit.jz64ToJz10(decryptedText.substring( 7,  8));
        String version2  = DecimalKit.jz64ToJz10(decryptedText.substring( 8, 10));
        String language2 = DecimalKit.jz64ToJz10(decryptedText.substring(10, 12));

        if (   Integer.valueOf(product2)  != product
            || Integer.valueOf(version2)  != version
            || Integer.valueOf(language2) != language)
        {
            return -1;
        }
            
        String serialNo2 = DecimalKit.jz64ToJz10(decryptedText.substring( 0,  6));
        return Integer.valueOf(serialNo2);
    }
    
    /*
    public static void main(String[] args)
    {
        int product   = tso.platform.ProductInfoTaishan.getProductID();
        int version   = tso.platform.ProductInfoTaishan.getVersionID();
        int lanaguage = tso.platform.ProductInfoTaishan.getLanguageID();

        String cdkey = "ADKIA-JQAAC-2LDZV-3VRCS-IBD38";
        int serial = validate(cdkey, product, version, lanaguage);
        System.out.println("serial="+serial+", "+cdkey);
    }
    */
}
