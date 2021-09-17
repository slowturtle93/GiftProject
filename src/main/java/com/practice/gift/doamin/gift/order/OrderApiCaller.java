package com.practice.gift.doamin.gift.order;

import com.practice.gift.doamin.gift.GiftCommand;

public interface OrderApiCaller {

    String registerGiftOrder(OrderApiCommand.Register request);

    void updateReceiverInfo(String orderToken, GiftCommand.Accept request);
}
