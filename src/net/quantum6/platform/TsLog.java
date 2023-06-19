/**
 * 请遵守量子开源协议(Quantum6 Open Source License)。
 * 
 * 作者：柳鲲鹏
 * 
 */

package net.quantum6.platform;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.quantum6.platform.filesystem.FileSystem;

/**
 * 1抓取异常，并保存到文件中。
 * 
 */
public final class TsLog implements Thread.UncaughtExceptionHandler
{
    private static long  MIN_TIME = 100;
    private static long  MAX_SIZE = 128*1024;
    
    //方便windows下查看
    private static String NEW_LINE = "\r\n";
    private static TsLog exceptionHandler = null;
    private static File logFile = null;
    private static long lastSaveTime = 0;

    //防止一种多存
    
    private TsLog()
    {
        //
    }
    
    public static TsLog getInstance()
    {
        if (exceptionHandler == null)
        {
            FileSystem.prepareDirs();

            synchronized (TsLog.class)
            {
                lastSaveTime = System.currentTimeMillis();
                exceptionHandler = new TsLog();
                logFile = net.quantum6.platform.CodeKit.newFile(FileSystem.getLogFile());
                Thread.setDefaultUncaughtExceptionHandler(exceptionHandler);
            }
        }
        return exceptionHandler;
    }
    
    public static void setLogFile(final String url)
    {
        logFile = net.quantum6.platform.CodeKit.newFile(url);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable)
    {
        writeExceptionToFile(null, throwable);
    }
    
    private void writeHead(BufferedWriter writer) throws IOException
    {
        writer.write(NEW_LINE);
        writer.write(FileSystem.getProductVersion());
        
        writer.write(NEW_LINE);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        writer.write(dateFormat.format(new Date()));
        writer.write(NEW_LINE);
    }
    
    private void writeExceptionToFile(final String text, final Throwable throwable)
    {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastSaveTime < MIN_TIME)
        {
            //return;
        }
        lastSaveTime = currentTime;
        
        try
        {
            boolean append = (!logFile.exists() || logFile.length() < MAX_SIZE);
            BufferedWriter writer = new BufferedWriter(net.quantum6.platform.CodeKit.newFileWriter(logFile, append));
            
            writeHead(writer);
            
            if (text != null)
            {
                
                writer.write(NEW_LINE);
                writer.write(text);
            }
            
            if (throwable != null)
            {
                throwable.printStackTrace();

                writer.write(NEW_LINE);
                writer.write(throwable.toString());
                StackTraceElement[] stes = throwable.getStackTrace();
                for (StackTraceElement ele : stes)
                {
                    writer.write(NEW_LINE);
                    writer.write(ele.toString());
                }
            }
            
            writer.write(NEW_LINE);
            
            writer.close();
        }
        catch (Exception e)
        {
            //TsLog.write();
        }
    }
    
    private void witeLogToFile(final String text)
    {
        System.out.println(text);
        try
        {
            boolean append = (logFile.exists() && logFile.length() < MAX_SIZE);
            BufferedWriter writer = new BufferedWriter(net.quantum6.platform.CodeKit.newFileWriter(logFile, append));
            
            writeHead(writer);

            writer.write(text);
            writer.write(NEW_LINE);
            
            writer.close();
        }
        catch (Exception e)
        {
            //TsLog.write();
        }
    }

    public static void writeLog(final String text)
    {
        synchronized (getInstance())
        {
            exceptionHandler.witeLogToFile(text);
        }
    }
    
    public static void writeLog(final Throwable throwable)
    {
        if (throwable == null || throwable instanceof NumberFormatException)
        {
            return;
        }

        synchronized (getInstance())
        {
            throwable.printStackTrace();
            exceptionHandler.writeExceptionToFile(null, throwable);
        }
    }
    
}
