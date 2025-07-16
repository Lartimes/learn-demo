package org.bankai.rag.chunks;

public enum FileChunksStrategy {

    DATA_BUFFER, // 多线程 + DataBuffer
    DATA_BUFFER_DISRUPTOR, //采用DataBuffer + Disruptor
    CIRCULAR_BUFFER, //参考MAPREDUCE 环形缓冲区，每一个task都一个独立的堆内存区

}
