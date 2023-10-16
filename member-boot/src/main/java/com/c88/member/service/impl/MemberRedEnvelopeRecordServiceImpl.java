package com.c88.member.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.c88.common.core.base.BaseEntity;
import com.c88.member.common.enums.RedEnvelopeRecordTimeTypeEnum;
import com.c88.member.mapper.MemberRedEnvelopeRecordMapper;
import com.c88.member.mapstruct.MemberRedEnvelopeRecordConverter;
import com.c88.member.pojo.entity.MemberRedEnvelopeRecord;
import com.c88.member.pojo.form.FindRedEnvelopeRecordForm;
import com.c88.member.pojo.vo.MemberRedEnvelopeRecordVO;
import com.c88.member.service.IMemberRedEnvelopeRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author user
 * @description 针对表【member_red_envelope_record(白菜紅包領取紀錄)】的数据库操作Service实现
 * @createDate 2022-10-03 11:18:12
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MemberRedEnvelopeRecordServiceImpl extends ServiceImpl<MemberRedEnvelopeRecordMapper, MemberRedEnvelopeRecord>
        implements IMemberRedEnvelopeRecordService {

    private final MemberRedEnvelopeRecordConverter memberRedEnvelopeRecordConverter;

    @Override
    public IPage<MemberRedEnvelopeRecordVO> findRedEnvelopeRecord(FindRedEnvelopeRecordForm form) {
        return this.lambdaQuery()
                .ge(Objects.equals(form.getTimeType(), RedEnvelopeRecordTimeTypeEnum.TRANSACTION.getCode()) && Objects.nonNull(form.getStartTime()), BaseEntity::getGmtCreate, form.getStartTime())
                .le(Objects.equals(form.getTimeType(), RedEnvelopeRecordTimeTypeEnum.TRANSACTION.getCode()) && Objects.nonNull(form.getEndTime()), BaseEntity::getGmtCreate, form.getEndTime())
                .ge(Objects.equals(form.getTimeType(), RedEnvelopeRecordTimeTypeEnum.REVIEW.getCode()) && Objects.nonNull(form.getStartTime()), BaseEntity::getGmtCreate, form.getStartTime())
                .le(Objects.equals(form.getTimeType(), RedEnvelopeRecordTimeTypeEnum.REVIEW.getCode()) && Objects.nonNull(form.getEndTime()), BaseEntity::getGmtCreate, form.getEndTime())
                .isNotNull(Objects.equals(form.getTimeType(), RedEnvelopeRecordTimeTypeEnum.REVIEW.getCode()), MemberRedEnvelopeRecord::getReviewTime)
                .eq(StringUtils.isNotBlank(form.getUsername()), MemberRedEnvelopeRecord::getUsername, form.getUsername())
                .eq(Objects.nonNull(form.getState()), MemberRedEnvelopeRecord::getState, form.getState())
                .orderByDesc(MemberRedEnvelopeRecord::getId)
                .page(new Page<>(form.getPageNum(), form.getPageSize()))
                .convert(memberRedEnvelopeRecordConverter::toVo);
    }

}




