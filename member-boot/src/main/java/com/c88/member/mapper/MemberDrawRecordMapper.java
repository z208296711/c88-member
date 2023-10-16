package com.c88.member.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.c88.member.pojo.entity.MemberDrawRecord;
import com.c88.member.pojo.vo.DrawStatisticVO;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * @Entity com.c88.member.pojo.entity.MemberDrawRecord
 */
public interface MemberDrawRecordMapper extends BaseMapper<MemberDrawRecord> {

//    @Select("SELECT award_id as awardId,count(*) as total from member_draw_record where template_id=#{templateId} GROUP BY award_id")
    @Select("SELECT award_id ,count(*) as total from member_draw_record where template_id=#{templateId} GROUP BY award_id")
    List<DrawStatisticVO> getDrawResultByTemplateStatistics(long templateId);

//    @Select("SELECT award_id as awardId,count(*) as total from member_draw_record where template_id=#{templateId} " +
//            "and gmt_create BETWEEN #{startDate} and #{endDate} " +
//            "GROUP BY award_id")
    @Select("SELECT award_id as awardId,count(*) as total from member_draw_record where template_id=#{templateId}" +
            "            and gmt_create BETWEEN #{startDate} and #{endDate}" +
            "            GROUP BY award_id")
    List<DrawStatisticVO> getDrawResultTodayByTemplateStatistics(Map<String, Object> map);
}




