package com.pxwork.api.controller.frontend;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pxwork.common.utils.StpUserUtil;
import com.pxwork.common.utils.Result;
import com.pxwork.course.entity.Course;
import com.pxwork.course.entity.UserCourseEnrollment;
import com.pxwork.course.service.CourseService;
import com.pxwork.course.service.UserCourseEnrollmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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

    @Autowired
    private UserCourseEnrollmentService userCourseEnrollmentService;

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
        if (course.getStatus() != null && course.getStatus() == 0) {
            return Result.fail("课程未发布或已下架");
        }
        return Result.success(course);
    }

    @Operation(summary = "选课")
    @PostMapping("/enroll/{courseId}")
    public Result<Boolean> enroll(@PathVariable Long courseId) {
        long userId = StpUserUtil.getLoginIdAsLong();
        Course course = courseService.getById(courseId);
        if (course == null) {
            return Result.fail("课程不存在");
        }
        long exists = userCourseEnrollmentService.count(new LambdaQueryWrapper<UserCourseEnrollment>()
                .eq(UserCourseEnrollment::getUserId, userId)
                .eq(UserCourseEnrollment::getCourseId, courseId));
        if (exists > 0) {
            return Result.fail("已选过该课程");
        }
        UserCourseEnrollment enrollment = new UserCourseEnrollment();
        enrollment.setUserId(userId);
        enrollment.setCourseId(courseId);
        enrollment.setStatus(0);
        return Result.success(userCourseEnrollmentService.save(enrollment));
    }

    @Operation(summary = "我的课程")
    @GetMapping("/my-courses")
    public Result<List<MyCourseVO>> myCourses() {
        long userId = StpUserUtil.getLoginIdAsLong();
        List<UserCourseEnrollment> enrollments = userCourseEnrollmentService.list(
                new LambdaQueryWrapper<UserCourseEnrollment>()
                        .eq(UserCourseEnrollment::getUserId, userId)
                        .orderByDesc(UserCourseEnrollment::getCreatedAt));
        if (enrollments.isEmpty()) {
            return Result.success(List.of());
        }
        Set<Long> courseIds = enrollments.stream().map(UserCourseEnrollment::getCourseId).collect(Collectors.toSet());
        List<Course> courses = courseService.list(new LambdaQueryWrapper<Course>().in(Course::getId, courseIds));
        Map<Long, Course> courseMap = new HashMap<>();
        for (Course course : courses) {
            courseMap.put(course.getId(), course);
        }
        List<MyCourseVO> result = enrollments.stream()
                .map(enrollment -> {
                    Course course = courseMap.get(enrollment.getCourseId());
                    if (course == null) {
                        return null;
                    }
                    MyCourseVO vo = new MyCourseVO();
                    vo.setCourseId(course.getId());
                    vo.setName(course.getName());
                    vo.setTitle(course.getTitle());
                    vo.setThumb(course.getThumb());
                    vo.setShortDesc(course.getShortDesc());
                    vo.setCreditHours(course.getCreditHours());
                    vo.setCourseStatus(course.getStatus());
                    vo.setLearningStatus(enrollment.getStatus());
                    return vo;
                })
                .filter(item -> item != null)
                .collect(Collectors.toList());
        return Result.success(result);
    }

    @Data
    public static class MyCourseVO {
        private Long courseId;
        private String name;
        private String title;
        private String thumb;
        private String shortDesc;
        private java.math.BigDecimal creditHours;
        private Integer courseStatus;
        private Integer learningStatus;
    }
}
