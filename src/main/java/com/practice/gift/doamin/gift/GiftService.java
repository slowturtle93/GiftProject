package com.practice.gift.doamin.gift;

public interface GiftService {

    GiftInfo getGiftInfo(String giftToken);

    GiftInfo registerOrder(GiftCommand.Register request);

    void requestPaymentProcessing(String giftToken);

    void completePayment(String orderToken);

    void acceptGift(GiftCommand.Accept request);
}
