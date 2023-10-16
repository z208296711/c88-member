package com.c88.member.service;

import com.c88.member.pojo.entity.MemberCoinRecord;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author mac
* @description 针对表【member_coin_record】的数据库操作Service
* @createDate 2022-10-04 17:32:56
*/
public interface IMemberCoinRecordService extends IService<MemberCoinRecord> {
    void batchSaveOrUpdateCoinRecord(List<MemberCoinRecord> list);
}
