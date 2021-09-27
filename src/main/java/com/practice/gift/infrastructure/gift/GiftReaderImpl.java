package com.practice.gift.infrastructure.gift;

import com.practice.gift.common.exception.EntityNotFoundException;
import com.practice.gift.common.exception.InvalidParamException;
import com.practice.gift.doamin.gift.Gift;
import com.practice.gift.doamin.gift.GiftReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class GiftReaderImpl implements GiftReader {

    private final GiftRepository giftRepository;

    /**
     * 선물하기 주문 조회
     *
     * @param giftToken
     * @return
     */
    @Override
    public Gift getGiftBy(String giftToken) {
        if(StringUtils.isEmpty(giftToken)) throw new InvalidParamException();
        return giftRepository.findByGiftToken(giftToken).orElseThrow(EntityNotFoundException::new);
    }

    /**
     * OrderToken 으로 선물하기 주문 조회
     *
     * @param orderToken
     * @return
     */
    @Override
    public Gift getGiftByOrderToken(String orderToken) {
        if(StringUtils.isEmpty(orderToken)) throw new InvalidParamException();
        return giftRepository.findByOrderToken(orderToken).orElseThrow(EntityNotFoundException::new);
    }
}
