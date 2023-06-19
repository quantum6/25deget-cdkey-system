/**
 * 请遵守量子开源协议(Quantum6 Open Source License)。
 * 
 * 作者：柳鲲鹏
 * 
 */

package net.quantum6.cdkey;

class CdkeyConfig {

    public  final static int PART_SIZE     = 5;
    //public  final static int CDKEY_SIZE    = 25;
    
    /**
     * 10000之内的号保留给我司内部使用。
     */
    final static int CDKEY_START   = 10001;
    /**
     * 根据需要，也许会调整CDKEY的结构。
     */
    final static int CDKEY_VERSION = 3;
    final static String CDKEY_PASSWORD   = "Quantum Office";

    final static String CHARSET    = "utf-8";

    final static String PKI_ALGORITHM = "RSA";

    /**
     * 密钥长度 于原文长度对应 以及越长速度越慢
     */
    final static int PKI_DIGIT = 4096;

    static final String PKC_ALGORITHM = "Blowfish";

}
