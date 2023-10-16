package com.c88.member.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.c88.member.pojo.entity.Tag;
import com.c88.member.service.ITagService;
import com.c88.member.mapper.TagMapper;
import org.springframework.stereotype.Service;

/**
* @author user
* @description 针对表【tag】的数据库操作Service实现
* @createDate 2022-11-28 16:32:47
*/
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag>
    implements ITagService {

}




