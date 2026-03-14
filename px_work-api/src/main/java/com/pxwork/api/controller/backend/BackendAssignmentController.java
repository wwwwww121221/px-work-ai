package com.pxwork.api.controller.backend;

import com.pxwork.common.utils.Result;
import com.pxwork.course.entity.AssignmentSubmission;
import com.pxwork.course.entity.Course;
import com.pxwork.course.entity.CourseAssignment;
import com.pxwork.course.service.AssignmentSubmissionService;
import com.pxwork.course.service.CourseAssignmentService;
import com.pxwork.course.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "后台作业管理")
@RestController
@RequestMapping("/backend/assignment")
public class BackendAssignmentController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private CourseAssignmentService courseAssignmentService;

    @Autowired
    private AssignmentSubmissionService assignmentSubmissionService;

    @Operation(summary = "发布作业")
    @PostMapping("/publish")
    public Result<Boolean> publish(@RequestBody @Validated PublishAssignmentRequest request) {
        Course course = courseService.getById(request.getCourseId());
        if (course == null) {
            return Result.fail("课程不存在");
        }
        CourseAssignment assignment = new CourseAssignment();
        assignment.setCourseId(request.getCourseId());
        assignment.setTitle(request.getTitle());
        assignment.setContent(request.getContent());
        assignment.setAttachmentUrl(request.getAttachmentUrl());
        assignment.setDeadline(request.getDeadline());
        return Result.success(courseAssignmentService.save(assignment));
    }

    @Operation(summary = "批改作业")
    @PostMapping("/grade")
    public Result<Boolean> grade(@RequestBody @Validated GradeAssignmentRequest request) {
        AssignmentSubmission submission = assignmentSubmissionService.getById(request.getSubmissionId());
        if (submission == null) {
            return Result.fail("提交记录不存在");
        }
        submission.setScore(request.getScore());
        submission.setComment(request.getComment());
        submission.setStatus(1);
        return Result.success(assignmentSubmissionService.updateById(submission));
    }

    @Data
    public static class PublishAssignmentRequest {
        @NotNull(message = "课程ID不能为空")
        private Long courseId;
        @NotNull(message = "作业标题不能为空")
        private String title;
        private String content;
        private String attachmentUrl;
        private java.time.LocalDateTime deadline;
    }

    @Data
    public static class GradeAssignmentRequest {
        @NotNull(message = "提交ID不能为空")
        private Long submissionId;
        @NotNull(message = "分数不能为空")
        private java.math.BigDecimal score;
        private String comment;
    }
}
