package net.quantum6.cdkey;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import net.quantum6.platform.filesystem.FileSystem;


final class CipherCdkey {
    
    /**
     * 加密/解密算法-工作模式-填充模式
     */
    private static final String CIPHER_ALGORITHM = CdkeyConfig.PKC_ALGORITHM + "/ECB/PKCS5Padding";
    
    private static Key keyFromFile;
    
    static Cipher getEncryptCipher()
    {
        try
        {
            return Cipher.getInstance(CIPHER_ALGORITHM);
        }
        catch (Exception e)
        {
            return null;
        }
    }

    static Cipher getDecryptCipher()
    {
        try
        {
            return Cipher.getInstance(CIPHER_ALGORITHM);
        }
        catch (Exception e)
        {
            return null;
        }
    }
    
    static Key generateKey(String password) throws InvalidKeyException, UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeySpecException
    {
        return new SecretKeySpec(password.getBytes(CdkeyConfig.CHARSET), CdkeyConfig.PKC_ALGORITHM);
    }
    
    static Key getKey()
    {
        if (keyFromFile == null)
        {
            keyFromFile = (Key)CipherFileKey.readObjectFromFile(FileSystem.getCdkeyKeyFile());
        }
        return keyFromFile;
    }
    

}
