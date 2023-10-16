package com.c88.member.controller.admin;

import com.c88.common.core.result.PageResult;
import com.c88.member.pojo.form.FindMemberBirthdayGiftRecordForm;
import com.c88.member.pojo.vo.MemberBirthdayGiftRecordVO;
import com.c88.member.service.IMemberBirthdayGiftRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "『後台』生日禮金")
@RestController
@RequestMapping("/api/v1/member/birthday/gift/record")
@RequiredArgsConstructor
public class MemberBirthdayGiftRecordController {

    private final IMemberBirthdayGiftRecordService iMemberBirthdayGiftRecordService;

    @Operation(summary = "找生日禮金")
    @GetMapping
    public PageResult<MemberBirthdayGiftRecordVO> findMemberBirthdayGiftRecord(@ParameterObject FindMemberBirthdayGiftRecordForm form) {
        return PageResult.success(iMemberBirthdayGiftRecordService.findMemberBirthdayGiftRecord(form));
    }

}
