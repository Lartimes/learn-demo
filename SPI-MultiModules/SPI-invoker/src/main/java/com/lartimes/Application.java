package com.lartimes;

import com.lartimes.service.PayService;

import java.util.ServiceLoader;
import java.util.Spliterator;

/**
 * @author wüsch
 * @version 1.0
 * @description:
 * @since 2025/2/28 1:01
 */
public class Application {
    private static ServiceLoader<PayService> payServiceServiceLoader =
            ServiceLoader.load(PayService.class);

    public static void main(String[] args) {
        for (PayService payService : payServiceServiceLoader) {
            payService.doPay();
        }
//        工厂method


    }
}
