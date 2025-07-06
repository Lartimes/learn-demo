package org.bankai.service;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import jakarta.annotation.PreDestroy;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class SseManager implements InitializingBean {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());
    private final ConcurrentMap<String, SseConnection> connections = new ConcurrentHashMap<>();
    // 服务运行状态
    private final AtomicBoolean running = new AtomicBoolean(true);
    // 连接清理任务
    private ScheduledFuture<?> cleanupTask;
    // 共享调度器 - 使用虚拟线程执行心跳任务
    private ScheduledExecutorService scheduler;
    // 虚拟线程任务执行器
    private ExecutorService virtualThreadExecutor;
    // 心跳间隔时间（毫秒）
    @Value("${sse.heartbeat:2000}")
    private long heartbeatInterval = 2000;
    // 清理失效连接的间隔时间
    @Value("${sse.clear:20000}")
    private long cleanupInterval = 20000;

    public SseEmitter getActiveSseEmitter(String uid) {
        SseConnection sseConnection = connections.get(uid);
        if (sseConnection != null) {
            return sseConnection.getSseEmitter();
        }
        return addConnection(uid);
    }


    public SseEmitter addConnection(String uid ) {
        // 创建新的SSE连接
        SseEmitter sseEmitter = new SseEmitter();
        sseEmitter.onCompletion(() -> {
            LOG.info("onCompletion");
            cleanupConnection(uid);
        });
        sseEmitter.onTimeout(() -> {
            LOG.info("onTimeout");
            cleanupConnection(uid);
        });
        sseEmitter.onError(
                throwable -> {
                    LOG.info("onError");
                    cleanupConnection(uid);
                }
        );
        SseConnection sseConnection = new SseConnection(new SseMetadata(uid, System.currentTimeMillis()),sseEmitter );
        // 创建心跳任务并保存引用
        ScheduledFuture<?> heartbeatTask = scheduler.scheduleAtFixedRate(() -> {
            sendHeartbeat(sseConnection);
        }, 0, heartbeatInterval, TimeUnit.MILLISECONDS);

        sseConnection.setHeartbeatTask(heartbeatTask);
        // 存储连接
        connections.put(uid, sseConnection);
        return sseConnection.getSseEmitter();
    }

    private void sendHeartbeat(SseConnection sseConnection) {

        SseEmitter emitter = sseConnection.getSseEmitter();

        if (emitter == null) {
            sseConnection.valid = false;
            return;
        }

        SseMetadata metadata = sseConnection.getSseMetadata();

        try {
            // 发送心跳消息
            emitter.send(SseEmitter.event()
                            .id(metadata.uid)
                            .data("ping")
//                    .data("ping" , MediaType.APPLICATION_JSON)
                            .comment(String.valueOf(metadata.timestamp))
                            .build()
            );
            LOG.info("========uid : {} 发送ping========", metadata.uid);
        } catch (Exception e) {
            e.printStackTrace();
//            只要sse 组件的map key存在，那就说明引用存在，sse 连接没断掉，用户在线， 可以再搞一个线程，
//           更新expire,关闭页面就直接下线了

            handleHeartbeatError(sseConnection, e);
        }
    }

    private void handleHeartbeatError(SseConnection sseConnection, Exception e) {
        // 记录错误日志
        System.err.println("Error sending SSE heartbeat: " + e.getMessage());

        // 标记连接为无效
        sseConnection.valid = false;
        cleanupConnection(sseConnection.getSseMetadata().uid);
        // 清理失效连接
        cleanupConnections();
    }

    /**
     * 删除无用的连接
     */
    private void cleanupConnections() {
        connections.entrySet().removeIf(entry -> {
            SseConnection conn = entry.getValue();
            return conn == null || !conn.valid;
        });
    }

    private void cleanupConnection(String uid) {
        LOG.info("删除uid：{}的sse 连接",uid);
        // 从活跃连接中移除
        SseConnection connection = connections.remove(uid);
        if (connection == null) {
            return;
        }

        // 取消心跳任务（即使任务在队列中，也会被标记为取消）
        ScheduledFuture<?> task = connection.getHeartbeatTask();
        if (task != null && !task.isCancelled()) {
            boolean cancelled = task.cancel(true); // true 表示中断正在执行的任务
            LOG.info("取消心跳任务，uid: {}, 取消结果: {}", uid, cancelled);
        }

        // 主动关闭 SseEmitter（避免资源泄漏）
        connection.getSseEmitter().complete();
    }

    @PreDestroy
    public void shutdown() {
        if (running.compareAndSet(true, false)) {
            // 取消清理任务
            cleanupTask.cancel(true);

            // 关闭线程池
            scheduler.shutdown();
            virtualThreadExecutor.shutdown();

            try {
                // 等待任务完成
                if (!scheduler.awaitTermination(30, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
                if (!virtualThreadExecutor.awaitTermination(30, TimeUnit.SECONDS)) {
                    virtualThreadExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                virtualThreadExecutor.shutdownNow();
                Thread.currentThread().interrupt();
            }

            // 清理所有连接
            connections.clear();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // 创建基于虚拟线程的调度器
        scheduler = Executors.newScheduledThreadPool(2, // 少量的平台线程用于调度
                new ThreadFactoryBuilder().setNameFormat("sse-scheduler-%d").build()
        );

        // 创建虚拟线程执行器
        virtualThreadExecutor = Executors.newVirtualThreadPerTaskExecutor();
        // 启动连接清理任务
        cleanupTask = scheduler.scheduleAtFixedRate(this::cleanupConnections,
                cleanupInterval,
                cleanupInterval,
                TimeUnit.MILLISECONDS);
        LOG.info("初始化sse manager 完成");
    }


    @Data
    private static class SseConnection {
        public SseMetadata sseMetadata;
        public SseEmitter sseEmitter;
        public volatile boolean valid = true;
        private ScheduledFuture<?> heartbeatTask;


        public SseConnection(SseMetadata sseMetadata, SseEmitter sseEmitter) {
            this.sseMetadata = sseMetadata;
            this.sseEmitter = sseEmitter;
        }

    }

    private record SseMetadata(String uid, long timestamp) {
    }
}
