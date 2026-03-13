package com.pxwork.api.controller.frontend;

import com.pxwork.common.utils.Result;
import com.pxwork.course.entity.Course;
import com.pxwork.course.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 前台课程展示 前端控制器
 * </p>
 *
 * @author TraeAI
 * @since 2026-03-13
 */
@Tag(name = "前台课程展示", description = "前台学员可见的课程接口")
@RestController
@RequestMapping("/frontend/course")
public class FrontendCourseController {

    @Autowired
    private CourseService courseService;

    @Operation(summary = "获取已发布课程列表", description = "获取所有已发布且对学员可见的课程列表")
    @GetMapping("/list")
    public Result<List<Course>> list() {
        return Result.success(courseService.getPublishedCourses());
    }
    
    @Operation(summary = "获取课程详情")
    @GetMapping("/detail/{id}")
    public Result<Course> detail(@PathVariable Long id) {
        Course course = courseService.getCourseDetails(id);
        if (course == null) {
            return Result.fail("课程不存在");
        }
        // 前台也可以加一层校验，如果课程未发布，不允许查看详情
        if (course.getStatus() != null && course.getStatus() == 0) {
             return Result.fail("课程未发布或已下架");
        }
        return Result.success(course);
    }
}
