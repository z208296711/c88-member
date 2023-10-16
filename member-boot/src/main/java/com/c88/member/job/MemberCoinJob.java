package com.c88.member.job;

import com.c88.common.core.result.Result;
import com.c88.common.core.vo.BetRecordVo;
import com.c88.feign.RiskFeignClient;
import com.c88.member.pojo.entity.MemberCoin;
import com.c88.member.pojo.entity.MemberCoinRecord;
import com.c88.member.service.impl.MemberCoinRecordServiceImpl;
import com.c88.member.service.impl.MemberCoinServiceImpl;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class MemberCoinJob {
    private final InteractiveQueryService interactiveQueryService;

    private final RiskFeignClient riskFeignClient;

    public static final BigDecimal CoinRate = new BigDecimal(250);

    public final MemberCoinServiceImpl memberCoinService;

    public final MemberCoinRecordServiceImpl memberCoinRecordService;


    @XxlJob("calculateCoin")
    public void calculateCoin(){
        log.info("memberCoin calculate start time : {}", LocalDateTime.now().toLocalTime());
        Result<Map<String, BetRecordVo>> mapResult = riskFeignClient.yesterdayBetRecord();
        Map<String, BetRecordVo> data = mapResult.getData();
        List<MemberCoin> list = new ArrayList<>();
        List<MemberCoinRecord> records = new ArrayList<>();
        for(Map.Entry<String, BetRecordVo> entry : data.entrySet()){
            String username = entry.getKey();
            BetRecordVo betRecordVo = entry.getValue();
            int coin = betRecordVo.getValidBetAmount().divide(CoinRate).intValue();

            MemberCoin memberCoin = Optional.ofNullable(memberCoinService.getMemberByUserName(username))
                    .orElse(MemberCoin.builder()
                            .username(username)
                            .coin(0)
                            .build());
            // 如果同一天已算過，不再計算一次
            //TODO unmark below after QA test
//            if(LocalDate.now().isEqual(memberCoin.getGmtModified().toLocalDate())){
//                continue;
//            }
            MemberCoinRecord record = MemberCoinRecord.builder()
                    .beforeCoin(memberCoin.getGmtCreate()==null ? 0 : memberCoin.getCoin())
                    .coin(coin)
                    .afterCoin(memberCoin.getGmtCreate()==null ? coin : (memberCoin.getCoin()+coin))
                    .username(username).build();

            memberCoin.setCoin(memberCoin.getCoin() + coin);

            list.add(memberCoin);
            records.add(record);
        }
        if(list.size()>0) {
            memberCoinService.batchSaveOrUpdateCoin(list);
            memberCoinRecordService.batchSaveOrUpdateCoinRecord(records);
        }
    }
}
