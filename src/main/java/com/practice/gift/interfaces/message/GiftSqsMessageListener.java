package com.practice.gift.interfaces.message;

import com.practice.gift.application.GiftFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class GiftSqsMessageListener {
    private final GiftFacade giftFacade;

    /**
     * @SqsListener
     * value - 생성한 SQS 이름
     * deletionPolicy - 메세지를 받은 후 삭제 정책
     * (ON_SUCCESS - SqsListener 어노테이션이 붙은 메서드에서 에러가 나지 않으면 메세지 삭제,
     * 에러가 난 경우에는 메세지 삭제안함)
     *
     * @param message
     */
    @SqsListener(value = "khs-order-payComplete-live.fifo", deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
    public void readMessage(GiftPaymentCompleteMessage message){
        var orderToken = message.getOrderToken();
        log.info("[GiftSqsMessageListener.readMessage] orderToken = {}", orderToken);
        giftFacade.completePayment(orderToken);
    }
}
