package com.practice.gift.common.exception;

import com.practice.gift.common.response.ErrorCode;

public class EntityNotFoundException extends BaseException{

    public EntityNotFoundException(){
        super(ErrorCode.COMMON_INVALID_PARAMETER);
    }

    public EntityNotFoundException(String messge){
        super(messge, ErrorCode.COMMON_INVALID_PARAMETER);
    }
}
