package net.quantum6.platform;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.color.ColorSpace;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.lang.reflect.Constructor;
import java.net.URLDecoder;

/**
 * 工具包。
 * 本类不依赖于其他。
 * 
 * 路径形式：
 * 
 * windows: d:\
 * linux:   /hmoe
 * java /file:/
 * 
 * 网络形式：
 * http://
 * file://
 * ftp://
 * \\
 * 
 */
public final class CodeKit
{
    public final static String WINDOWS_NET_HEAD = "\\\\";
    public final static String LINUX_ROOT = "/";
    public final static char   XML_SEPARATOR = '/';
    
    public final static char   DISPLAY_MNINOMIC = '/';
    
    public final static String WINDOWS_98 = "Windows 98";
    public final static String WINDOWS_ME = "Windows Me";
    
    private static final String SUN_JNU_ENCODING = "sun.jnu.encoding";
    public static final int BUFFER_LENGTH = 1024;
    
    public final static String[] LINUX_BROWSERS =
        {
            "gnome-www-browser",  // for uos
            "konqueror",
            "mozilla",
            "firefox"
        };

    public final static String[] LINUX_EDITORS =
        {
            "dedit",             // for uos
            "kwrite",
            "kedit",
            "gedit",
            "pluma"
        };


    /**
     * 调用时更方便。
     * 
     * @param millis
     */
    public static void sleep(final long millis)
    {
        if (millis < 0)
        {
            return;
        }
        try
        {
            Thread.sleep(millis);
        }
        catch (Exception e)
        {
            net.quantum6.platform.TsLog.writeLog(e);
            //
        }
    }
    
    public static void dumpIntToHex(int[] data)
    {
        int count = data.length;
        if (count > 128)
        {
            count = 128;
        }
        
        for (int i=0; i<count; i++)
        {
            if (i % 8 == 0)
            {
                
            }
            String text = Integer.toHexString(data[i]);
            int left = 8-text.length();
            for (int j=0; j<left; j++)
            {
                text = "8"+text;
            }
            
        }
        
    }

	public static void dumpClassConstructor(final Class<?> clazz)
	{
		Constructor<?>[] cons = clazz.getConstructors();
		for (int i=0; i<cons.length; i++)
		{
			StringBuilder sb = new StringBuilder();
			sb.append(i);
			sb.append('=');
			sb.append('(');
			
			Constructor<?> con = cons[i];
			Class<?>[] types = con.getParameterTypes();
			for (int j=0; j<types.length; j++)
			{
				sb.append(j);
				sb.append('=');
				sb.append(types[j].toString());
				sb.append(',');
				sb.append(' ');
			}
			
			sb.append(')');
			
		}
		
	}
	
	public static String checkPathForOs(String path)
	{
	    if (isStringEmpty(path))
	    {
	        return path;
	    }
	    
	    if (path.startsWith("ftp:/") || path.startsWith("file:/"))
	    {
	        return path;
	    }
	    char src;
	    char dest;
	    if (File.separatorChar == '/')
	    {
	        src  = '\\';
	        dest = '/';
	    }
	    else
	    {
            src  = '/';
            dest = '\\';
	    }
	    return path.replace(src, dest);
	}
	
	//{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{ 
	
	public static File newFile(final String path)
	{
	    if (null == path)
	    {
	        return null;
	    }
	    return new File(checkPathForOs(path));
	}

    public static File newFile(final String dir, final String name)
    {
        if (null == dir || null == name)
        {
            return null;
        }
        return newFile(newFile(dir), name);
    }

    public static File newFile(final File dir, final String name)
    {
        return new File(dir, checkPathForOs(name));
    }

	//}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}
	
	//{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{ 
    public static FileOutputStream newFileOutputStream(final String path) throws FileNotFoundException
    {
        return newFileOutputStream(newFile(path), false);
    }

    public static FileOutputStream newFileOutputStream(final String path, boolean append) throws FileNotFoundException
	{
	    return newFileOutputStream(newFile(path), append);
	}
	
    public static FileOutputStream newFileOutputStream(final File path) throws FileNotFoundException
    {
        return newFileOutputStream(path, false);
    }

    public static FileOutputStream newFileOutputStream(final File path, boolean append) throws FileNotFoundException
    {
        return new FileOutputStream(path, append);
    }
    
    public static FileOutputStream newFileOutputStream(FileDescriptor fdObj)
    {
        return new FileOutputStream(fdObj);
    }
    
    //}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}

    //{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{ 
    
    public static FileInputStream newFileInputStream(final String path) throws FileNotFoundException
    {
        return newFileInputStream(newFile(path));
    }

    public static FileInputStream newFileInputStream(final File path) throws FileNotFoundException
    {
        if (path.exists())
        {
            return new FileInputStream(path);
        }
        return null;
    }
    
    //}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}

    //{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{ 

    public static FileWriter newFileWriter(final String path) throws IOException
    {
        return newFileWriter(newFile(path), false);
    }
    
    public static FileWriter newFileWriter(final String path, boolean append) throws IOException
    {
        return newFileWriter(newFile(path), append);
    }

    public static FileWriter newFileWriter(final File path) throws IOException
    {
        return newFileWriter(path, false);
    }
    
    public static FileWriter newFileWriter(final File path, boolean append) throws IOException
    {
        return new FileWriter(path, append);
    }

    //}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}

    //{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{ 

    public static FileReader newFileReader(final String path) throws IOException
    {
        return newFileReader(newFile(path));
    }
    
    public static FileReader newFileReader(final File path) throws IOException
    {
        return new FileReader(path);
    }
    
    //}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}

    //{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{
    
    public static Color newColor(final int c, boolean hasalpha)
    {
        return new Color(c, hasalpha);
    }

    public static Color newColor(final int c)
    {
        return new Color(c);
    }

    public static Color newColor(final int r, final int g, final int b)
    {
        return new Color(r, g, b);
    }

    public static Color newColor(final int r, final int g, final int b, final int a)
    {
        return new Color(r, g, b, a);
    }
    
    public static Color newColor(float r, float g, float b)
    {
        return new Color(r, g, b);
    }
    
    public static Color newColor(ColorSpace cspace, float components[], float alpha)
    {
        return new Color(cspace, components, alpha);
    }

    //}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}
    
    
    //{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{
    
    /**
     * - 在LINUX平台上，如果设置了，反而不对。是全部还是个别？因为不确定，又不想引起新问题，
     * - 所以碰到一个改一个
     * - 有人就问了，那XOR就不能用了？吾怀疑 可以用：
     * - 现有做法可能不适用于LINUX。比如说顺序上产生了差异。
     * - 现有做法实际上没有利用XOR。比如实际上是全绘。
     * - 以后有机会再仔细分析。
     * 
     * @param g
     * @param color
     * @return
     */
    public static boolean setXORMode(Graphics g, Color color)
    {
        if (OsKit.isOsLinux())
        {
            return false;
        }
        g.setXORMode(color);
        return true;
    }
    //}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}

    //{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{
    public static RandomAccessFile newRandomAccessFile(final String file, final String mode) throws FileNotFoundException
    {
        return newRandomAccessFile(newFile(file), mode);
    }
    
    public static RandomAccessFile newRandomAccessFile(final File file, final String mode) throws FileNotFoundException
    {
        if (!file.exists() && mode.equals("r"))
        {
            return null;
        }
        return new RandomAccessFile(file, mode);
    }

    public static RandomAccessFile newBufferedRandomAccessFile(final String file, final String mode) throws FileNotFoundException
    {
        return newBufferedRandomAccessFile(newFile(file), mode);
    }

    public static RandomAccessFile newBufferedRandomAccessFile(final File file, final String mode) throws FileNotFoundException
    {
        return new RandomAccessFile(file, mode);
    }

    /**
     * 得到文件名，如通过c:/abc.jpg得到abc
     * 
     * @param name 图片路径名
     */
    public static String getName(String name)
    {
        if (name != null)
        {
            int index = name.lastIndexOf(File.separatorChar);
            return index != -1 ? name.substring(index + 1, name.lastIndexOf('.')) : name.substring(
                0, name.lastIndexOf('.'));
        }
        return null;
    }

    /**
     * 得到标准的文件路径名(不处理linux下的路径，因为在linux下生成的图片文件名很变态，若处理会使图片读取有错）
     * 
     * @param path 文件路径名
     */
    public static String getFormatPath(String path)
    {
        if (path != null && File.separatorChar == '\\')
        {
            int index = 0;
            if (path.indexOf(':') != -1)
            {
                while (path.indexOf('/', index) == index || path.indexOf('\\', index) == index)
                {
                    index++;
                }
            }
            path = index == 0 ? path : path.substring(index, path.length());
            // if(IS_RIGHT_SEPARATOR)
            // {
            if (path.indexOf('/') != -1)
            {
                path = path.replace('/', File.separatorChar);
            }
            return path.toLowerCase();
        }
        return path;
    }

    public static void gc()
    {
        gc(1);
    }
    
    /**
     * 检查哪里调用了。 
     */
    public static void gc(final int times)
    {
        for (int i=0; i<times; i++)
        {
            System.gc();
        }
    }
    
    public static boolean isStringData(final String str)
    {
        return (str != null && str.length()  > 0);
    }

    public static boolean isStringEmpty(final String str)
    {
        return (str == null || str.length() == 0);
    }

    /**
     * String.isBlank()从JDK11开始，在LINUX JDK8上无法编译。
     */
    public static boolean isStringBlank(final String str)
    {
        return (str != null && str.trim().length() == 0);
    }
    
    /**
     * 比较两个文件是否相同
     * 
     * @param path1 文件1
     * @param path2 文件2
     */
    public static boolean compareFile(String path1, String path2)
    {
        path1 = getFormatPath(path1);
        path2 = getFormatPath(path2);
        if (path1.equals(path2))
        {
            return true;
        }
        File f1 = newFile(path1);
        File f2 = newFile(path2);
        long len = f1.length();
        if (len != f2.length())
        {
            f1 = null;
            f2 = null;
            return false;
        }
        boolean equal = true;
        int skipCount = (int)(len / 50);
        FileInputStream fin1 = null;
        FileInputStream fin2 = null;
        try
        {
            fin1 = newFileInputStream(f1);
            fin2 = newFileInputStream(f2);
            int value = fin1.read();
            while (value != -1)
            {
                if (value != fin2.read())
                {
                    equal = false;
                    break;
                }
                fin1.skip(skipCount);
                fin2.skip(skipCount);
                value = fin1.read();
            }
            fin1.close();
            fin1 = null;
            fin2.close();
            fin2 = null;
        }
        catch(IOException e)
        {
            net.quantum6.platform.TsLog.writeLog(e);
            fin1 = null;
            fin2 = null;
            equal = false;
        }
        f1 = null;
        f2 = null;
        return equal;
    }

    public static int toInt(final byte[] a, final int start)
    {
        return     (a[start+3] & 0xFF)
                | ((a[start+2] & 0xFF) <<  8)
                | ((a[start+1] & 0xFF) << 16)
                | ((a[start  ] & 0xFF) << 24);
    }
    
    public static int toInt(byte a, byte b, byte c, byte d)
    {
        return (d & 0xFF) | ((c & 0xFF) << 8) | ((b & 0xFF) << 16) | ((a & 0xFF) << 24);
    }

    public static short toShort(byte[] a, final int start)
    {
        return (short) ((a[start+1] & 0xFF)
                     | ((a[start  ] & 0xFF) <<  8));
    }
    
    public static short toShort(byte a, byte b)
    {
        return (short) ((b & 0xFF) | ((a & 0xFF) << 8));
    }
    
    //}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}
    
    public static String decodeFileName(String fileName)
    {
    	try
    	{
    		return URLDecoder.decode(fileName, "utf8");
    	}
    	catch (Exception e)
    	{
        	return fileName;
    	}
    }
    
    public static Class<?> loadClass(String className, ClassLoader classLoader)
        throws ClassNotFoundException
    {
        if (classLoader == null)
        {
            // Look up the class loader to be used
            classLoader = Thread.currentThread().getContextClassLoader();
            if (classLoader == null)
            {
                classLoader = CodeKit.class.getClassLoader();
            }
        }
    
        // Attempt to load the specified class
        return (classLoader.loadClass(className));
    }
    
    public static boolean checkFlag(final int flag, final int flags)
    {
        return ((flags & flag) > 0);
    }
    
    public static String runShellCommand(final String command, int bufferLength, final boolean toWait) {
        String result = null;
        Runtime runtime = Runtime.getRuntime();
        if(bufferLength <= 0) {
            bufferLength = BUFFER_LENGTH;
        }
        
        if(CodeKit.isStringEmpty(command)) {
            return result;
        }
        
        byte[] data = new byte[bufferLength];
        Process process;
        
        try {
            process = runtime.exec(command);

            if (toWait) {
                process.waitFor();

                InputStream in = process.getInputStream();
                int length = in.read(data);
                result = new String(data, 0, length, System.getProperty(SUN_JNU_ENCODING));
                
                while (result.endsWith("\n") || result.endsWith("\r")) {
                    result = result.substring(0, result.length() - 1).trim();
                }
            }
        } catch (Exception e) {
            TsLog.writeLog(e.getMessage());
        }
        
        return result;
    }
    
    public enum LoopReturn
    {
        GOON,
        CONTINUE,
        BREAK,
        RETURN
    }

    /**返回是否正在录制宏状态。
     *@return true表示正在录制。
     */
    public static boolean isRecording()
    {
        return false;
    }

    /**设置录制宏状态。
     *@param true录制。
     */
    public static void setMacroRecording(boolean recording)
    {
        
    }
    
    public static int[] checkArraySize(int[] data, int count)
    {
        if (data == null)
        {
            data = new int[count];
            return data;
        }
        
        if (count >= data.length)
        {
            int[] temp = data;
            data = new int[count*2];
            System.arraycopy(temp, 0, data, 0, temp.length);
        }
        return data;
    }
}
