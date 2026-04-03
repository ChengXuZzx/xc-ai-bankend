
package com.xc.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("school")
public class School extends BaseEntity {

    private String name;

    private String city;
}