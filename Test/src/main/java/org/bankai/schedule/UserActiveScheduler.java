package org.bankai.schedule;

import org.bankai.mapper.UserActiveMapper;
import org.bankai.service.SseManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class UserActiveScheduler {

    private final UserActiveMapper userActiveMapper;
    @Autowired
    private SseManager sseManager;

    public UserActiveScheduler(UserActiveMapper userActiveMapper) {
        this.userActiveMapper = userActiveMapper;
    }

    /**
     * 检验用户在线状态 进行推送，用户在线 xxx 几分钟了 10分钟一次， 之推送第一次？ 如何判断第一次？？？ 好友activeTime 是要小于等于(早于) 该用户的activeTime，说明早10分钟之前到来
     * 全表扫吧？？？？ --->
     * <p>
     * =================== 1. 登陆的时候推送已经在线的
     * 2. login——time 小于自己的 然后保持 active的 就可以的
     */
    @Scheduled(fixedRate = 30_000)
    public void checkUserActive() {
        long limit = 1000;
        long id = 0;


    }
}
