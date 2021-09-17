package com.practice.gift.doamin.gift;

import com.practice.gift.doamin.gift.order.OrderApiCaller;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GiftServiceImpl implements GiftService{

    private final GiftReader giftReader;
    private final GiftStore giftStore;
    private final OrderApiCaller orderApiCaller;
    private final GiftToOrderMapper giftToOrderMapper;

    /**
     * 선물 주문 정보 조회
     * 선물 수령자의 수락 페이지 로딩 시에 사용
     *
     * @param giftToken
     * @return
     */
    @Override
    public GiftInfo getGiftInfo(String giftToken) {
        var gift = giftReader.getGiftBy(giftToken); // 선물 주문 정보 조회
        return new GiftInfo(gift);
    }

    /**
     * 선물하기 주문 등록
     * 해당 주문을 주문 서비스에 등록하기 위해 API 를 호출하고
     * 등록된 주문의 식별키와 선물 관련 정보를 반영하여 Gift 도메인을 저장
     *
     * @param request
     * @return
     */
    @Override
    public GiftInfo registerOrder(GiftCommand.Register request) {
        var orderCommand = giftToOrderMapper.of(request); // Command Convert
        var orderToken = orderApiCaller.registerGiftOrder(orderCommand); // 주문토큰 조회
        var initGift = request.toEntity(orderToken);                       // 선물하기 정보 생성
        var gift = giftStore.store(initGift);                              // 선물하기 주문 등록
        return new GiftInfo(gift);
    }

    /**
     * 선물하기 주문의 상태를 [결제중] 으로 변경
     *
     * @param giftToken
     */
    @Override
    public void requestPaymentProcessing(String giftToken) {
        var gift = giftReader.getGiftBy(giftToken); // 선물하기 정보 조회
        gift.inPayment();
    }

    /**
     * 주문 서비스에서 결제 완료 후 orderToken 을 메시징으로 발행하면
     * 선물하기 서비스에서 이를 읽어서 선물 주문의 결제를 완료 상태로 변경
     *
     * @param orderToken
     */
    @Override
    public void completePayment(String orderToken) {
        var gift = giftReader.getGiftByOrderToken(orderToken); // 선물하기 정보 조회
        gift.completePayment();
    }

    /**
     * 선물 수령자가 배송지를 입력하고 [선물 수락] 하면
     * 선물 상태를 Accept 로 변경하고, 주문 서비스 API 를 호출하여 주문의 배송지 주소를 업데이트
     *
     * @param request
     */
    @Override
    public void acceptGift(GiftCommand.Accept request) {
        var giftToken = request.getGiftToken();    // 선물토큰 조회
        var gift = giftReader.getGiftBy(giftToken); // 선물 주문 정보 조회
        gift.accept(request);                             // 선물 상태 Accept 로 변경

        orderApiCaller.updateReceiverInfo(gift.getOrderToken(), request);
    }
}
