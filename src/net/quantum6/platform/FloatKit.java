/**
 * 请遵守量子开源协议(Quantum6 Open Source License)。
 * 
 * 作者：柳鲲鹏
 * 
 */

package net.quantum6.platform;


/**
 * 浮点的判断是有点麻烦的。
 * 此处的问题，都是指相近的情形。
 * 
 * 正确的算法应该是放大后队
 *
 */
public final class FloatKit
{
    private final static int TIMES = 1000;
    
    private static boolean isIntEqual(float f1, float f2)
    {
        return ((int)f1 == (int)f2);
    }
    
    public static boolean equal(float f1, float f2)
    {
        if (isIntEqual(f1, f2))
        {
            return ((int)(f1*TIMES) == (int)(f2*TIMES));
        }
        return false;
    }

    public static boolean notEqual(float f1, float f2)
    {
        if (isIntEqual(f1, f2))
        {
            return !((int)(f1*TIMES) == (int)(f2*TIMES));
        }
        return true;
    }

    public static boolean less(float f1, float f2)
    {
        if (isIntEqual(f1, f2))
        {
            return ((int)(f1*TIMES) <  (int)(f2*TIMES));
        }
        return (f1 < f2);
    }

    public static boolean lessEqual(float f1, float f2)
    {
        if (isIntEqual(f1, f2))
        {
            return ((int)(f1*TIMES) <= (int)(f2*TIMES));
        }
        return (f1 <= f2);
    }

    public static boolean great(float f1, float f2)
    {
        if (isIntEqual(f1, f2))
        {
            return ((int)(f1*TIMES) >  (int)(f2*TIMES));
        }
        return (f1 > f2);
    }

    public static boolean greatEqual(float f1, float f2)
    {
        if (isIntEqual(f1, f2))
        {
            return ((int)(f1*TIMES) >= (int)(f2*TIMES));
        }
        return (f1 >= f2);
    }

}
