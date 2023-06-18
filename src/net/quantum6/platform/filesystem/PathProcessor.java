package net.quantum6.platform.filesystem;

import java.io.File;

public interface PathProcessor
{

    /**
     * 返回false，表示中断。
     */
    boolean onActionFile(File file);
    
    /**
     * 返回false，表示中断。
     */
    boolean onActionDirectory(File file);

}
