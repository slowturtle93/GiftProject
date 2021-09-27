package com.practice.gift.infrastructure.gift.order;

import com.practice.gift.common.response.CommonResponse;
import com.practice.gift.doamin.gift.GiftCommand;
import com.practice.gift.doamin.gift.order.OrderApiCaller;
import com.practice.gift.doamin.gift.order.OrderApiCommand;
import com.practice.gift.infrastructure.retrofit.RetrofitUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderApiCallerImpl implements OrderApiCaller {

    private final RetrofitUtils retrofitUtils;
    private final RetrofitOrderApi retrofitOrderApi;

    /**
     * 선물하기 주문 등록
     *
     * @param request
     * @return
     */
    @Override
    public String registerGiftOrder(OrderApiCommand.Register request) {
        var call = retrofitOrderApi.registerOrder(request);
        return retrofitUtils.responseSyne(call)
                .map(CommonResponse::getData)
                .map(RetrofitOrderApiResponse.Register::getOrderToken)
                .orElseThrow(RuntimeException::new);
    }

    /**
     * 선물하기 수령인 주소 정보 업데이트
     *
     * @param orderToken
     * @param request
     */
    @Override
    public void updateReceiverInfo(String orderToken, GiftCommand.Accept request) {
        var call = retrofitOrderApi.updateReceiverInfo(orderToken, request);
        retrofitUtils.responseVoid(call);
    }
}
