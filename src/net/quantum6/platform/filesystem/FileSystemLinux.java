/**
 * 请遵守量子开源协议(Quantum6 Open Source License)。
 * 
 * 作者：柳鲲鹏
 * 
 */

package net.quantum6.platform.filesystem;

import java.io.File;

import net.quantum6.platform.OsKit;

public class FileSystemLinux extends FileSystem
{
    final static String NATIVE_LIB_PREFIX = "lib";
    final static String NATIVE_LIB_SUFFIX = ".so";

    private final static String LINUX_WORK_DIR_NAME   = "/home";

    /** 默认，比如Ubuntu就是这个。 */
    private final static String[] HOME_RESOURCE_DIRS_DEFAULT =
    {
            "桌面",
            "文档",
            "图片",
            "音乐",
            "视频"
    };

    private final static String[] HOME_RESOURCE_DIRS_2 =
    {
            "Desktop",
            "Documents",
            "Pictures",
            "Music",
            "Videos"
    };
    

    /**
     * 如果有多个宋体，以此为优先。
     * 有两个文件都是宋体，初步看也一样。
     * simsun.ttc
     * simsun_0.ttc
     * 
     */
    private final static String FONT_DIR = "/usr/share/fonts";
    
    private final static String[] FONT_DIRS = {FONT_DIR};
    
    private String[] homeDirs = HOME_RESOURCE_DIRS_DEFAULT;
    
    FileSystemLinux()
    {
        if (OsKit.isOsLinuxUos())
        {
            homeDirs = HOME_RESOURCE_DIRS_2;
        }
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
    protected String getOsWorkDirName()
    {
        return "."+super.getOsWorkDirName();
    }

    @Override
    protected String getOsWorkDir(String windowsDirName)
    {
        String workDir = System.getProperty("user.home");
        if (workDir.startsWith("/root"))
        {
            String sudoUser = System.getenv("SUDO_USER");
            if (sudoUser != null && !sudoUser.isEmpty())
            {
                workDir = LINUX_WORK_DIR_NAME + File.separator + sudoUser;
            }
        }
        
        workDir += File.separator;
        workDir += getOsWorkDirName();
        return checkDirectoryExist(workDir);
    }

    @Override
    protected String getOsDirDesktop()
    {
        return getUserHomeDir(homeDirs[0]);
    }

    @Override
    protected String getOsDirMyDocuments()
    {
        return getUserHomeDir(homeDirs[1]);
    }

    @Override
    protected String getOsDirMyPictures()
    {
        return getUserHomeDir(homeDirs[2]);
    }

    @Override
    protected String getOsDirMyMusic()
    {
        return getUserHomeDir(homeDirs[3]);
    }

    @Override
    protected String getOsDirMyVideo()
    {
        return getUserHomeDir(homeDirs[4]);
    }
    
    @Override
    protected String[] getOsKeyFileDirs()
    {
        //LINUX有权限问题。
        return new String[] {
                FileSystem.getInstallDirConfig(),
                FileSystem.getUserConfigDir()
                };
    }
    
    @Override
    protected String[] getOsFontDirs()
    {
        return FONT_DIRS;
    }
    
}
