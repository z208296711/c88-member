package com.c88.member.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.c88.member.pojo.entity.MemberCoin;
import com.c88.member.pojo.entity.MemberCoinRecord;
import com.c88.member.service.IMemberCoinRecordService;
import com.c88.member.mapper.MemberCoinRecordMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author mac
* @description 针对表【member_coin_record】的数据库操作Service实现
* @createDate 2022-10-04 17:32:56
*/
@Service
@AllArgsConstructor
public class MemberCoinRecordServiceImpl extends ServiceImpl<MemberCoinRecordMapper, MemberCoinRecord>
    implements IMemberCoinRecordService {

    private final MemberCoinRecordMapper memberCoinRecordMapper;

    @Override
    public void batchSaveOrUpdateCoinRecord(List<MemberCoinRecord> list) {
        memberCoinRecordMapper.batchSaveOrUpdateCoinRecord(list);
    }
}




