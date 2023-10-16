package com.c88.member.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.c88.member.pojo.entity.MemberDrawPool;
import com.c88.member.pojo.vo.EnableDrawItemCountVO;
import com.c88.member.pojo.vo.MInMaxVO;
import com.c88.member.pojo.vo.MemberDrawVO;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Entity com.c88.member.pojo.entity.MemberDrawPool
 */
public interface MemberDrawPoolMapper extends BaseMapper<MemberDrawPool> {

    @Select("SELECT " +
            "p.id,p.template_id, p.draw_type, p.award_id,p.prize_count, p.prize_today, p.percent, p.sort, p.enable, " +
            "item.name as item_name, " +
            "item.`enable` as item_enable,  " +
            "item.award_type as awardType," +
            "item.daily_number," +
            "item.total_number " +
            "FROM " +
            "member_draw_pool p " +
            "JOIN member_draw_award_item item ON p.award_id = item.id and item.enable=1 " +
            "WHERE " +
            "p.template_id =#{templateId}")
    List<MemberDrawVO> getDrawpoolByTemplateId(long templateId);

    void batchSaveOrUpdateDrawpool(List<MemberDrawPool> list);

    @Select("SELECT IFNULL(min(sort),0) as min , IFNULL(max(sort),0) as max from member_draw_pool where template_id=(select template_id from member_draw_pool where id=#{id})")
    List<MInMaxVO> getMinMaxSort(long id);


    List<EnableDrawItemCountVO> findEnableDrawItemByTemplateId(List<Integer> templateIdList);

}




