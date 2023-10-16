package com.c88.member.mapper;

import com.c88.member.pojo.entity.MemberCoinRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author mac
* @description 针对表【member_coin_record】的数据库操作Mapper
* @createDate 2022-10-04 17:32:56
* @Entity com.c88.member.pojo.entity.MemberCoinRecord
*/
public interface MemberCoinRecordMapper extends BaseMapper<MemberCoinRecord> {
    void batchSaveOrUpdateCoinRecord(List<MemberCoinRecord> list);
}




