package com.c88.member.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.c88.common.core.base.BaseEntity;
import com.c88.common.core.enums.RechargeAwardTypeEnum;
import com.c88.member.enums.RechargeAwardRecordStateEnum;
import com.c88.member.mapper.MemberRechargeAwardRecordMapper;
import com.c88.member.mapstruct.MemberRechargeAwardRecordConverter;
import com.c88.member.pojo.entity.MemberRechargeAwardRecord;
import com.c88.member.pojo.form.FindPersonalRechargeAwardRecordForm;
import com.c88.member.pojo.form.FindRechargeAwardRecordForm;
import com.c88.member.vo.AllMemberPersonalRechargeAwardRecordByTemplateIdVO;
import com.c88.member.pojo.vo.MemberPersonalRechargeAwardRecordVO;
import com.c88.member.pojo.vo.MemberRechargeAwardRecordVO;
import com.c88.member.service.IMemberRechargeAwardRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author user
 * @description 针对表【member_recharge_award_record(存送優惠領取紀錄)】的数据库操作Service实现
 * @createDate 2022-10-25 14:09:22
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MemberRechargeAwardRecordServiceImpl extends ServiceImpl<MemberRechargeAwardRecordMapper, MemberRechargeAwardRecord>
        implements IMemberRechargeAwardRecordService {

    private final MemberRechargeAwardRecordConverter memberRechargeAwardRecordConverter;

    @Override
    public IPage<MemberRechargeAwardRecordVO> findRechargeAwardRecord(FindRechargeAwardRecordForm form) {
        return this.lambdaQuery()
                .ge(Objects.nonNull(form.getStartTime()), BaseEntity::getGmtCreate, form.getStartTime())
                .le(Objects.nonNull(form.getEndTime()), BaseEntity::getGmtCreate, form.getEndTime())
                .eq(Objects.nonNull(form.getType()), MemberRechargeAwardRecord::getType, form.getType())
                .eq(StringUtils.isNotBlank(form.getUsername()), MemberRechargeAwardRecord::getUsername, form.getUsername())
                .like(StringUtils.isNotBlank(form.getName()), MemberRechargeAwardRecord::getName, form.getName())
                .isNotNull(MemberRechargeAwardRecord::getUseTime)
                .orderByDesc(MemberRechargeAwardRecord::getId)
                .page(new Page<>(form.getPageNum(), form.getPageSize()))
                .convert(memberRechargeAwardRecordConverter::toVO);
    }

    @Override
    public IPage<MemberPersonalRechargeAwardRecordVO> findPersonalRechargeAwardRecord(FindPersonalRechargeAwardRecordForm form) {
        return this.lambdaQuery()
                .ge(Objects.nonNull(form.getStartTime()), BaseEntity::getGmtCreate, form.getStartTime())
                .le(Objects.nonNull(form.getEndTime()), BaseEntity::getGmtCreate, form.getEndTime())
                .eq(Objects.nonNull(form.getState()), MemberRechargeAwardRecord::getState, form.getState())
                .eq(StringUtils.isNotBlank(form.getUsername()), MemberRechargeAwardRecord::getUsername, form.getUsername())
                .like(StringUtils.isNotBlank(form.getName()), MemberRechargeAwardRecord::getName, form.getName())
                .eq(MemberRechargeAwardRecord::getType, RechargeAwardTypeEnum.PERSONAL.getCode())
                .orderByDesc(MemberRechargeAwardRecord::getId)
                .page(new Page<>(form.getPageNum(), form.getPageSize()))
                .convert(memberRechargeAwardRecordConverter::toPersonalVO);
    }

    @Override
    public Boolean cancelPersonalRechargeAwardRecord(Long id) {
        return this.lambdaUpdate()
                .eq(MemberRechargeAwardRecord::getId, id)
                .set(MemberRechargeAwardRecord::getCancelTime, LocalDateTime.now())
                .set(MemberRechargeAwardRecord::getState, RechargeAwardRecordStateEnum.CANCEL.getCode())
                .update();
    }

    @Override
    public List<AllMemberPersonalRechargeAwardRecordByTemplateIdVO> findAllPersonalRechargeAwardRecordByTemplateId(Long templateId, Long memberId) {
        return this.lambdaQuery()
                .eq(MemberRechargeAwardRecord::getTemplateId, templateId)
                .eq(MemberRechargeAwardRecord::getMemberId, memberId)
                .list()
                .stream()
                .map(memberRechargeAwardRecordConverter::toAllPersonalVO)
                .collect(Collectors.toList());
    }
}




