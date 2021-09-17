package com.practice.gift.doamin.gift;

public interface GiftReader {

    Gift getGiftBy(String giftToken);

    Gift getGiftByOrderToken(String orderToken);
}
