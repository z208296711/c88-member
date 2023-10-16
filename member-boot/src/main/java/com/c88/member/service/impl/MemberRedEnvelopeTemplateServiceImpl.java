package com.c88.member.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.c88.common.core.result.ResultCode;
import com.c88.common.web.exception.BizException;
import com.c88.member.common.enums.RedEnvelopeRecordStateEnum;
import com.c88.member.common.enums.RedEnvelopeTemplateTypeEnum;
import com.c88.member.mapper.MemberRedEnvelopeTemplateMapper;
import com.c88.member.mapstruct.MemberRedEnvelopeTemplateConditionConverter;
import com.c88.member.mapstruct.MemberRedEnvelopeTemplateConverter;
import com.c88.member.pojo.entity.MemberRedEnvelopeCode;
import com.c88.member.pojo.entity.MemberRedEnvelopeRecord;
import com.c88.member.pojo.entity.MemberRedEnvelopeTemplate;
import com.c88.member.pojo.entity.MemberRedEnvelopeTemplateCondition;
import com.c88.member.pojo.form.AddMemberRedEnvelopeTemplateConditionForm;
import com.c88.member.pojo.form.AddRedEnvelopeTemplateForm;
import com.c88.member.pojo.form.FindRedEnvelopeTemplateForm;
import com.c88.member.pojo.form.ModifyRedEnvelopeTemplateForm;
import com.c88.member.pojo.vo.MemberRedEnvelopeTemplateConditionVO;
import com.c88.member.pojo.vo.MemberRedEnvelopeTemplateVO;
import com.c88.member.service.IMemberRedEnvelopeCodeService;
import com.c88.member.service.IMemberRedEnvelopeRecordService;
import com.c88.member.service.IMemberRedEnvelopeTemplateConditionService;
import com.c88.member.service.IMemberRedEnvelopeTemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author user
 * @description 针对表【member_red_envelope_template(白菜紅包模型)】的数据库操作Service实现
 * @createDate 2022-10-03 11:18:12
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MemberRedEnvelopeTemplateServiceImpl extends ServiceImpl<MemberRedEnvelopeTemplateMapper, MemberRedEnvelopeTemplate>
        implements IMemberRedEnvelopeTemplateService {

    private final MemberRedEnvelopeTemplateConverter memberRedEnvelopeTemplateConverter;

    private final IMemberRedEnvelopeTemplateConditionService iMemberRedEnvelopeTemplateConditionService;

    private final IMemberRedEnvelopeCodeService iMemberRedEnvelopeCodeService;

    private final IMemberRedEnvelopeRecordService iMemberRedEnvelopeRecordService;

    private final MemberRedEnvelopeTemplateConditionConverter memberRedEnvelopeTemplateConditionConverter;

    @Override
    public IPage<MemberRedEnvelopeTemplateVO> findRedEnvelopeTemplate(FindRedEnvelopeTemplateForm form) {
        IPage<MemberRedEnvelopeTemplateVO> memberRedEnvelopeTemplateVOIPage = this.lambdaQuery()
                .eq(StringUtils.isNotBlank(form.getName()), MemberRedEnvelopeTemplate::getName, form.getName())
                .eq(Objects.nonNull(form.getType()), MemberRedEnvelopeTemplate::getType, form.getType())
                .eq(Objects.nonNull(form.getEnable()), MemberRedEnvelopeTemplate::getEnable, form.getEnable())
                .orderByDesc(MemberRedEnvelopeTemplate::getId)
                .page(new Page<>(form.getPageNum(), form.getPageSize()))
                .convert(memberRedEnvelopeTemplateConverter::toVo);

        List<MemberRedEnvelopeTemplateCondition> templateConditions = iMemberRedEnvelopeTemplateConditionService.list();
        memberRedEnvelopeTemplateVOIPage.getRecords()
                .forEach(vo -> {
                            List<MemberRedEnvelopeTemplateConditionVO> memberRedEnvelopeTemplateConditionVOS = templateConditions.stream()
                                    .filter(filter -> Objects.equals(filter.getTemplateId(), vo.getId()))
                                    .sorted(Comparator.comparing(MemberRedEnvelopeTemplateCondition::getLevel))
                                    .map(memberRedEnvelopeTemplateConditionConverter::toVo)
                                    .collect(Collectors.toList());
                            vo.setMemberRedEnvelopeTemplateConditions(memberRedEnvelopeTemplateConditionVOS);
                        }
                );

        return memberRedEnvelopeTemplateVOIPage;
    }

    @Override
    @Transactional
    public Boolean addRedEnvelopeTemplate(AddRedEnvelopeTemplateForm form) {
        // 領取條件不得為空且不應超過十筆
        List<AddMemberRedEnvelopeTemplateConditionForm> receiveCondition = form.getAddMemberRedEnvelopeTemplateConditionForms();
        if (Objects.equals(form.getType(), RedEnvelopeTemplateTypeEnum.CHINESE_CABBAGE.getCode())) {
            if (CollectionUtils.isEmpty(receiveCondition) || receiveCondition.size() > 10) {
                throw new BizException(ResultCode.PARAM_ERROR);
            }
        }

        MemberRedEnvelopeTemplate memberRedEnvelopeTemplate = memberRedEnvelopeTemplateConverter.toEntity(form);

        boolean save = this.save(memberRedEnvelopeTemplate);

        // 總充值金額
        BigDecimal rechargeAmountTotal = receiveCondition.stream()
                .map(AddMemberRedEnvelopeTemplateConditionForm::getRechargeAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 總有效投注金額
        BigDecimal validBetAmountTotal = receiveCondition.stream()
                .map(AddMemberRedEnvelopeTemplateConditionForm::getValidBetAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<MemberRedEnvelopeTemplateCondition> conditions = receiveCondition.stream()
                .map(conditionForm -> {
                            MemberRedEnvelopeTemplateCondition memberRedEnvelopeTemplateCondition = memberRedEnvelopeTemplateConditionConverter.toEntity(conditionForm);
                            memberRedEnvelopeTemplateCondition.setTemplateId(memberRedEnvelopeTemplate.getId());
                            memberRedEnvelopeTemplateCondition.setMaxLevel(receiveCondition.size());
                            memberRedEnvelopeTemplateCondition.setRechargeAmountTotal(rechargeAmountTotal);
                            memberRedEnvelopeTemplateCondition.setValidBetAmountTotal(validBetAmountTotal);
                            return memberRedEnvelopeTemplateCondition;
                        }
                )
                .collect(Collectors.toList());

        boolean saveConditions = Boolean.TRUE;
        if (CollectionUtils.isNotEmpty(conditions)) {
            saveConditions = iMemberRedEnvelopeTemplateConditionService.saveBatch(conditions);
        }

        if (!save || !saveConditions) {
            throw new BizException(ResultCode.PARAM_ERROR);
        }

        // 如果為紅包代碼則產生對應的代碼
        if (Objects.equals(memberRedEnvelopeTemplate.getType(), RedEnvelopeTemplateTypeEnum.RED_PACKETS.getCode())) {
            List<MemberRedEnvelopeCode> datasource = new ArrayList<>();
            for (int x = 0; x < memberRedEnvelopeTemplate.getNumber(); x++) {
                datasource.add(
                        MemberRedEnvelopeCode.builder()
                                .templateId(memberRedEnvelopeTemplate.getId())
                                .code(RandomUtil.randomString(16).toUpperCase())
                                .build()
                );
            }

            if (!iMemberRedEnvelopeCodeService.saveBatch(datasource)) {
                throw new BizException(ResultCode.PARAM_ERROR);
            }
        }

        return Boolean.TRUE;
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = "memberRedEnvelopeTemplate", allEntries = true)
    public Boolean modifyRedEnvelopeTemplate(ModifyRedEnvelopeTemplateForm form) {
        return this.saveOrUpdate(memberRedEnvelopeTemplateConverter.toEntity(form));
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = "memberRedEnvelopeTemplate", allEntries = true)
    public Boolean deleteRedEnvelopeTemplate(List<Integer> ids) {
        // 檢查紅包代碼如還有待審核時不可刪除
        List<MemberRedEnvelopeRecord> records = iMemberRedEnvelopeRecordService.lambdaQuery()
                .select(MemberRedEnvelopeRecord::getId)
                .in(MemberRedEnvelopeRecord::getTemplateId, ids)
                .eq(MemberRedEnvelopeRecord::getState, RedEnvelopeRecordStateEnum.PENDING_REVIEW.getCode())
                .list();

        if (CollectionUtils.isNotEmpty(records)) {
            throw new BizException(ResultCode.NEED_REVIEW_CODE);
        }

        // 刪除白菜紅包設定
        iMemberRedEnvelopeTemplateConditionService.remove(
                Wrappers.<MemberRedEnvelopeTemplateCondition>lambdaQuery()
                        .in(MemberRedEnvelopeTemplateCondition::getTemplateId, ids)
        );

        // 刪除白菜紅包代碼
        iMemberRedEnvelopeCodeService.remove(
                Wrappers.<MemberRedEnvelopeCode>lambdaQuery()
                        .in(MemberRedEnvelopeCode::getTemplateId, ids)
        );

        return this.removeByIds(ids);
    }

    @Override
    @Cacheable(cacheNames = "memberRedEnvelopeTemplate", key = "#templateId")
    public MemberRedEnvelopeTemplate findById(Long templateId) {
        return Optional.ofNullable(this.getById(templateId)).orElseThrow(() -> new BizException(ResultCode.PARAM_ERROR));
    }
}




