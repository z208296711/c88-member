package com.c88.member.consumer;

import com.alibaba.fastjson.JSON;
import com.c88.common.core.enums.BalanceChangeTypeLinkEnum;
import com.c88.member.pojo.entity.Member;
import com.c88.member.pojo.entity.MemberLiquidity;
import com.c88.member.service.IMemberLiquidityService;
import com.c88.member.service.IMemberService;
import com.c88.payment.dto.AddBalanceDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static com.c88.common.core.constant.TopicConstants.BALANCE_CHANGE;
import static com.c88.common.core.constant.TopicConstants.MEMBER_LIQUIDITY;

@Slf4j
@Configuration
@AllArgsConstructor
public class LiquidityChangeConsumer {

    private final IMemberService memberService;

    private final IMemberLiquidityService memberLiquidityService;

    @KafkaListener(id = MEMBER_LIQUIDITY, topics = BALANCE_CHANGE)
    @Transactional
    public void listenLiquidityChange(AddBalanceDTO addBalanceDTO, Acknowledgment acknowledgement) {
        log.info("listenBalanceChange Consumer: {}", JSON.toJSONString(addBalanceDTO));
        try {
            MemberLiquidity liquidity = memberLiquidityService.lambdaQuery()
                    .eq(MemberLiquidity::getMemberId, addBalanceDTO.getMemberId())
                    .oneOpt()
                    .orElseGet(() -> {
                                Member member = memberService.getById(addBalanceDTO.getMemberId());
                                MemberLiquidity memberLiquidity = MemberLiquidity.builder()
                                        .memberId(addBalanceDTO.getMemberId())
                                        .username(member.getUserName())
                                        .rechargeAmount(BigDecimal.ZERO)
                                        .rechargeCount(0)
                                        .bonusAmount(BigDecimal.ZERO)
                                        .bonusCount(0)
                                        .promotionAmount(BigDecimal.ZERO)
                                        .promotionCount(0)
                                        .withdrawAmount(BigDecimal.ZERO)
                                        .withdrawCount(0)
                                        .build();

                                memberLiquidityService.save(memberLiquidity);

                                return memberLiquidity;

                            }
                    );

            switch (BalanceChangeTypeLinkEnum.getEnum(addBalanceDTO.getType())) {
                case RECHARGE:
                    liquidity.setRechargeAmount(addBalanceDTO.getBalance().add(liquidity.getRechargeAmount()));
                    liquidity.setRechargeCount(liquidity.getRechargeCount() + 1);
                    break;
                case BONUS:
                    liquidity.setBonusAmount(addBalanceDTO.getBalance().add(liquidity.getBonusAmount()));
                    liquidity.setBonusCount(liquidity.getBonusCount() + 1);
                    break;
                case RECHARGE_PROMOTION:
                    liquidity.setPromotionAmount(addBalanceDTO.getBalance().add(liquidity.getPromotionAmount()));
                    liquidity.setPromotionCount(liquidity.getPromotionCount() + 1);
                    break;
                case WITHDRAW:
                    liquidity.setWithdrawAmount(addBalanceDTO.getBalance().add(liquidity.getWithdrawAmount()));
                    liquidity.setWithdrawCount(liquidity.getWithdrawCount() + 1);
                    break;
            }

            memberLiquidityService.updateById(liquidity);
        } catch (Exception e) {
            log.error("listenBalanceChange dto : {}", JSON.toJSONString(addBalanceDTO), e);
        } finally {
            acknowledgement.acknowledge();
        }
    }
}
