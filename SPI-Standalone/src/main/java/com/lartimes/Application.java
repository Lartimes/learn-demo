package com.lartimes;

import com.lartimes.service.PaymentService;

import java.util.ServiceLoader;

/**
 * @author wüsch
 * @version 1.0
 * @description:
 * @since 2025/2/28 1:16
 */
public class Application {
    private static ServiceLoader<PaymentService> payServiceServiceLoader =
            ServiceLoader.load(PaymentService.class);

    public static void main(String[] args) {
        for (PaymentService payService : payServiceServiceLoader) {
            payService.doPay();
        }
    }
}
