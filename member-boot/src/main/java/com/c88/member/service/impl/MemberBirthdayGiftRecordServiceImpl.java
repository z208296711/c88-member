package com.c88.member.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.c88.member.mapper.MemberBirthdayGiftRecordMapper;
import com.c88.member.mapstruct.MemberBirthdayGiftRecordConverter;
import com.c88.member.pojo.entity.MemberBirthdayGiftRecord;
import com.c88.member.pojo.form.FindMemberBirthdayGiftRecordForm;
import com.c88.member.pojo.vo.MemberBirthdayGiftRecordVO;
import com.c88.member.service.IMemberBirthdayGiftRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author user
 * @description 针对表【member_birthday_gift_record】的数据库操作Service实现
 * @createDate 2022-09-12 14:52:17
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MemberBirthdayGiftRecordServiceImpl extends ServiceImpl<MemberBirthdayGiftRecordMapper, MemberBirthdayGiftRecord>
        implements IMemberBirthdayGiftRecordService {

    private final MemberBirthdayGiftRecordConverter memberBirthdayGiftRecordConverter;

    @Override
    public IPage<MemberBirthdayGiftRecordVO> findMemberBirthdayGiftRecord(FindMemberBirthdayGiftRecordForm form) {
        return this.lambdaQuery()
                .like(StringUtils.isNotBlank(form.getUsername()), MemberBirthdayGiftRecord::getUsername, form.getUsername())
                .ge(Objects.nonNull(form.getStartTime()), MemberBirthdayGiftRecord::getGmtCreate, form.getStartTime())
                .le(Objects.nonNull(form.getEndTime()), MemberBirthdayGiftRecord::getGmtCreate, form.getEndTime())
                .orderByDesc(MemberBirthdayGiftRecord::getGmtCreate)
                .page(new Page<>(form.getPageNum(), form.getPageSize()))
                .convert(memberBirthdayGiftRecordConverter::toVo);
    }
}




