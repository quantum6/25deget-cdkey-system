package net.quantum6.platform;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.FocusEvent;
import java.awt.image.BufferedImage;
import java.io.InputStream;

import javax.imageio.ImageIO;

public final class OsKit
{

    // 系统常量定义
    /** 未知的操作系统 */
    private static final int OS_UNKNOWN     = -1;
    // windows
    private static final int OS_WINDOWS     = 0;
    // linux
    private static final int OS_LINUX       = 1;
    /** MAC（苹果）操作系统 */
    private static final int OS_MACINTOSH   = 2;
    
    /** Windows操作系统,用于OS_LANGUAGE常量 */
    private static final int OS_WINDOWS_ENG = 0x40;
    private static final int OS_WINDOWS_ZCH = 0x41;
    private static final int OS_WINDOWS_TCH = 0x42;
    
    
    private final static String OS_NAME_WIN       = "win";
    private final static String OS_NAME_WINDOWS   = "windows";
    
    private final static String OS_NAME_MAC       = "mac";
    private final static String OS_NAME_LINUX     = "linux";

    private final static String OS_NAME_KYLIN     = "kylin";

    //这是同一个东西。
    private final static String OS_NAME_UOS       = "uos";
    private final static String OS_NAME_UNIONTECH = "uniontech";

    private final static String TERMINAL_MATE     = "mate-terminal";
    private final static String TERMINAL_UOS      = "x-terminal-emulator";

    /** 操作系统类型 */
    private static final int OS_CLASS;
    /** 区分语言版本的操作系统 */
    private static int OS_WIN_LANGUAGE = OS_UNKNOWN;

    private static final String  mCpuArch;
    private static final boolean isTsJDK;
    
    private static String  mOsName;
    private static boolean isOsLinuxUOS;
    private static boolean isOsLinuxKylin;
    
    static
    {
        GuiKit.updateDpi();
        
        /*
        java.util.Properties properties = System.getProperties();
        //遍历所有的属性
        for (String key : properties.stringPropertyNames()) {
            //输出对应的键和值
            Sys tem.out.println(key + "=" + properties.getProperty(key));
        }
        */
        
        mCpuArch = System.getProperty("os.arch").toLowerCase();
        isTsJDK  = System.getProperty("java.version").indexOf("taishan") > 0;
        
        OS_WIN_LANGUAGE = -1;
        
        // 操作系统的文本
        String osString = System.getProperty("os.name").toLowerCase();
        if (osString.startsWith(OS_NAME_MAC))
        {
            // 判断Mac操作系统，以“Mac OS X”开头
            OS_CLASS = OS_MACINTOSH;
            mOsName  = OS_NAME_MAC;
        }
        else if (osString.startsWith(OS_NAME_WIN))
        {
            OS_CLASS = OS_WINDOWS;
            // 判断是是否为中文Windows.实际应该使用“zh”来判断
            if ("en".equalsIgnoreCase(System.getProperty("user.language")))
            {
                OS_WIN_LANGUAGE = OS_WINDOWS_ENG;
            }
            else
            {
                OS_WIN_LANGUAGE = OS_WINDOWS_ZCH;
            }
            mOsName = OS_NAME_WINDOWS;
        }
        // 判断Linux操作系统
        else if (osString.startsWith(OS_NAME_LINUX))
        {
            OS_CLASS = OS_LINUX;
           
            String osText = runShellCommandForResult("cat /etc/issue", true);
            if (osText != null && !osText.isEmpty())
            {
                mOsName = osText.split(" ")[0];
                mOsName = mOsName.toLowerCase();

                int pos = osText.indexOf(' ');
                if (pos != -1)
                {
                    mOsName = osText.substring(0, pos).toLowerCase();
                    if (mOsName.equals(OS_NAME_UOS) || mOsName.equals(OS_NAME_UNIONTECH))
                    {
                        isOsLinuxUOS = true;
                        mOsName = OS_NAME_UOS;
                    }
                    else if (mOsName.contains(OS_NAME_KYLIN)) 
                    {
                        isOsLinuxKylin = true;
                    }
                }
            }
            else
            {
                mOsName = OS_NAME_LINUX;
            }
        }
        // 未知操作系统
        else
        {
            OS_CLASS = OS_UNKNOWN;
        }
    }

    public static void init()
    {
        //
    }
    
    public static String getOsName() {
        return mOsName;
    }

    public static String getCpuArch()
    {
        return mCpuArch;
    }
    
    public static boolean isTsJDK()
    {
        return isTsJDK;
    }
    
    public static boolean isOsWindowsZCH()
    {
        return (OS_WIN_LANGUAGE == OS_WINDOWS_ZCH);
    }

    public static boolean isOsWindowsENG()
    {
        return (OS_WIN_LANGUAGE == OS_WINDOWS_ENG);
    }

    public static boolean isOsWindowsTCH()
    {
        return (OS_WIN_LANGUAGE == OS_WINDOWS_TCH);
    }

    public static boolean isOsWindows()
    {
        return (OS_CLASS == OS_WINDOWS);
    }

    public static boolean isOsLinux()
    {
        return (OS_CLASS == OS_LINUX);
    }

    public static boolean isOsLinuxUos() {
        return isOsLinuxUOS;
    }

    public static boolean isOsLinuxKylin() {
        return isOsLinuxKylin;
    }

    /**
     * 以前的，已经不用了。待版本发布后删除之。
     * 请使用isOsMac()
     * @return
     */
    @Deprecated
    public static boolean isOsMacOld()
    {
        return false;
    }

    /**
     * 新写代码都要使用这个。
     * @return
     */
    public static boolean isOsMac() {
        return (OS_CLASS == OS_MACINTOSH);
    }
    
    public static String runCommandShell(final String[] cmd, final boolean wait)
    {
        try
        {
            if(cmd == null || cmd.length == 0) {
                return null;
            }
            
            Process process = null;
            if(cmd.length == 1) {
                process = Runtime.getRuntime().exec(cmd[0]);
            }
            else {
                process = Runtime.getRuntime().exec(cmd);
            }
            
            if (wait)
            {
                process.waitFor();
                
                byte[] data = new byte[2048];
                InputStream in = process.getInputStream();
                in.read(data); //子进程不退出的话 程序就会卡在这里
                return new String(data, System.getProperty("sun.jnu.encoding"));
            }
        }
        catch (Exception e)
        {
            TsLog.writeLog(e);
        }
        return "";
    }

    public static void runCommandForLinux(final String cmd, final boolean wait)
    {
        runCommandShell(new String[]{cmd}, wait); 
    }

    public static String runShellCommandForResult(final String cmd, final boolean wait)
    {
        return runCommandShell(new String[]{cmd}, wait);
    }

    public static void runChmodXForLinux(final String path)
    {
        if (OsKit.isOsWindows())
        {
            return;
        }
        
        // 复制完成后，设置可执行权限
        String cmds[] = {"/bin/sh", "-c", "chmod +x " + path + "*"};
        runCommandShell(cmds, true);
    }

    public static String getLinuxTerminal() {
    	String osString = getOsName();
    	if (osString.equals(OS_NAME_WINDOWS)) {
    		return "";
    	}
    	else if(osString.equals(OS_NAME_UOS)) {
    		return TERMINAL_UOS;
    	}
    	else {
    		return TERMINAL_MATE;
    	}
    }

    /**
     * 判断是否是root权限的用户
     * @return true有root权限;false没有root权限
     */
    public static boolean isRootLinux()
    {
        Process process = null;
        try
        {
            process = Runtime.getRuntime().exec("test -w /root");
            int result = process.waitFor();
            return (result == 0);
        }
        catch(Exception ex)
        {
            net.quantum6.platform.TsLog.writeLog(ex);
            return false;
        }
        finally
        {
            if (process != null)
            {
                process.destroy();
            }
        }
    }
    
    /**
     * 向指定的组件发送FOCUS_GAINED事件，避免Linux上执行java.awt.Container.remove(int)后输入法自动切换
     * @return void
     */ 
    public static void activateInputContextForLinux(Component comp) {
        if(comp == null) {
            return;
        }

        if(OsKit.isOsLinux()) {
            FocusEvent event = new FocusEvent(comp, FocusEvent.FOCUS_GAINED, false, null);
            if(comp.getInputContext() != null) {
                comp.getInputContext().dispatchEvent(event); 
            }
        }
    }
    
    public static BufferedImage screenShot(Rectangle rect)
    {
        try
        {   
            Robot robot = new Robot();
            BufferedImage image = robot.createScreenCapture(rect);
            if (image == null && isOsLinux())
            {
                Runtime runtime = Runtime.getRuntime();
                Process process;
                String[] command = { "/bin/sh", "-c", "dbus-send --session --type=method_call --print-reply --dest=org.kde.KWin /Screenshot org.kde.kwin.Screenshot.screenshotFullscreen | grep -P -o '[/\\w]+\\.(jpg|png)'" };
                final String SUN_JNU_ENCODING = "sun.jnu.encoding";
                byte[] data = new byte[256];
              
                process = runtime.exec(command);
                process.waitFor();
                String resultImageFilePath = null;

                if (process.exitValue() == 0)
                {
                    InputStream in = process.getInputStream();
                    int mContentLength = in.read(data);
                    resultImageFilePath = new String(data, 0, mContentLength, System.getProperty(SUN_JNU_ENCODING));
                    
                    while (resultImageFilePath.endsWith("\n") || resultImageFilePath.endsWith("\r")) {
                        resultImageFilePath = resultImageFilePath.substring(0, resultImageFilePath.length() - 1).trim();
                    }
                }
                
                if(resultImageFilePath != null && resultImageFilePath.length() > 0) {
                    image = ImageIO.read(CodeKit.newFileInputStream(resultImageFilePath));
                }
            }
            return image;
        } catch (Exception e) {
            TsLog.writeLog(e.getMessage());
        }
        return null;
    }
    

    /**
     * 1033 美国
     */
    public static final int LANGUAGE_DEFAULT     = 0x0409;
    /**
     * 1028 繁体中文
     */
    public static final int LANGUAGE_CHINESE_TW  = 0x0404;
    /**
     * 2052 简体中文
     */
    public static final int LANGUAGE_CHINESE_PRC = 0x0804;
    /**
     * 1033 美国
     */
    public static final int LANGUAGE_ENGLISH_USA = 0x0409;
    /**
     * 1041 日本
     */
    public static final int LANGUAGE_JAPANESE    = 0x0411;
    /**
     * 1042 韩国
     */
    public static final int LANGUAGE_KOREA       = 0x0412;
    /**
     * 获得系统默认的语言,通过该标记来确定如何显示字体名，
     * 字体名应该是根据操作系统来确定显示中文，还是西文，
     * 还是其他语言，改区域设置没有用的,只跟操作系统的类型
     * 有关系。
     * @return
     */
    public static int getDefaultLanguageID()
    {
        return DEFAULT_LANGUAGE_ID;
    }


    // 默认语言，这里不能直接调用方法，用于安装的时候会调用本类的静态方法，导致循环初始化。
    private static int DEFAULT_LANGUAGE_ID = LANGUAGE_CHINESE_PRC;
    
}
