package org.bankai.schedule;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class UserActiveScheduler {

    /**
     * 检验用户在线状态
     * 进行推送，用户在线 xxx 几分钟了
     *
     */
    @Scheduled
    public void  checkUserActive() {

    }
}
