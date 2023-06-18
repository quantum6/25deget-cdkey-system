package net.quantum6.cdkey;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;

import net.quantum6.platform.CodeKit;
import net.quantum6.platform.TsLog;
import net.quantum6.platform.filesystem.FileSystem;

final class CipherFileKey
{
    private final static int DATA_BLOCK_SIZE = 32;
    private final static int FILE_BLOCK_SIZE = CdkeyConfig.PKI_DIGIT/8;

    private static String publicKeyStr;
    private static String privatKeyStr;
    
    private static void writeKey(final String key, final String file)
    {
        try
        {
            FileOutputStream fos = CodeKit.newFileOutputStream(file);
            fos.write(key.getBytes(CdkeyConfig.CHARSET));
            fos.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    private static String readKey(final String file)
    {
        try
        {
            byte[] data = new byte[CdkeyConfig.PKI_DIGIT];
            int len=0;
            int count=0;
            FileInputStream fis = CodeKit.newFileInputStream(file);
            while (true)
            {
                len = fis.read(data, 0, CdkeyConfig.PKI_DIGIT-count);
                if (len <= 0)
                {
                    break;
                }
                count += len;
            }
            fis.close();
            return new String(data, 0, count, CdkeyConfig.CHARSET);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 随机生成密钥对
     */
    static void generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(CdkeyConfig.PKI_ALGORITHM);
        keyPairGen.initialize(CdkeyConfig.PKI_DIGIT, new SecureRandom());
        KeyPair keyPair = keyPairGen.generateKeyPair();

        RSAPublicKey  publicKey = (RSAPublicKey)  keyPair.getPublic();
        publicKeyStr = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        writeKey(publicKeyStr, FileSystem.getCdkeyPublicFile());
        
        RSAPrivateKey privatKey = (RSAPrivateKey) keyPair.getPrivate();
        privatKeyStr = Base64.getEncoder().encodeToString(privatKey.getEncoded());
        writeKey(privatKeyStr, FileSystem.getCdkeyPrivateFile());
    }

    static void writeObjectToFile(Object key, String file)
    {
        try
        {
            String keyStr = readKey(FileSystem.getCdkeyPrivateFile());
            byte[] decoded = Base64.getDecoder().decode(keyStr);
            RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance(CdkeyConfig.PKI_ALGORITHM).generatePrivate(new PKCS8EncodedKeySpec(decoded));

            Cipher cipher = Cipher.getInstance(CdkeyConfig.PKI_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, priKey);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(key);
            byte[] keyData = bos.toByteArray();
            bos.close();
            oos.close();
            
            byte[] blockData = new byte[DATA_BLOCK_SIZE];
            int start = 0;
            FileOutputStream fos = CodeKit.newFileOutputStream(file);
            while (true)
            {
                int len = keyData.length-start;
                if (len > DATA_BLOCK_SIZE)
                {
                    len = DATA_BLOCK_SIZE;
                }
                System.arraycopy(keyData, start, blockData, 0, len);
                byte[] encrypted = cipher.doFinal(blockData);
                fos.write(encrypted);
                
                start += len;
                if (start >= keyData.length)
                {
                    break;
                }
            }
            fos.close();
        }
        catch (Exception e)
        {
            TsLog.writeLog(e);
        }
    }

    static Object readObjectFromFile(String file)
    {
        try
        {
            String keyStr = readKey(FileSystem.getCdkeyPublicFile());
            byte[] decoded = Base64.getDecoder().decode(keyStr);
            Cipher cipher = Cipher.getInstance(CdkeyConfig.PKI_ALGORITHM);
            RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance(CdkeyConfig.PKI_ALGORITHM).generatePublic(new X509EncodedKeySpec(decoded));
            cipher.init(Cipher.DECRYPT_MODE, pubKey);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] blockData = new byte[FILE_BLOCK_SIZE];
            FileInputStream fis = CodeKit.newFileInputStream(file);
            while (true)
            {
                int count = fis.read(blockData);
                if (count <= 0)
                {
                    break;
                }
                byte[] decrypted = cipher.doFinal(blockData);
                bos.write(decrypted);
            }
            byte[] keyData = bos.toByteArray();
            bos.close();
            fis.close();
            
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(keyData));
            Object key = ois.readObject();
            ois.close();
            return key;
        }
        catch(Exception e)
        {
            TsLog.writeLog(e);
        }
        return null;
    }

}