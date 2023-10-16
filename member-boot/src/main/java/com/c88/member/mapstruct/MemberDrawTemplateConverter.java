package com.c88.member.mapstruct;

import com.c88.common.core.base.BaseConverter;
import com.c88.member.pojo.entity.MemberDrawTemplate;
import com.c88.member.pojo.vo.AdminMemberDrawTemplateConditionVO;
import com.c88.member.pojo.vo.AdminMemberDrawTemplateVO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MemberDrawTemplateConverter extends BaseConverter<MemberDrawTemplate, AdminMemberDrawTemplateVO> {

    default AdminMemberDrawTemplateVO toVO(MemberDrawTemplate template,
                                           Map<Integer, List<AdminMemberDrawTemplateConditionVO>> map,
                                           Map<Integer, Integer> templateEnableDrawItemMap ) {
        AdminMemberDrawTemplateVO vo = this.toVo(template);
        vo.setConditions(map.getOrDefault(template.getId(), Collections.emptyList())
                .stream()
                .sorted(Comparator.comparingInt(AdminMemberDrawTemplateConditionVO::getLevel))
                .collect(Collectors.toList()));
        vo.setAwardCount(templateEnableDrawItemMap.getOrDefault(template.getId(),1));
        return vo;
    }

}
