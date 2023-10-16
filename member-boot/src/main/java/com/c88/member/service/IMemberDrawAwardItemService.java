package com.c88.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.c88.member.pojo.entity.MemberDrawAwardItem;
import com.c88.member.pojo.vo.H5DrawItemVO;

import java.util.List;

/**
 *
 */
public interface IMemberDrawAwardItemService extends IService<MemberDrawAwardItem> {

    List<H5DrawItemVO> findEnableDrawItem(Integer templateId);

    List<MemberDrawAwardItem> getItemsByIds(List<Long> ids);

}
