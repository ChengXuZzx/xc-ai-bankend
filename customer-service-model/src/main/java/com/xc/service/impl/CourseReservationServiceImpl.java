package com.xc.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xc.entity.CourseReservation;
import com.xc.mapper.CourseReservationMapper;
import com.xc.service.CourseReservationService;
import org.springframework.stereotype.Service;

@Service
public class CourseReservationServiceImpl extends ServiceImpl<CourseReservationMapper, CourseReservation> implements CourseReservationService {
}
