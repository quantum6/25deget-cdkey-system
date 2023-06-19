/**
 * 请遵守量子开源协议(Quantum6 Open Source License)。
 * 
 * 作者：柳鲲鹏
 * 
 */

package net.quantum6.platform.filesystem;

import java.io.File;
import java.io.FileReader;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.LinkedList;
import java.util.List;

import net.quantum6.platform.CodeKit;
import net.quantum6.platform.OsKit;

/**
 * 所有获取产品目录、文件，都从这里获取。
 * 
 * File: 文件
 * Dir(Directory)：目录。所有目录的结尾都没有/\
 * Path：路径
 *
 */
public abstract class FileSystem implements PathProcessor
{

    static
    {
        prepareDirs();
    }

    public  final static String DIR_TAISHAN            = "Quantum6";
    public  final static String DEFAULT_PRODUCT_NAME   = "Office";
    
    public  final static String DEFAULT_UPGRADER_SERVER      = "10.11.131.3";
    public  final static int    DEFAULT_UPGRADER_SERVER_PORT = 9300;
    public  final static int    DEFAULT_UPGRADER_SERVER_UDP  = 8362;
    
    private final static String DOT_LOG        = ".log";
    private final static String DOT_EXE        = ".exe";
    
    public  final static String FILE_PRODUCT_VERSION_INFO         = "ProductVersion.info";
    
    public  final static String FILE_NETWORK_INI                  = "network.ini";
    
    public  final static String FILE_UPGRADER           = "Upgrader";
    public  final static String FILE_UNPACKAGER         = "Unpackager";
    

    private final static String DIR_TEMP = "Temp";
    public  final static String DIR_SUB  = "sub";
    
    
    public  final static String DIR_BACKUP             = "Backup";
    public  final static String DIR_UPGRADER           = "Upgrader";

    public  final static String DIR_DUPLICATES         = "Duplicates";

    // {{{{{{{{{{ 安装目录中的目录。
    public  final static String DIR_INSTALL_BIN            = "bin";
    public  final static String DIR_INSTALL_LIB            = "lib";
    public  final static String DIR_INSTALL_JDK            = "jdk";
    public  final static String DIR_INSTALL_CONFIG         = "Config";
    public  final static String DIR_INSTALL_HELP           = "Help";
    private final static String DIR_INSTALL_GRAMMERSPELL   = "GrammarSpell";
    public  final static String DIR_INSTALL_IMAGES         = "Images";
    public  final static String DIR_INSTALL_TEMPLATES      = "Templates";
    public  final static String DIR_INSTALL_PLUGINS        = "Plugins";
    public  final static String DIR_INSTALL_UPGRADER       = DIR_UPGRADER;

    // }}}}}}}}}}
    
    // {{{{{{{{{{ 用户使用产品时的工作目录。

    
    private final static String DIR_WORK_USERCONFIG        = "UserConfig";
    
    // [[[[[[[[[[
    
    public  final static String FILE_CDKEY_SERIAL  = "cdkey_serial.data";
    
    private final static String FILE_CDKEY_KEY     = "cdkey_key.data";
    private final static String FILE_CDKEY_PUBLIC  = "cdkey_public.data";
    private final static String FILE_CDKEY_PRIVATE = "cdkey_private.data";
    
    // ]]]]]]]]]]
    
    // }}}}}}}}}}
    
    private final static String TAISHAN_NATIVE_LIB = "taishan.dynamiclibrary.path";

    
    /** 动态库装载路径 */
    private   static String     nativeLibDir;
    private   static String     installDir;

    private   static String     productName;
    private   static String     productVersion;
    
    private   static String     productWorkDir;
    private   static String     userWorkDir;

    private   static FileSystem mCurrentFS;
    
    private   static boolean    isRunningJarMode;
    private   static boolean    isOpenCVLoaded = false;
    protected static boolean    isJawtLoaded   = false;
    
    /**
     * 如果有同名的字体，优先使用自带的字体文件
     */
    private static final String[] FIRST_FONT_FILES =
    {
            "batang.ttc",
            "BROADW_0.TTF",
            "cambria_0.ttc",
            "FANGSONG_GB2312.TTF",
            "himalaya.ttf",
            "lucidasans.ttf",
            "monbaiti.ttf",
            "msyh.ttc",
            "msyhbd.ttc",
            "msyi.ttf",
            "simfang.ttf",
            "simhei.ttf",
            "simkai.ttf",
            "simsun.ttc",
            "simsunb.ttf",
            "SourceHanSansCN-Medium.otf",
            "SourceHanSansCN-Normal.otf",
            "Sun-ExtA.ttf",
            "Sun-ExtB.ttf",
            "SunmanPUA.ttf",
            "symbol_0.ttf",
            "times.ttf",
            "wingding.ttf",
            "华康饰艺体W5.TTC",
    };

    private String mJawtLib;
    protected FileSystem()
    {
        mJawtLib = getNativeLibPrefix() + "jawt" + getNativeLibSuffix();
    }

    protected abstract String   getNativeLibPrefix();
    protected abstract String   getNativeLibSuffix();

    protected abstract String   getOsDirDesktop();
    protected abstract String   getOsDirMyDocuments();
    protected abstract String   getOsDirMyPictures();
    protected abstract String   getOsDirMyMusic();
    protected abstract String   getOsDirMyVideo();
    

    protected abstract String   getOsWorkDir(String windowsDirName);
    
    protected abstract String[] getOsKeyFileDirs();
    protected abstract String[] getOsFontDirs();
    
    
    protected final String getUserHomeDir(final String sub)
    {
        return System.getProperty("user.home") + File.separator + sub;
    }
    
    public static String getRunningDir()
    {
        return System.getProperty("user.dir");
    }
    
    public static String getJreDir()
    {
        return System.getProperty("java.home");
    }
    
    public static boolean isRunningJarMode()
    {
        getInstallDir();
        return isRunningJarMode;
    }
    
    /**
     * 获得当前运行Jar所在的目录，尾部不包含File.separator
     * class情形下：
     * jar情形下：
     * 
     * 如果包含中文，会包含类似%e4%b8%ad%e6%96%87的东西，需要用UTF-8解码。
     * @return
     */
    private static String getDirOfThisJar() {
        Class<?> clazz = FileSystem.class;
        String className =clazz.getSimpleName()+".class";
        String path = clazz.getResource(className).toString();
        path = path.substring(path.indexOf("file:/")+5);
        
        className = clazz.getName()+".class";
        path = path.substring(0, path.length() - className.length());
        
        //把+替换成别的
        path = path.replace("+", "<*^*>");
        try
        {
            path = URLDecoder.decode(path, "UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            net.quantum6.platform.TsLog.writeLog(e);
            return "";
        }
        path = path.replace("<*^*>", "+");

        //说明在jar中运行
        if (path.endsWith("!/"))
        {
            isRunningJarMode = true;
            path = path.substring(0, path.length()-2);
            path = path.substring(0, path.lastIndexOf('/'));
        }
        
        return CodeKit.newFile(path).getAbsolutePath();
    }
    
    // {{{{{{{{{{ Install Dir

    /**
     * 这里只判断运行的情况。
     * 如果是安装情况，即子目录的子目录，不予处理。
     * @return
     */
    public static String getInstallDir()
    {
        //是否可以使用getRunningDir() ？对插件运行有什么影响？等确认。
        if (installDir == null)
        {
            installDir = getDirOfThisJar();
            if (isRunningJarMode)
            {
                String[] exeDirs = {DIR_INSTALL_BIN, DIR_INSTALL_UPGRADER};
                for (String dir : exeDirs)
                {
                    if (installDir.endsWith(dir))
                    {
                        installDir = installDir.substring(0, installDir.length()-dir.length()-1);
                        break;
                    }
                }
            }
            installDir = getValidDir(installDir);
        }
        return installDir;
    }
    

    /**
     * not care the dir is exist
     */
    public static String getInstallDir(final String sub)
    {
        return getInstallDir() + File.separator + sub;
    }

    public static String getInstallDirBin()
    {
        return getInstallDir(DIR_INSTALL_BIN);
    }

    /**
     * 获得安装的系统目录
     */
    public static String getInstallDirConfig()
    {
        return getInstallDir(DIR_INSTALL_CONFIG);
    }
    
    public static String getInstallDirHelp()
    {
        return getInstallDir(DIR_INSTALL_HELP);
    }
    
    public static String getInstallDirTemplates() {
        return getInstallDir(DIR_INSTALL_TEMPLATES);
    }
    
    public static String getInstallDirImages()
    {
        return getInstallDir(DIR_INSTALL_IMAGES);
    }

    public static String getInstallDirUpgrader()
    {
        return getInstallDir(DIR_INSTALL_UPGRADER);
    }
    
    public static String getInstallDirGrammarSpell()
    {
        return getInstallDir(DIR_INSTALL_GRAMMERSPELL);
    }
    
    /**
     * 获取安装路径下插件的位置
     */
    public static String getInstallDirPlugins()
    {
        return getInstallDir(DIR_INSTALL_PLUGINS);
    }
    
    /**
     * 获得本地方法的加载路径
     */
    public static String getInstallDirNativeLib()
    {
        if (nativeLibDir == null)
        {
            // For WebOffice, Dynamic Link Library is downloaded from http server,
            // So the path isn't fixed.
            nativeLibDir = System.getProperty(TAISHAN_NATIVE_LIB);
            if (nativeLibDir == null)
            {
                nativeLibDir  = getInstallDir(DIR_INSTALL_BIN);
            }
        }
        return nativeLibDir;
    }

    public static String getProductVersionInfoFile()
    {
        return getInstallDirConfig()+File.separator+FILE_PRODUCT_VERSION_INFO;
    }
    

    public static String getCdkeyFile(final String file)
    {
        return getInstallDirConfig() + File.separator + file;
    }

    public static String getCdkeyKeyFile()
    {
        return FileSystem.getCdkeyFile(FILE_CDKEY_KEY);
    }


    public static String getCdkeyPublicFile()
    {
        return FileSystem.getCdkeyFile(FILE_CDKEY_PUBLIC);
    }


    public static String getCdkeyPrivateFile()
    {
        return FileSystem.getCdkeyFile(FILE_CDKEY_PRIVATE);
    }

    public static String getCdkeySerialFile()
    {
        return FileSystem.getCdkeyFile(FILE_CDKEY_SERIAL);
    }
    
    // }}}}}}}}}} Install Dir
    
    public static String getProductName()
    {
        if (productName == null)
        {
            String runDir = FileSystem.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            if (runDir.indexOf(DIR_TAISHAN) >= 0)
            {
                int pos = runDir.indexOf(DIR_TAISHAN);
                runDir = runDir.substring(pos);
                runDir = runDir.substring(runDir.indexOf('/')+1);
                productName = runDir.substring(0, runDir.indexOf('/'));
            }
            else
            {
                //类运行方式
                productName = DEFAULT_PRODUCT_NAME;
            }
            getProductVersion();
        }
        return productName;
    }
    
    public static String getProductVersion()
    {
        if (productVersion == null)
        {
            try
            {
                FileReader reader = new FileReader(getConfigIniFile());
                char[] buffer = new char[4096];
                reader.read(buffer);
                reader.close();
                
                String text = new String(buffer);
                int pos = text.indexOf("version_code");
                text = text.substring(pos);
                productVersion = text.substring(
                    text.indexOf('=')+1,
                    text.indexOf('\r'));
            }
            catch (Exception e)
            {
                //TsLog.writeLog(e);
            }
        }
        return productVersion;
    }
    
    // {{{{{{{{{{ Product work dir

    /**
     * 就是工作目录的名。
     */
    protected String getOsWorkDirName()
    {
        return DIR_TAISHAN + "-" + getProductName();
    }
    
    public static String getProductWorkDir()
    {
        if (productWorkDir == null)
        {
            productWorkDir = mCurrentFS.getOsWorkDir(FileSystemWindows.WINDOWS_PRODUCT_DIR_NAME);
        }
        return productWorkDir;
    }
    
    public static String getProductNetworkIniFile()
    {
        if (OsKit.isOsMac() || OsKit.isOsLinux())
        {
            File iniDir = new File(FileSystem.getProductWorkDir() + File.separator + DIR_UPGRADER);
            if (!iniDir.exists())
            {
                iniDir.mkdirs();
            }
            return FileSystem.getProductWorkDir() + File.separator + DIR_UPGRADER + File.separator + FileSystem.FILE_NETWORK_INI;
        }
        
        return FileSystem.getProductWorkDir()+File.separator+FileSystem.FILE_NETWORK_INI;
    }
    
    public static String getProductUpgradePackageDir()
    {
        return checkDirectoryExist(getProductWorkDir() + File.separator + DIR_UPGRADER);
    }
    
    protected static String checkDirectoryExist(final String dirPath)
    {
        File dir = new File(dirPath);
        if (!dir.exists())
        {
            dir.mkdirs();
        }
        return dirPath;
    }
    
    // }}}}}}}}}} Product work dir
    
    
    
    public static String getProductExe()
    {
        return getProductName() + (OsKit.isOsWindows() ? DOT_EXE : "");
    }
    
    public static String getLocalAppData()
    {
        String localDir = "";
        if (OsKit.isOsWindows())
        {
            localDir = System.getenv(FileSystemWindows.WINDOWS_LOCALAPPDATA_DIR_NAME);
        }
        return localDir;
    }
    
    public static String getUserWorkTempDir()
    {
        //这个目录，在WINDOWS上就是TEMP环境变量（设置框上面那个。
        String temp = System.getProperty("java.io.tmpdir");
        if (!temp.endsWith(File.separator))
        {
            temp += File.separator;
        }
        return checkDirectoryExist(temp + mCurrentFS.getOsWorkDirName());
    }
    
    public static String getUserWorkDir()
    {
        if (userWorkDir == null)
        {
            userWorkDir = mCurrentFS.getOsWorkDir(FileSystemWindows.WINDOWS_USER_DIR_NAME);
            saveAPP_DATA(userWorkDir);
        }
        return userWorkDir;
    }
    
    public static String getUserDataDir()
    {
        return getUserWorkDir();
    }
    
    public static String getUserConfigDir()
    {
        return checkDirectoryExist(getUserWorkDir()+File.separator+DIR_WORK_USERCONFIG);
    }
    
    public static String getUserTempDir()
    {
        return checkDirectoryExist(getUserWorkDir()+File.separator+DIR_TEMP);
    }

    public static String[] getKeyFileDirs()
    {
        return mCurrentFS.getOsKeyFileDirs();
    }
    
    public static String getLogFile()
    {
        String path = getProductWorkDir();
        if (path == null)
        {
            path = ".";
        }
        return path+File.separator+getProductName()+DOT_LOG;
    }

    public static void prepareDirs()
    {
        if (mCurrentFS != null)
        {
            return;
        }
        
        if (OsKit.isOsWindows())
        {
            mCurrentFS = new FileSystemWindows();
        }
        else if (OsKit.isOsMac())
        {
            mCurrentFS = new FileSystemMac();
        }
        else
        {
            mCurrentFS = new FileSystemLinux();
        }


        File fullpath = CodeKit.newFile(getInstallDir(), DIR_INSTALL_CONFIG);
        //代码调试时，需要进行判空。
        if(fullpath != null && !fullpath.exists())
        {
            fullpath.mkdirs();
        }

    }

    public  static String getConfigIniFile()
    {
        return getUserWorkTempDir()
            + File.separator + FileSystem.FILE_PRODUCT_VERSION_INFO
            + "." + getProductName();
    }
    
    private static String getValidDir(String dir)
    {
        if (dir == null)
        {
            return null;
        }
        
        //代码调试时，需要进行判空
        if (dir.endsWith("/") || dir.endsWith("\\"))
        {
            return dir.substring(0, dir.length()-1);
        }
        return dir;
    }
    
    public static void deleteAll(String path)
    {
        deleteAll(CodeKit.newFile(path));
    }
    
    /**
     * 删除当前文件的所有内容包含子目录。
     */
    private static void deleteAll(File path)
    {
        if (path == null || !path.exists()) // 如果文件为空，返回
        {
            return;
        }
        else if (path.isFile()) // 是文件
        {
            path.delete();
            return;
        }

        // 删除所有文件
        File[] files = path.listFiles();
        for (int i = 0; i < files.length; i++)
        {
            if (files[i].isFile())
            {
                files[i].delete();
            }
            else
            {
                deleteAll(files[i]);
            }
        }
        
        path.delete();
    }

    public static boolean loadNativeLib(final String lib)
    {
        try
        {
            if (!isJawtLoaded)
            {
                PathTraverser.processPath(FileSystem.getJreDir(), mCurrentFS);
            }
            
            String[] result = new String[2];
            if (   OsKit.isOsWindows() && lib.toLowerCase().endsWith(FileSystemWindows.NATIVE_LIB_SUFFIX)
                || OsKit.isOsLinux()   && lib.endsWith(FileSystemLinux.NATIVE_LIB_SUFFIX)
                || OsKit.isOsMac()     && lib.endsWith(FileSystemMac.NATIVE_LIB_SUFFIX))
            {
                getOsNativeLib(lib, result);
            }
            else
            {
                return false;
            }
            
            String libHome = getInstallDirNativeLib() + File.separatorChar + result[0];
            if (net.quantum6.platform.CodeKit.newFile(libHome).exists())
            {
                System.load(libHome);
            }
            else
            {
                System.loadLibrary(result[1]);
            }
            return true;
        }
        catch (Exception e)
        {
            net.quantum6.platform.TsLog.writeLog(e);
        }
        return false;
    }

    private static void getOsNativeLib(final String lib, String[] result)
    {
        String prefix = mCurrentFS.getNativeLibPrefix();
        
        if (lib.toLowerCase().startsWith(prefix))
        {
            result[0] = lib;
            result[1] = lib.substring(3, lib.length());
        }
        else
        {
            result[0] = prefix+lib;
            result[1] = lib;
        }

        String suffix = mCurrentFS.getNativeLibSuffix();
        //注意
        if (result[1].endsWith(suffix))
        {
            result[1] = result[1].substring(0, result[1].length()-suffix.length());
        }
        else
        {
            result[0] = result[0] + suffix;
        }
    }
    
    public static String getDirDesktop()
    {
        return mCurrentFS.getOsDirDesktop();
    }

    public static String getDirMyDocuments()
    {
        return mCurrentFS.getOsDirMyDocuments();
    }
    
    public static String getDirMyPictures()
    {
        return mCurrentFS.getOsDirMyPictures();
    }

    public static String getDirMyMusic()
    {
        return mCurrentFS.getOsDirMyMusic();
    }

    public static String getDirMyVideo()
    {
        return mCurrentFS.getOsDirMyVideo();
    }

    private static void saveAPP_DATA(String userAppdataPath)
    {
        try
        {
            if (userAppdataPath != null && userAppdataPath.length() > 0)
            {
                String installPath = userWorkDir;
                if (installPath != null)
                {
                    String systemPath = FileSystem.getUserConfigDir();
                    // 判断目录是否存在
                    File dir = net.quantum6.platform.CodeKit.newFile(systemPath);
                    if (!dir.exists())
                    {
                        dir.mkdir();
                    }
                    
                    String path = systemPath + File.separator + "appdata";
                    RandomAccessFile io = net.quantum6.platform.CodeKit.newRandomAccessFile(path, "rw");
                    
                    
                    long len = io.length();
                    String lineTxt = null;
                    if (len > 0)
                    {
                        io.seek(0);
                        lineTxt = io.readLine();
                        
                        while(!userAppdataPath.equals(lineTxt))
                        {
                            if (lineTxt != null)
                            {
                                lineTxt = io.readLine();
                            }
                            else
                            {
                                break;
                            }
                        }
                    }
                    
                    if (lineTxt == null)
                    {
                        io.seek(io.length());
                        io.write(userAppdataPath.getBytes());
                        io.write(System.getProperty("line.separator").getBytes());
                    }
                    io.close();
                    io = null;
                }
            }
        }
        catch(Exception e)
        {
            net.quantum6.platform.TsLog.writeLog(e);
            // do nothing
        }
    }
    
    private static List<File> getOpenCVFiles(final String dirName)
    {
        if (dirName == null)
        {
            return null;
        }
        File dir = new File(dirName);
        if (!dir.exists() || !dir.isDirectory())
        {
            return null;
        }
        
        File[] files = dir.listFiles();
        List<File> fileList = new LinkedList<File>();
        for (File file : files)
        {
            String name = file.getName();
            if (   name.startsWith(mCurrentFS.getNativeLibPrefix())
                && name.endsWith(  mCurrentFS.getNativeLibSuffix()))
            {
                fileList.add(file);
            }
        }
        return fileList;
    }
    
    public static void loadNativeOpenCV(final String dirName)
    {
        if (isOpenCVLoaded)
        {
            return;
        }
        
        List<File> fileList = getOpenCVFiles(dirName);
        if (fileList == null || fileList.size() == 0)
        {
            return;
        }
        
        while (fileList.size() > 0)
        {
            for (int i=0; i<fileList.size(); i++)
            {
                File file = fileList.get(i);
                try
                {
                    System.load(file.getAbsolutePath());
                }
                catch (java.lang.Throwable e)
                {
                    continue;
                }
                
                fileList.remove(i);
                i--;
            }
        }
        
        //如果libopencv_java.so在另外目录，需要单独加载
        //System.load(Dir+"/"+"lib"+Core.Core.NATIVE_LIBRARY_NAME+"."+LIB_SUFFIX_LINUX);
        isOpenCVLoaded = true;
    }

    public static String[] getFontDirs()
    {
        //System.getProperty("sun.java2d.fontpath");
        return mCurrentFS.getOsFontDirs();
    }

    public static String[] getFontFiles4First()
    {
        return FIRST_FONT_FILES;        
    }
    

    // {{{{{{{{{
    
    @Override
    public boolean onActionFile(File file)
    {
        if (file.toString().endsWith(mJawtLib))
        {
            System.load(file.toString());
            isJawtLoaded = true;
            return false;
        }
        return true;
    }
    
    @Override
    public boolean onActionDirectory(File file)
    {
        return true;
    }
    
    // }}}}}}}}}}
}
