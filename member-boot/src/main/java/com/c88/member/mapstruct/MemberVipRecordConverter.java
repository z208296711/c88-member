package com.c88.member.mapstruct;

import com.c88.common.core.base.BaseConverter;
import com.c88.member.pojo.entity.MemberVipLevelRecord;
import com.c88.member.pojo.vo.MemberDetailVipRecordVO;
import com.c88.member.pojo.vo.MemberVipRecordVO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.Map;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MemberVipRecordConverter extends BaseConverter<MemberVipLevelRecord, MemberVipRecordVO> {

    MemberDetailVipRecordVO toMemberDetailVO(MemberVipLevelRecord memberVipLevelRecord);

    default MemberVipRecordVO toVo(MemberVipLevelRecord record, Map<Integer, String> memberVipConfigMap) {
        String originConfigName = memberVipConfigMap.getOrDefault(record.getOriginVipId(), "");
        String targetConfigName = memberVipConfigMap.getOrDefault(record.getTargetVipId(), "");
        MemberVipRecordVO recordVO = this.toVo(record);
        recordVO.setTargetLevel(targetConfigName);
        recordVO.setOriginLevel(originConfigName);
        return recordVO;
    }
}
