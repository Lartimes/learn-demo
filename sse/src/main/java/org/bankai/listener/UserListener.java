package org.bankai.listener;

import org.bankai.TestApplication;
import org.bankai.mapper.UserActiveMapper;
import org.bankai.mapper.UserMapper;
import org.bankai.model.User;
import org.bankai.model.UserActive;
import org.bankai.service.SseManager;
import org.bankai.utils.RedisCacheUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class UserListener {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final Logger LOG = LoggerFactory.getLogger(UserListener.class);
    private final BlockingQueue<Integer> uidQueue = new LinkedBlockingQueue<>(1000);
    private final AtomicInteger queueSize = new AtomicInteger(0);
    private final UserActiveMapper userActiveMapper;
    private final SseManager sseManager;
    private final UserMapper userMapper;
    private final RedisCacheUtil redisCacheUtil;

    public UserListener(UserActiveMapper userActiveMapper, SseManager sseManager, UserMapper userMapper, RedisCacheUtil redisCacheUtil) {
        this.userActiveMapper = userActiveMapper;
        this.sseManager = sseManager;
        this.userMapper = userMapper;
        this.redisCacheUtil = redisCacheUtil;
    }

    @EventListener(classes = User.class)
    public void expireUser(User user) {

        Integer uid = user.getUid();
        uidQueue.add(uid);
        // 使用CAS操作确保只有一个线程执行发送逻辑
        if (queueSize.incrementAndGet() > 100) {
            if (queueSize.getAndSet(0) > 0) { // 重置计数前再次检查
                sendMessage();
                uidQueue.clear();
            }
        }

    }

    @Scheduled(fixedRate = 30_000) // 30秒，单位为毫秒
    public void sendMessage() {//当然是数据库 双重验证
        System.out.println("进来schedule sendMessage =====================");
        String startTime = LocalDateTime.now().format(formatter);
        LOG.info("定时任务开始执行，时间: {}", startTime);
        doBusiness();
        String endTime = LocalDateTime.now().format(formatter);
        LOG.info("定时任务执行完成，时间: {}", endTime);
    }

    private void doBusiness() {
        Collection<Integer> uids = popAllMessage();
        if (uids.isEmpty()) {
            return;
        }
        System.out.println("掉线的");
        System.out.println(uids);
        List<UserActive> userActives = userActiveMapper.selectInIds(uids);
        long nowExpireTime = System.currentTimeMillis();
        Date date = new Date(nowExpireTime);
//        ping 时间gap为2000ms ， 但是redis进行存储，db 10000ms间隔，所以放大三倍吧，30s
//        执行到这里，肯定有用户进来，不过要确保这个时间比较 正确 ，就设置2min吧？？？ 没办法了，不精准
        List<UserActive> destUsers = userActives.stream()
                .filter(userActive -> userActive.getLastActive().getTime() + (1000 * 10 * 3) <= nowExpireTime)
                .toList();
        destUsers.forEach(userActive -> {
            userActive.setExpireTime(date);
        });
        //过滤出来失效的
//        batchUpdate
        Integer updated = userActiveMapper.batchUpdateUserActive(destUsers);
        System.out.println("更新条数: " + updated);
//        更新完之后，进行推送好友下线通知
        userActives.parallelStream().forEachOrdered(userActive -> {
            String message = userActive.getUserId() + "下线了";
            Collection<Object> userIds = redisCacheUtil.getZSetByKey(String.format(TestApplication.FRIEND_KEY, userActive.getUserId()));
            if (!userIds.isEmpty()) {
                userIds.forEach(userId -> {
                    try {
                        sseManager.send(userId.toString(), SseManager.SseResponseBody.message(message));
                    } catch (IOException ignore) {
                        System.out.println("发送失败");
                    }
                });
            }
        });
    }

    public Set<Integer> popAllMessage() {
        Set<Integer> set = new TreeSet<>(uidQueue);
        uidQueue.clear();
        return set;
    }
}
