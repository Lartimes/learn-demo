package com.lartimes.service.pay;

import com.lartimes.service.PayService;

/**
 * @author wüsch
 * @version 1.0
 * @description:
 * @since 2025/2/28 0:56
 */
public class ZFBPaymentServiceImpl implements PayService {
    @Override
    public void doPay() {
        System.out.println("支付宝支付");
    }
}
