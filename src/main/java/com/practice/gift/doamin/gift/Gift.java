package com.practice.gift.doamin.gift;

import com.practice.gift.common.exception.IllegalStatusException;
import com.practice.gift.common.exception.InvalidParamException;
import com.practice.gift.common.util.TokenGenerator;
import com.practice.gift.doamin.AbstractEntity;
import lombok.*;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Getter
@Entity
@ToString
@NoArgsConstructor
@Table(name = "gifts")
public class Gift extends AbstractEntity {

    private static final String GIFT_PREFIX = "gt_";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long   id;          // 선물하기 고유번호
    private String giftToken;   // 선물하기 토큰
    private Long   buyerUserId; // 구매자 아이디
    private String orderToken;  // 주문 토큰

    @Enumerated(EnumType.STRING)
    private Status status; // 선물하기 상태

    @Enumerated(EnumType.STRING)
    private PushType pushType;        // 푸시타입
    private String giftReceiverName;  // 선물 수령인 명
    private String giftReceiverPhone; // 선물 수령인 전화번호
    private String giftMessage;       // 선물 메시지

    private String receiverName;     // 받는사람이름
    private String receiverPhone;    // 수령인 전화번호
    private String receiverZipcode;  // 수령인 우편번호
    private String receiverAddress1; // 수령인 주소1
    private String receiverAddress2; // 수령인 주소2
    private String etcMessage;       // 메시지

    private ZonedDateTime paidAt;     // 결제일시
    private ZonedDateTime pushedAt;   // 푸시발송일시
    private ZonedDateTime acceptedAt; // 선물수락일시
    private ZonedDateTime expiredAt;  // 선물수락만료일시

    @Getter
    @AllArgsConstructor
    public enum Status{
        INIT("선물 주문 생성"),
        IN_PAYMENT("결제 중"),
        ORDER_COMPLETE("주문 완료"),
        PUSH_COMPLETE("선물 링크 발송 완료"),
        ACCEPT("선물 수락"),
        DELIVERY_PREPARE("상품준비"),
        IN_DELIVERY("배송중"),
        DELIVERY_COMPLETE("배송완료"),
        EXPIRATION("선물 수락 만료");

        private final String description;
    }

    @Getter
    @AllArgsConstructor
    public enum PushType{
        KAKAO("카카오톡"),
        LMS("문자");

        private final String description;
    }

    @Builder
    public Gift(
            Long buyerUserId,
            String orderToken,
            PushType pushType,
            String giftReceiverName,
            String giftReceiverPhone,
            String giftMessage
    ){
        if (buyerUserId == null) throw new InvalidParamException("Gift constructor buyerUserId is null");
        if (pushType    == null) throw new InvalidParamException("Gift constructor pushType is null");
        if (StringUtils.isEmpty(giftReceiverName)) throw new InvalidParamException("Gift constructor giftReceiverName is empty");
        if (StringUtils.isEmpty(giftReceiverPhone)) throw new InvalidParamException("Gift constructor giftReceiverPhone is empty");
        if (StringUtils.isEmpty(giftMessage))       throw new InvalidParamException("Gift constructor giftMessage is empty");

        this.giftToken         = TokenGenerator.randomCharacterWithPrefix(GIFT_PREFIX);
        this.buyerUserId       = buyerUserId;
        this.orderToken        = orderToken;
        this.status            = Status.INIT;
        this.pushType          = pushType;
        this.giftReceiverName  = giftReceiverName;
        this.giftReceiverPhone = giftReceiverPhone;
        this.giftMessage       = giftMessage;
        this.expiredAt         = ZonedDateTime.now().plusDays(7);
    }

    // 선물주문상태 [결제중] 변경 메서드
    public void inPayment(){
        if(this.status != Status.INIT) throw new IllegalStateException("Gift inPayment");
        this.status = Status.IN_PAYMENT;
    }

    // 선물주문상태 [주문 완료] 변경 메서드
    public void completePayment() {
        if (this.status != Status.IN_PAYMENT) throw new IllegalStatusException("Gift paymentComplete");
        this.status = Status.ORDER_COMPLETE;
        this.paidAt = ZonedDateTime.now();
    }

    // 선물주문상태 [선물 링크 발송 완료] 변경 메서드
    public void pushLink() {
        if (this.status != Status.ORDER_COMPLETE) throw new IllegalStatusException("Gift pushLink");
        this.status   = Status.PUSH_COMPLETE;
        this.pushedAt = ZonedDateTime.now();
    }

    // 선물 수락 메서드
    public void accept(GiftCommand.Accept request) {
        var receiverName     = request.getReceiverName();
        var receiverPhone    = request.getReceiverPhone();
        var receiverZipcode  = request.getReceiverZipcode();
        var receiverAddress1 = request.getReceiverAddress1();
        var receiverAddress2 = request.getReceiverAddress2();
        var etcMessage       = request.getEtcMessage();

        if (!availableAccept())                    throw new IllegalStatusException();
        if (StringUtils.isEmpty(receiverName))     throw new InvalidParamException("Gift accept receiverName is empty");
        if (StringUtils.isEmpty(receiverPhone))    throw new InvalidParamException("Gift accept receiverPhone is empty");
        if (StringUtils.isEmpty(receiverZipcode))  throw new InvalidParamException("Gift accept receiverZipcode is empty");
        if (StringUtils.isEmpty(receiverAddress1)) throw new InvalidParamException("Gift accept receiverAddress1 is empty");
        if (StringUtils.isEmpty(receiverAddress2)) throw new InvalidParamException("Gift accept receiverAddress2 is empty");
        if (StringUtils.isEmpty(etcMessage))       throw new InvalidParamException("Gift accept etcMessage is empty");

        this.status           = Status.ACCEPT;
        this.receiverName     = receiverName;
        this.receiverPhone    = receiverPhone;
        this.receiverZipcode  = receiverZipcode;
        this.receiverAddress1 = receiverAddress1;
        this.receiverAddress2 = receiverAddress2;
        this.etcMessage       = etcMessage;
        this.acceptedAt       = ZonedDateTime.now();
    }

    // 선물 만료일자 Set 메서드
    public void expired() {
        this.status    = Status.EXPIRATION;
        this.expiredAt = ZonedDateTime.now();
    }

    // 선물 수락 가능 여부 확인
    private boolean availableAccept() {
        if (this.expiredAt.isBefore(ZonedDateTime.now())) return false;
        return this.status == Status.ORDER_COMPLETE || this.status == Status.PUSH_COMPLETE;
    }
}
