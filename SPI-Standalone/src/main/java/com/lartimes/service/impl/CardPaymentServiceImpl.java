package com.lartimes.service.impl;

import com.lartimes.service.PaymentService;

/**
 * @author wüsch
 * @version 1.0
 * @description:
 * @since 2025/2/28 1:16
 */
public class CardPaymentServiceImpl implements PaymentService {
    @Override
    public void doPay() {
        System.out.println("card支付");
    }
}
