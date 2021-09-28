package com.practice.gift.interfaces.api;

import com.practice.gift.application.GiftFacade;
import com.practice.gift.common.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/gifts")
public class GiftApiController {

    private final GiftFacade giftFacade;
    private final GiftDtoMapper giftDtoMapper;

    /**
     * 선물하기 주문 조회
     *
     * @param giftToken
     * @return
     */
    @GetMapping("{giftToken}")
    public CommonResponse retrieveOrder(@PathVariable String giftToken){
        var giftInfo = giftFacade.getOrder(giftToken);
        return CommonResponse.success(giftInfo);
    }

    /**
     * 선물하기 주문 등록
     *
     * @param requset
     * @return
     */
    @PostMapping
    public CommonResponse registerOrder(@RequestBody @Valid GiftDto.RegisterReq requset){
        var command = giftDtoMapper.of(requset);
        var giftInfo = giftFacade.registerOrder(command);
        return CommonResponse.success(new GiftDto.RegisterRes(giftInfo));
    }

    /**
     * 선물하기 주문 상태 결제중 업데이트
     *
     * @param giftToken
     * @return
     */
    @PostMapping("/{giftToken}/payment-processing")
    public CommonResponse requestPaymentProcessing(@PathVariable String giftToken){
        giftFacade.requestPaymentProcessing(giftToken);
        return CommonResponse.success("OK");
    }

    /**
     * 선물하기 수락
     *
     * @param giftToken
     * @param request
     * @return
     */
    @PostMapping("/{giftToken}/accept-gift")
    public CommonResponse acceptGift(@PathVariable String giftToken, @RequestBody @Valid GiftDto.AcceptGiftReq request){
        var acceptCommand  = giftDtoMapper.of(giftToken, request);
        giftFacade.acceptGift(acceptCommand);
        return CommonResponse.success("OK");
    }
}
