/**
 * 请遵守量子开源协议(Quantum6 Open Source License)。
 * 
 * 作者：柳鲲鹏
 * 
 */
package net.quantum6.cdkey;

import java.io.IOException;
import java.security.Key;

import javax.crypto.Cipher;

import net.quantum6.platform.ProductInfo;
import net.quantum6.platform.TsLog;

public final class CdkeyGenerator
{
    private static String jz10ToJz64(String jz10, int len)
    {
        jz10 = DecimalKit.z10To64(jz10);
        while (jz10.length() < len)
        {
            jz10 = DecimalKit.DECIMAL_DIGIT_64[0] + jz10;
        }
        
        //从后向前取。
        int result = jz10.length();
        if (result > len)
        {
            jz10 = jz10.substring(result-len, result);
        }
        return jz10;
    }
    
    public static String generate(int serialNo,
        int product, int version,
        int language) throws IOException
    {
        return generateInteral(serialNo + CdkeyConfig.CDKEY_START,
            product, version, language);
    }

    private static String generateInteral(int serialNo,
        int product, int version,
        int language) throws IOException
    {
        String productStr = jz10ToJz64(String.valueOf(product),       1);
        product = Integer.valueOf(DecimalKit.jz64ToJz10(productStr));

        String versionStr = jz10ToJz64(String.valueOf(version),       2);
        version = Integer.valueOf(DecimalKit.jz64ToJz10(versionStr));

        String languageStr = jz10ToJz64(String.valueOf(language),     2);
        language = Integer.valueOf(DecimalKit.jz64ToJz10(languageStr));

        String key64_15 =
              jz10ToJz64(String.valueOf(serialNo),      6)
            + jz10ToJz64(String.valueOf(CdkeyConfig.CDKEY_VERSION), 1)
            + productStr
            
            + versionStr
            + languageStr
            
            //使用serialNo是为了让CDEKY看起来更乱。
            + jz10ToJz64(String.valueOf(serialNo),      3);

        byte[] encryptedBytes;
        try
        {
            Cipher cipher = CipherCdkey.getEncryptCipher();
            Key key = CipherCdkey.getKey();
            cipher.init(Cipher.ENCRYPT_MODE, key);
            encryptedBytes = cipher.doFinal(key64_15.getBytes());
        }
        catch (Exception e)
        {
            TsLog.writeLog(e);
            return null;
        }

        DecimalKit.changeOrder(encryptedBytes);
        String encrypted32   = DecimalKit.jz256ToJz34(encryptedBytes);
        
        int i = 0;
        int j = 0;
        int length = encrypted32.length();
        String displayText = "";
        while ((j = j + CdkeyConfig.PART_SIZE) < length)
        {
            displayText += encrypted32.substring(i, j) + "-";
            i = j;
        }
        displayText += encrypted32.substring(i);
        //System.out.println("displayText="+displayText);

        //进行一次校验
        int serial2 = CdkeyValidator.validate(displayText, product, version, language);
        if (serialNo == serial2)
        {
            return displayText;
        }
        
        throw new IOException("Generator "+serialNo+" ERROR !!!");
    }
    
    public static void main(String[] args)
    {
        try
        {
            if (!CdkeyInitiator.initCdkeyKeyFile())
            {
                return;
            }

            int product   = ProductInfo.getProductID();
            int version   = ProductInfo.getVersionID();
            int lanaguage = ProductInfo.getLanguageID();
            System.out.println("product="+product+", version="+version+", lanaguage="+lanaguage);
            for (int i=1; i<2; i++)
            {
                String cdkey = generate(i, product, version, lanaguage);
                if (cdkey == null)
                {
                    break;
                }
                System.out.println(i+", "+cdkey);
                //System.out.println(cdkey);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
