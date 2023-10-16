package com.c88.member.service;

import com.c88.member.pojo.entity.MemberDrawAwardItem;
import com.c88.member.pojo.entity.MemberDrawPool;
import com.baomidou.mybatisplus.extension.service.IService;
import com.c88.member.pojo.form.ModifyDrawpoolSortTopBottomForm;
import com.c88.member.pojo.vo.EnableDrawItemCountVO;
import com.c88.member.pojo.vo.MemberDrawVO;

import java.util.List;

/**
 *
 */
public interface IMemberDrawPoolService extends IService<MemberDrawPool> {
    List<MemberDrawVO> getDrawPoolsByTemplateId(long templateId);

    boolean update(Long templateId, List<MemberDrawPool> pools);

    boolean insert(List<MemberDrawPool> pools);

    List<MemberDrawVO> list(Long templateId);

    Boolean modifyDrawpoolSortTopBottom(ModifyDrawpoolSortTopBottomForm form);

    List<EnableDrawItemCountVO> findEnableDrawItemByTemplateId(List<Integer> templateIdList);

    boolean checkDrawItemStatus(List<MemberDrawAwardItem> items);
}
