package net.quantum6.platform;

import java.awt.Toolkit;

/**
 * DPI会变，导致与像素相关的变化。
 * 而各单位之间的关系都不会变。
 *
 */
public final class GuiKit {

    /** 1英寸 = 2.54 cm */
    public  final static float IN_TO_CM     = 2.54F;

    /** 1英寸 = 72 cm */
    public  final static float IN_TO_PT     = 72F;

    /** 1 pich = 1/6 inch */
    public  final static float IN_TO_PI     = 6F;

    /** 1提=1/20磅 */
    public  final static float PT_TO_TI     = 20F;
    public  final static float TI_TO_PT     = 1.0F/PT_TO_TI;

    public  final static float PI_TO_PT     = 1.0F*IN_TO_PT/IN_TO_PI;

    public  final static float CM_TO_PT     = IN_TO_PT/IN_TO_CM;

    /**
     1行(line)=5磅
     1字行(charline)=12*磅
     */
    
    /**
     * 默认的dpi，即100%的情形。
     */
    private final static float DPI_DEFAULT  = 96F;

    private static int   mDpi;
    private static float mZoom;
    
    private static float mPtToPx;


    /**
     * 在系统初始化，
     */
    public static void updateDpi()
    {
        mDpi    = Toolkit.getDefaultToolkit().getScreenResolution();
        mZoom   = mDpi / DPI_DEFAULT;
        mPtToPx = mDpi / IN_TO_PT;
    }
    
    /**
     * 屏幕缩放比例。
     */
    public  final static float getScreenZoomRatio()
    {
        return mZoom;
    }

    public static int getDpi() {
        return mDpi;
    }

    public static float ptToPx() {
        return mPtToPx;
    }

    public static float cmToPx() {
        return CM_TO_PT * mPtToPx;
    }

    public static float pxToPt(float px)
    {
        return px/mPtToPx;
    }
}
