/**
 * 请遵守量子开源协议(Quantum6 Open Source License)。
 * 
 * 作者：柳鲲鹏
 * 
 */

package net.quantum6.platform.filesystem;

import java.io.File;
import java.io.InputStream;

public final class FileSystemWindows extends FileSystem
{
    final static String NATIVE_LIB_PREFIX = "";
    final static String NATIVE_LIB_SUFFIX = ".dll";

    public  final static String FILE_TIOMISC_EXE       = "TIOMisc.exe";

    final static String WINDOWS_PRODUCT_DIR_NAME       = "ALLUSERSPROFILE";
    final static String WINDOWS_USER_DIR_NAME          = "APPDATA";
    final static String WINDOWS_LOCALAPPDATA_DIR_NAME  = "LOCALAPPDATA";

    
    private final static String[] FONT_DIRS = {"C:\\Windows\\Fonts"};
    
    private final static String REG_ITEM_FORDERS_DESKTOP      = "\"" + "Desktop"     +"\"";
    private final static String REG_ITEM_FORDERS_MY_DOCUMENTS = "personal";
    private final static String REG_ITEM_FORDERS_MY_PICTURES  = "\"" + "My Pictures" +"\"";
    private final static String REG_ITEM_FORDERS_MY_MUSIC     = "\"" + "My Music"    +"\"";
    private final static String REG_ITEM_FORDERS_MY_VIDEO     = "\"" + "My Video"    +"\"";

    private final static String REG_KEY_FORDERS = "\"HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Shell Folders\"";

    private static String readRegKeyWindows(final String item)
    {
        try {
            Process p =  Runtime.getRuntime().exec("reg query "+REG_KEY_FORDERS+" /v "+item);
            p.waitFor();

            InputStream in = p.getInputStream();
            byte[] b = new byte[in.available()];
            in.read(b);
            in.close();

            String result = new String(b);
            return result.split("\\s\\s+")[4];
        }
        catch (Exception e)
        {
            //e.printStackTrace();
        }

        return null;
    }
    
    @Override
    protected String getNativeLibPrefix()
    {
        return NATIVE_LIB_PREFIX;
    }

    @Override
    protected String getNativeLibSuffix()
    {
        return NATIVE_LIB_SUFFIX;
    }

    @Override
    protected String getOsWorkDir(String windowsDirName)
    {
        String workDir = System.getenv(windowsDirName);
        
        workDir += File.separator;
        workDir += getOsWorkDirName();
        return checkDirectoryExist(workDir);
    }
    
    @Override
    protected String getOsDirDesktop()
    {
        return readRegKeyWindows(REG_ITEM_FORDERS_DESKTOP);
    }
    
    @Override
    protected String getOsDirMyDocuments()
    {
        return readRegKeyWindows(REG_ITEM_FORDERS_MY_DOCUMENTS);
    }

    @Override
    protected String getOsDirMyPictures()
    {
        return readRegKeyWindows(REG_ITEM_FORDERS_MY_PICTURES);
    }

    @Override
    protected String getOsDirMyMusic()
    {
        return readRegKeyWindows(REG_ITEM_FORDERS_MY_MUSIC);
    }

    @Override
    protected String getOsDirMyVideo()
    {
        return readRegKeyWindows(REG_ITEM_FORDERS_MY_VIDEO);
    }

    @Override
    protected String[] getOsKeyFileDirs()
    {
        return new String[] {FileSystem.getProductWorkDir()};
    }
    
    @Override
    protected String[] getOsFontDirs()
    {
        return FONT_DIRS;
    }
}
