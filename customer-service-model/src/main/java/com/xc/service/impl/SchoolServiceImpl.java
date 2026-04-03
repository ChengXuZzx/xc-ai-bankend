package com.xc.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xc.entity.School;
import com.xc.mapper.SchoolMapper;
import com.xc.service.SchoolService;
import org.springframework.stereotype.Service;

@Service
public class SchoolServiceImpl extends ServiceImpl<SchoolMapper, School> implements SchoolService {
}
