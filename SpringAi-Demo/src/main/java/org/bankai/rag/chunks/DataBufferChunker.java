package org.bankai.rag.chunks;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DataBufferChunker {
    public static void main(String[] args) throws Exception {
        String filepath = "SpringAi-Demo/src/main/resources/DACP_META_PROC_STEP_LOG+_20250620.csv";
        int chunkSize = 4 * 1024 * 1024; // 4MB
        double overlapRatio = 0.1; // 10%重叠
        int maxMemoryMB = 100;
        int maxChunksInMemory = maxMemoryMB * 1024 * 1024 / (int)(chunkSize * (1 + overlapRatio));
        int workerCount = 4;
        System.out.println(maxChunksInMemory);

        BlockingQueue<byte[]> queue = new ArrayBlockingQueue<>(maxChunksInMemory);
        ExecutorService pool = Executors.newFixedThreadPool(workerCount);

        // 消费者线程 , 消费模型后面也改成completeFuture，加入 原子性和
        for (int i = 0; i < workerCount; i++) {
            pool.submit(() -> {
                try {
                    while (true) {
                        byte[] chunk = queue.take();
                        if (chunk.length == 0) break;
                        // 这里处理分片
                        System.out.println(Thread.currentThread().getName() + " 处理分片: " + chunk.length);
//                        System.out.println(new String(chunk));
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        // 生产者线程（主线程）
        Path path = Paths.get(filepath);
        int overlapSize = (int) (chunkSize * overlapRatio);
        byte[] buffer = new byte[chunkSize + overlapSize];
        final int[] bufferLen = {0};

        Flux<DataBuffer> flux = DataBufferUtils.read(path, new DefaultDataBufferFactory(), 4096);

        flux.publishOn(Schedulers.boundedElastic()).doOnNext(dataBuffer -> {
            int readable = dataBuffer.readableByteCount();
            dataBuffer.read(buffer, bufferLen[0], readable);
            bufferLen[0] += readable;
            DataBufferUtils.release(dataBuffer);

            while (bufferLen[0] >= chunkSize) {
                byte[] chunk = new byte[chunkSize];
                System.arraycopy(buffer, 0, chunk, 0, chunkSize);
                try {
                    queue.put(chunk);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                // 滑动窗口：保留结尾overlapSize字节
                System.arraycopy(buffer, chunkSize - overlapSize, buffer, 0, overlapSize);
                bufferLen[0] = overlapSize;
            }
        }).doOnComplete(() -> {
            // 文件读完，处理最后一片
            if (bufferLen[0] > 0) {
                byte[] chunk = new byte[bufferLen[0]];
                System.arraycopy(buffer, 0, chunk, 0, bufferLen[0]);
                try {
                    queue.put(chunk);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            // 结束信号
            for (int i = 0; i < workerCount; i++) {
                try {
                    queue.put(new byte[0]);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }).blockLast();

        pool.shutdown();
    }
}