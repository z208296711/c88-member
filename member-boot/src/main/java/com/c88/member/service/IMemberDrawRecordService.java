package com.c88.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.c88.member.pojo.entity.MemberDrawRecord;
import com.c88.member.pojo.vo.DrawStatisticVO;

import java.util.List;

/**
 *
 */
public interface IMemberDrawRecordService extends IService<MemberDrawRecord> {
    List<DrawStatisticVO> getDrawResultByTemplateStatistics(long templateId);

    List<DrawStatisticVO> getDrawResultTodayByTemplateStatistics(long templateId);
}
