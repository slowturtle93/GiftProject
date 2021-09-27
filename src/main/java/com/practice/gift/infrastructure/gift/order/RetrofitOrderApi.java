package com.practice.gift.infrastructure.gift.order;

import com.practice.gift.common.response.CommonResponse;
import com.practice.gift.doamin.gift.GiftCommand;
import com.practice.gift.doamin.gift.order.OrderApiCommand;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface RetrofitOrderApi {

    // 선물하기 주문 등록
    @POST("api/v1/gift-orders/init")
    Call<CommonResponse<RetrofitOrderApiResponse.Register>> registerOrder(@Body OrderApiCommand.Register request);

    // 선물하기 주문 상태 업데이트
    @POST("api/v1/gift-orders/{orderToken}/update-receiver-info")
    Call<Void> updateReceiverInfo(@Path("orderToken") String orderToken, @Body GiftCommand.Accept request);
}
