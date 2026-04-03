package com.xc.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("course_reservation")
public class CourseReservation extends BaseEntity {

    private String course;

    private String studentName;

    private String contactInfo;

    private String school;

    private String remark;
}
