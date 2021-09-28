package com.practice.gift.application;

import com.practice.gift.doamin.gift.GiftCommand;
import com.practice.gift.doamin.gift.GiftInfo;
import com.practice.gift.doamin.gift.GiftService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GiftFacade {

    private final GiftService giftService;

    /**
     * 선물하기 주문 조회
     *
     * @param giftToken
     * @return
     */
    public GiftInfo getOrder(String giftToken){
        log.info("getOrder giftToken = {}", giftToken);
        return giftService.getGiftInfo(giftToken);
    }

    /**
     * 선물하기 주문 등록
     *
     * @param command
     * @return
     */
    public GiftInfo registerOrder(GiftCommand.Register command){
        var giftInfo = giftService.registerOrder(command);
        log.info("registerOrder orderToken = {}", giftInfo);
        return giftInfo;
    }

    /**
     * 선물하기 주문 상태 결제중 업데이트
     *
     * @param giftToken
     */
    public void requestPaymentProcessing(String giftToken){
        giftService.requestPaymentProcessing(giftToken);
    }

    /**
     * 선물하기 주문 상태 완료 업데이트
     *
     * @param orderToken
     */
    public void completePayment(String orderToken){
        giftService.completePayment(orderToken);
    }

    /**
     * 선물하기 수락
     *
     * @param request
     */
    public void acceptGift(GiftCommand.Accept request){
        giftService.acceptGift(request);
    }
}
