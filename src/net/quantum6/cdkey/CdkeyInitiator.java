/**
 * 请遵守量子开源协议(Quantum6 Open Source License)。
 * 
 * 作者：柳鲲鹏
 * 
 */

package net.quantum6.cdkey;

import java.security.Key;

import net.quantum6.platform.filesystem.FileSystem;

final class CdkeyInitiator {

    static boolean initCdkeyKeyFile()
    {
        try
        {
            CipherFileKey.generateKeyPair();
            Key key = CipherCdkey.generateKey(CdkeyConfig.CDKEY_PASSWORD);
            CipherFileKey.writeObjectToFile(key, FileSystem.getCdkeyKeyFile());
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args)
    {
        initCdkeyKeyFile();
    }

}
