package com.practice.gift.doamin.gift;

import com.practice.gift.doamin.gift.order.OrderApiCommand;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface GiftToOrderMapper {

    OrderApiCommand.Register of(GiftCommand.Register register);

    OrderApiCommand.RegisterOrderItem of(GiftCommand.RegisterOrderItem register);

    OrderApiCommand.RegisterOrderItemOptionGroup of(GiftCommand.RegisterOrderItemOptionGroup register);

    OrderApiCommand.RegisterOrderItemOption of(GiftCommand.RegisterOrderItemOption register);

}
