package org.bankai.rag.chunks;

import org.springframework.core.io.buffer.DefaultDataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;

public class FileChunks {
    public void test(){
        DefaultDataBufferFactory sharedInstance = DefaultDataBufferFactory.sharedInstance;
        DefaultDataBuffer defaultDataBuffer = sharedInstance.allocateBuffer(1024);

    }
}
