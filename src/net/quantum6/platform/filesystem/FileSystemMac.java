package net.quantum6.platform.filesystem;

public final class FileSystemMac extends FileSystem
{
    
    final static String NATIVE_LIB_PREFIX = "lib";
    final static String NATIVE_LIB_SUFFIX = ".dylib";

    private final static String[] HOME_RESOURCE_DIRS_DEFAULT =
    {
            "Desktop",
            "Documents",
            "Pictures",
            "Music",
            "Movies"
    };
    
    public final static String[] FONT_DIRS = {"/System/Library/Fonts", "/Library/Fonts"};
    
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
        String workDir = getUserHomeDir(getOsWorkDirName());
        return checkDirectoryExist(workDir);
    }

    @Override
    protected String getOsDirDesktop()
    {
        return getUserHomeDir(HOME_RESOURCE_DIRS_DEFAULT[0]);
    }

    @Override
    protected String getOsDirMyDocuments()
    {
        return getUserHomeDir(HOME_RESOURCE_DIRS_DEFAULT[1]);
    }

    @Override
    protected String getOsDirMyPictures()
    {
        return getUserHomeDir(HOME_RESOURCE_DIRS_DEFAULT[2]);
    }

    @Override
    protected String getOsDirMyMusic()
    {
        return getUserHomeDir(HOME_RESOURCE_DIRS_DEFAULT[3]);
    }

    @Override
    protected String getOsDirMyVideo()
    {
        return getUserHomeDir(HOME_RESOURCE_DIRS_DEFAULT[4]);
    }

    @Override
    protected String[] getOsKeyFileDirs()
    {
        return new String[] {
                FileSystem.getInstallDirConfig()
                };
    }
    
    @Override
    protected String[] getOsFontDirs()
    {
        return FONT_DIRS;
    }
    
}
