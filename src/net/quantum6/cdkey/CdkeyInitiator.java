package net.quantum6.cdkey;

import java.security.Key;

import net.quantum6.platform.filesystem.FileSystem;

final class CdkeyInitiator {

    private static void initCdkeyKeyFile()
    {
        try
        {
            Key key = CipherCdkey.generateKey(CdkeyConfig.CDKEY_PASSWORD);
            CipherFileKey.writeObjectToFile(key, FileSystem.getCdkeyKeyFile());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void main(String[] args)
    {
        try
        {
            CipherFileKey.generateKeyPair();
            initCdkeyKeyFile();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
