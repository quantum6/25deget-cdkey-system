/**
 * 请遵守量子开源协议(Quantum6 Open Source License)。
 * 
 * 作者：柳鲲鹏
 * 
 */

package net.quantum6.platform.filesystem;

import java.io.File;

public final class PathTraverser
{

    private static boolean processFile(File file, PathProcessor processor)
    {
        return processor.onActionFile(file);
    }
    
    private static boolean processDirectory(File dir, PathProcessor processor)
    {
        if (!processor.onActionDirectory(dir))
        {
            return false;
        }
        
        File[] files = dir.listFiles();
        for (File file : files)
        {
            if (file.isDirectory())
            {
                if (!processDirectory(file, processor))
                {
                    return false;
                }
            }
            else if(!processFile(file, processor))
            {
                return false;
            }
        }
        return true;
    }

    public final static void processPath(File path, PathProcessor processor)
    {
        if (path.isDirectory())
        {
            processDirectory(path, processor);
        }
        else
        {
            processFile(path, processor);
        }
    }
    
    public final static void processPath(File[] paths, PathProcessor processor)
    {
        for (File p : paths)
        {
            processPath(p, processor);
        }
    }

    public final static void processPath(String path, PathProcessor processor)
    {
        processPath(new File(path), processor);
    }

    public final static void processPaths(String[] paths, PathProcessor processor)
    {
        for (String p : paths)
        {
            processPath(new File(p), processor);
        }
    }
}
