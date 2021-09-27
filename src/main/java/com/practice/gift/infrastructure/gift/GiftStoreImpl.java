package com.practice.gift.infrastructure.gift;

import com.practice.gift.common.exception.InvalidParamException;
import com.practice.gift.doamin.gift.Gift;
import com.practice.gift.doamin.gift.GiftStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class GiftStoreImpl implements GiftStore {

    private final GiftRepository giftRepository;

    /**
     * 선물하기 주문 등록
     *
     * @param gift
     * @return
     */
    @Override
    public Gift store(Gift gift) {
        if(gift == null) throw new InvalidParamException();
        return giftRepository.save(gift);
    }
}
