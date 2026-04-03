package com.xc.tools;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.xc.entity.Course;
import com.xc.entity.CourseReservation;
import com.xc.entity.School;
import com.xc.entity.query.CourseQuery;
import com.xc.service.CourseReservationService;
import com.xc.service.CourseService;
import com.xc.service.SchoolService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CourseTools {

    private final CourseService courseService;

    private final SchoolService schoolService;

    private final CourseReservationService courseReservationService;

    @Tool(description = "根据条件查询课程")
    public List<Course> queryCourseList(@ToolParam(description = "查询条件") CourseQuery query) {
        if (query == null) {
            return courseService.list();
        }
        QueryChainWrapper<Course> wrapper = courseService.query()
                .eq(query.getType() != null, "type", query.getType()) // type = '编程'
                .le(query.getEdu() != null, "edu", query.getEdu()); // edu <= 2

        if (query.getSorts() != null && !query.getSorts().isEmpty()) {
            for (CourseQuery.Sort sort : query.getSorts()) {
                wrapper.orderBy(true, sort.getAsc(), sort.getField());
            }
        }
        return wrapper.list();
    }

    @Tool(description = "查询所有学校")
    public List<School> querySchoolList() {
        return schoolService.list();
    }

    @Tool(description = "生成预约单，返回预约单号")
    public Integer createCourse(@ToolParam(description = "课程信息") String course,
                                @ToolParam(description = "预约校区") String school,
                                @ToolParam(description = "预约人姓名") String studentName,
                                @ToolParam(description = "预约人联系方式") String contactInfo,
                                @ToolParam(description = "预约人备注") String remark) {
        CourseReservation courseReservation = new CourseReservation();
        courseReservation.setCourse(course);
        courseReservation.setSchool(school);
        courseReservation.setStudentName(studentName);
        courseReservation.setContactInfo(contactInfo);
        courseReservation.setRemark(remark);
        courseReservationService.save(courseReservation);
        return courseReservation.getId();
    }

}
