package com.c88.member.mapstruct;

import com.c88.common.core.base.BaseConverter;
import com.c88.member.pojo.entity.MemberRedEnvelopeCode;
import com.c88.member.pojo.vo.MemberRedEnvelopeCodeVO;
import com.c88.member.pojo.vo.MemberRedEnvelopeTemplateCodeVO;
import org.mapstruct.BeforeMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.Objects;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MemberRedEnvelopeCodeConverter extends BaseConverter<MemberRedEnvelopeCode, MemberRedEnvelopeCodeVO> {

    // 指定目標
    // void toTemplateVo(List<MemberRedEnvelopeCode> entity, @MappingTarget List<MemberRedEnvelopeTemplateCodeVO> vo);

    @BeforeMapping
    default void before(MemberRedEnvelopeCode entity) {
        if (Objects.nonNull(entity) && entity.getState() != 1) {
            entity.setGmtModified(null);
        }
    }

    @Mapping(source = "gmtModified", target = "receiveTime")
    MemberRedEnvelopeTemplateCodeVO toTemplateVo(MemberRedEnvelopeCode entity);

}
