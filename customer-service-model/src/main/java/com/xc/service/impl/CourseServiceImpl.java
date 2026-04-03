package com.xc.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xc.entity.Course;
import com.xc.mapper.CourseMapper;
import com.xc.service.CourseService;
import org.springframework.stereotype.Service;

@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements CourseService {
}
