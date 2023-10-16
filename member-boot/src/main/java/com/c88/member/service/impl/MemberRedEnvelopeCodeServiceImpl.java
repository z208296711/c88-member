package com.c88.member.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.c88.member.mapper.MemberRedEnvelopeCodeMapper;
import com.c88.member.pojo.entity.MemberRedEnvelopeCode;
import com.c88.member.service.IMemberRedEnvelopeCodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author user
 * @description 针对表【member_red_envelope_code(白菜紅包代碼)】的数据库操作Service实现
 * @createDate 2022-10-03 11:18:12
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MemberRedEnvelopeCodeServiceImpl extends ServiceImpl<MemberRedEnvelopeCodeMapper, MemberRedEnvelopeCode>
        implements IMemberRedEnvelopeCodeService {

}




