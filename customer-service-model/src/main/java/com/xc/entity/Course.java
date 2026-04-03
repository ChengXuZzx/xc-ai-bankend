package com.xc.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("course")
public class Course extends BaseEntity {

    private String name;

    private Integer edu;

    private String type;

    private Long price;

    private Integer duration;
}
