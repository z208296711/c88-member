package com.c88.member.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.c88.member.pojo.entity.MemberDrawAwardItem;
import com.c88.member.pojo.vo.H5DrawItemVO;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Entity com.c88.member.pojo.entity.MemberDrawAwardItem
 */
public interface MemberDrawAwardItemMapper extends BaseMapper<MemberDrawAwardItem> {

    @Select(" select mdai.id, " +
            "       mdai.image, " +
            "       mdai.note, " +
            "       mdai.award_type awardType," +
            "       mdai.name," +
            "       mdai.amount" +
            " from member_draw_pool mdp left join member_draw_award_item mdai on mdp.award_id =  mdai.id" +
            " where template_id = #{templateId}" +
            "  and mdp.enable = 1 and mdai.enable = 1")
    List<H5DrawItemVO> findEnableDrawItemsByTemplateId(Integer templateId);

}




