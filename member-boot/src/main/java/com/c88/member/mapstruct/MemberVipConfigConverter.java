package com.c88.member.mapstruct;

import com.c88.common.core.base.BaseConverter;
import com.c88.member.common.enums.LevelUpModeEnum;
import com.c88.member.common.enums.LevelUpReviewEnum;
import com.c88.member.dto.MemberVipConfigDTO;
import com.c88.member.pojo.entity.MemberVipConfig;
import com.c88.member.pojo.form.AddMemberVipForm;
import com.c88.member.pojo.form.ModifyMemberVipForm;
import com.c88.member.pojo.vo.H5LevelUpGiftVO;
import com.c88.member.pojo.vo.MemberVipConfigVO;
import com.c88.member.pojo.vo.VipDetailVO;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MemberVipConfigConverter extends BaseConverter<MemberVipConfig, MemberVipConfigVO> {

    MemberVipConfigDTO toMemberVipDTO(MemberVipConfig memberVipConfig);

    VipDetailVO toVipDetailVO(MemberVipConfig memberVipConfig);

    List<VipDetailVO> toVipDetailVO(List<MemberVipConfig> memberVipConfig);

    MemberVipConfig toEntity(AddMemberVipForm form);

    MemberVipConfig toEntity(ModifyMemberVipForm form);

    H5LevelUpGiftVO toH5LevelUpGiftVO(MemberVipConfig memberVipConfig);

    List<H5LevelUpGiftVO> toH5LevelUpGiftVO(List<MemberVipConfig> memberVipConfigList);

    default LevelUpModeEnum customLevelTypeConverter(Integer levelUpMode) {
        if (levelUpMode == null) {
            return null;
        }
        return LevelUpModeEnum.fromIntValue(levelUpMode);
    }

    default LevelUpReviewEnum customReviewConverter(Integer levelUpReview) {
        if (levelUpReview == null) {
            return null;
        }
        return LevelUpReviewEnum.fromIntValue(levelUpReview);
    }

    default Integer customLevelTypeConverter(LevelUpModeEnum levelUpMode) {
        if (levelUpMode == null) {
            return null;
        }
        return levelUpMode.getValue();
    }

    default Integer customReviewConverter(LevelUpReviewEnum levelUpReview) {
        if (levelUpReview == null) {
            return null;
        }
        return levelUpReview.getValue();
    }

    @AfterMapping
    default void customMapping(@MappingTarget MemberVipConfigVO vipConfigVO, MemberVipConfig source) {
        // any custom logic
        if (source.getLevelUpMode().equals(LevelUpModeEnum.MANUAL)) {
            vipConfigVO.setLevelUpCondition(source.getLevelUpNote());
            vipConfigVO.setKeepCondition(source.getKeepLevelNote());
        } else {
            vipConfigVO.setLevelUpCondition(source.getLevelUpRecharge() + "/" + source.getLevelUpExp());
            vipConfigVO.setKeepCondition(source.getKeepRecharge() + "/" + source.getKeepExp());
        }
        vipConfigVO.setLevelUpMode(source.getLevelUpMode().getValue());
    }
}
