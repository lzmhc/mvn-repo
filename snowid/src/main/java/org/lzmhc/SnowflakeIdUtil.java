package org.lzmhc;

public class SnowflakeIdUtil {
    // 单例实例（线程安全）
    private static final SnowflakeIdGenerator ID_GENERATOR = new SnowflakeIdGenerator();
    /**
     * 获取下一个雪花ID
     * @return 19位的Long型ID
     */
    public static long nextId() {
        return ID_GENERATOR.nextId();
    }
}
