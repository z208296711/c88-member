package com.c88.member.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.c88.member.mapper.MemberDrawAwardItemMapper;
import com.c88.member.pojo.entity.MemberDrawAwardItem;
import com.c88.member.pojo.vo.H5DrawItemVO;
import com.c88.member.service.IMemberDrawAwardItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 */
@Service
@RequiredArgsConstructor
public class MemberDrawAwardItemServiceImpl extends ServiceImpl<MemberDrawAwardItemMapper, MemberDrawAwardItem> implements IMemberDrawAwardItemService {


    @Override
    @Cacheable(cacheNames = "activityDrawItem", key = "'activityDrawItem:'+#templateId")
    public List<H5DrawItemVO> findEnableDrawItem(Integer templateId) {
        return this.baseMapper.findEnableDrawItemsByTemplateId(templateId);
    }

    @Override
    public List<MemberDrawAwardItem> getItemsByIds(List<Long> ids) {
        return this.baseMapper.selectList(new LambdaQueryWrapper<MemberDrawAwardItem>().in(MemberDrawAwardItem::getId, ids));
    }


}




