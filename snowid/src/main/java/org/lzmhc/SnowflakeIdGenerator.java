package org.lzmhc;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.time.Instant;

/**
 * 雪花ID生成器 (Snowflake ID Generator)
 * 生成一个19位的Long型ID
 * 结构: | 时间戳 (41位) | 数据中心ID (5位) | 机器ID (5位) | 序列号 (12位) |
 */
public class SnowflakeIdGenerator {

    // ====================== 配置参数 ==========================
    /** 开始时间截 (2023-01-01) */
    private static final long TWEPOCH = 1672531200000L;

    /** 机器ID所占的位数 */
    private static final long WORKER_ID_BITS = 5L;
    /** 数据中心ID所占的位数 */
    private static final long DATA_CENTER_ID_BITS = 5L;
    /** 序列在ID中占的位数 */
    private static final long SEQUENCE_BITS = 12L;

    /** 支持的最大机器ID，结果是31 */
    private static final long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);
    /** 支持的最大数据中心ID，结果是31 */
    private static final long MAX_DATA_CENTER_ID = ~(-1L << DATA_CENTER_ID_BITS);
    /** 机器ID向左移12位 */
    private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;
    /** 数据中心ID向左移17位(12+5) */
    private static final long DATA_CENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;
    /** 时间戳向左移22位(5+5+12) */
    private static final long TIMESTAMP_LEFT_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATA_CENTER_ID_BITS;

    /** 生成序列的掩码，这里为4095 (0b111111111111=0xfff=4095) */
    private static final long SEQUENCE_MASK = ~(-1L << SEQUENCE_BITS);

    // ================== 工作参数 =====================
    /** 数据中心ID (0-31) */
    private long dataCenterId;
    /** 机器ID (0-31) */
    private long workerId;
    /** 毫秒内序列(0-4095) */
    private long sequence = 0L;
    /** 上次生成ID的时间截 */
    private long lastTimestamp = -1L;

    //============================== 构造函数 ==============================
    public SnowflakeIdGenerator() {
        this.dataCenterId = getDataCenterId(MAX_DATA_CENTER_ID);
        this.workerId = getWorkerId(dataCenterId, MAX_WORKER_ID);
    }

    public SnowflakeIdGenerator(Snowflake snowflake) {
        int providedDataCenterId = snowflake.dataCenterId();
        int providedWorkerId = snowflake.workerId();

        // 如果用户指定了值，则使用；否则使用默认逻辑
        this.dataCenterId = getDataCenterId(MAX_DATA_CENTER_ID);
        this.workerId = getWorkerId(this.dataCenterId, MAX_WORKER_ID);

        // 验证参数范围
        if (this.dataCenterId > MAX_DATA_CENTER_ID || this.dataCenterId < 0) {
            throw new IllegalArgumentException(
                    String.format("dataCenterId (%d) can't be greater than %d or less than 0",
                            this.dataCenterId, MAX_DATA_CENTER_ID));
        }
        if (this.workerId > MAX_WORKER_ID || this.workerId < 0) {
            throw new IllegalArgumentException(
                    String.format("workerId (%d) can't be greater than %d or less than 0",
                            this.workerId, MAX_WORKER_ID));
        }
    }
    // ================== 核心方法 =====================
    /**
     * 获得下一个ID (该方法是线程安全的)
     * @return SnowflakeId
     */
    public synchronized long nextId() {
        long timestamp = timeGen();

        // 如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过，抛出异常
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(
                    String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }

        // 如果是同一时间生成的，则进行毫秒内序列
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & SEQUENCE_MASK;
            // 毫秒内序列溢出
            if (sequence == 0) {
                // 阻塞到下一个毫秒，获得新的时间戳
                timestamp = tilNextMillis(lastTimestamp);
            }
        }
        // 时间戳改变，毫秒内序列重置
        else {
            sequence = 0L;
        }

        // 上次生成ID的时间截
        lastTimestamp = timestamp;

        // 移位并通过或运算拼成最终的ID
        return ((timestamp - TWEPOCH) << TIMESTAMP_LEFT_SHIFT) //
                | (dataCenterId << DATA_CENTER_ID_SHIFT) //
                | (workerId << WORKER_ID_SHIFT) //
                | sequence;
    }

    /**
     * 阻塞到下一个毫秒，直到获得新的时间戳
     * @param lastTimestamp 上次生成ID的时间截
     * @return 当前时间戳
     */
    private long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    /**
     * 返回以毫秒为单位的当前时间
     * @return 当前时间(毫秒)
     */
    private long timeGen() {
        return Instant.now().toEpochMilli();
    }

    //============================ 支持方法 =========================
    /**
     * 获取机器ID
     * @param dataCenterId 数据中心ID
     * @param maxWorkerId 最大机器ID
     * @return 机器ID
     */
    protected static long getWorkerId(long dataCenterId, long maxWorkerId) {
        StringBuilder sb = new StringBuilder();
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            NetworkInterface network = NetworkInterface.getByInetAddress(inetAddress);
            byte[] mac = network.getHardwareAddress();
            sb.append(mac[mac.length - 2]);
            sb.append(mac[mac.length - 1]);
        } catch (Exception e) {
            sb.append(1);
        }
        return (sb.toString().hashCode() & 0xffff) % (maxWorkerId + 1);
    }

    /**
     * 获取数据中心ID
     * @param maxDataCenterId 最大数据中心ID
     * @return 数据中心ID
     */
    protected static long getDataCenterId(long maxDataCenterId) {
        long id = 0;
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            NetworkInterface network = NetworkInterface.getByInetAddress(inetAddress);
            if (network != null) {
                byte[] mac = network.getHardwareAddress();
                if (mac != null) {
                    id = ((0x000000FF & (long) mac[mac.length - 1]) |
                            (0x0000FF00 & (((long) mac[mac.length - 2]) << 8))) >> 6;
                    id = id % (maxDataCenterId + 1);
                }
            }
        } catch (Exception e) {
            // ignore
        }
        return id;
    }
}
