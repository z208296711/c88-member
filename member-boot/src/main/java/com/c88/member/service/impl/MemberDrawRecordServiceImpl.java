package com.c88.member.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.c88.common.core.util.DateUtil;
import com.c88.member.mapper.MemberDrawRecordMapper;
import com.c88.member.pojo.entity.MemberDrawRecord;
import com.c88.member.pojo.vo.DrawStatisticVO;
import com.c88.member.service.IMemberDrawRecordService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
@Service
public class MemberDrawRecordServiceImpl extends ServiceImpl<MemberDrawRecordMapper, MemberDrawRecord>
    implements IMemberDrawRecordService {

    @Override
    public List<DrawStatisticVO> getDrawResultByTemplateStatistics(long templateId) {
        return baseMapper.getDrawResultByTemplateStatistics(templateId);
    }

    @Override
    public List<DrawStatisticVO> getDrawResultTodayByTemplateStatistics(long templateId) {
        LocalDateTime[] dateTimes = DateUtil.getTodayFloorAndCelling(new Date());
        Map<String, Object> map = new HashMap<>();
        map.put("templateId", templateId);
        map.put("startDate", dateTimes[0]);
        map.put("endDate", dateTimes[1]);
        return baseMapper.getDrawResultTodayByTemplateStatistics(map);
    }
}




