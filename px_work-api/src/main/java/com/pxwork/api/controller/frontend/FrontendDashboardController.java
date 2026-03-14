package com.pxwork.api.controller.frontend;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pxwork.common.entity.User;
import com.pxwork.common.service.UserService;
import com.pxwork.common.utils.Result;
import com.pxwork.common.utils.StpUserUtil;
import com.pxwork.course.entity.Course;
import com.pxwork.course.entity.UserCourseEnrollment;
import com.pxwork.course.service.CourseService;
import com.pxwork.course.service.UserCourseEnrollmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Tag(name = "前台学习看板")
@RestController
@RequestMapping("/frontend/dashboard")
public class FrontendDashboardController {

    @Autowired
    private UserService userService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private UserCourseEnrollmentService userCourseEnrollmentService;

    @Operation(summary = "学习进度统计")
    @GetMapping("/progress-stats")
    public Result<Map<String, Object>> progressStats() {
        long userId = StpUserUtil.getLoginIdAsLong();
        User user = userService.getById(userId);
        if (user == null) {
            return Result.fail("用户不存在");
        }
        String jobRole = user.getJobRole();
        BigDecimal targetHours = BigDecimal.ZERO;
        if (StringUtils.hasText(jobRole)) {
            List<Course> targetCourses = courseService.list(new LambdaQueryWrapper<Course>()
                    .like(Course::getTargetRoles, jobRole));
            targetHours = sumCreditHours(targetCourses);
        }
        List<UserCourseEnrollment> finishedEnrollments = userCourseEnrollmentService.list(
                new LambdaQueryWrapper<UserCourseEnrollment>()
                        .eq(UserCourseEnrollment::getUserId, userId)
                        .eq(UserCourseEnrollment::getStatus, 1));
        BigDecimal finishedHours = BigDecimal.ZERO;
        if (!finishedEnrollments.isEmpty()) {
            Set<Long> finishedCourseIds = finishedEnrollments.stream()
                    .map(UserCourseEnrollment::getCourseId)
                    .collect(Collectors.toSet());
            if (!finishedCourseIds.isEmpty()) {
                List<Course> finishedCourses = courseService.list(
                        new LambdaQueryWrapper<Course>().in(Course::getId, finishedCourseIds));
                finishedHours = sumCreditHours(finishedCourses);
            }
        }
        BigDecimal percentageValue = BigDecimal.ZERO;
        if (targetHours.compareTo(BigDecimal.ZERO) > 0) {
            percentageValue = finishedHours
                    .multiply(BigDecimal.valueOf(100))
                    .divide(targetHours, 2, RoundingMode.HALF_UP);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("targetHours", targetHours);
        result.put("finishedHours", finishedHours);
        result.put("percentage", percentageValue.stripTrailingZeros().toPlainString() + "%");
        return Result.success(result);
    }

    private BigDecimal sumCreditHours(List<Course> courses) {
        BigDecimal sum = BigDecimal.ZERO;
        for (Course course : courses) {
            if (course.getCreditHours() != null) {
                sum = sum.add(course.getCreditHours());
            }
        }
        return sum;
    }
}
