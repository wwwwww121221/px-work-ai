package com.pxwork.api.controller.backend;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pxwork.common.utils.Result;
import com.pxwork.course.entity.ProcessEvaluation;
import com.pxwork.course.service.ProcessEvaluationService;
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

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Tag(name = "后台过程评价")
@RestController
@RequestMapping("/backend/evaluation")
public class BackendEvaluationController {

    private static final BigDecimal MAX_DIMENSION_SCORE = BigDecimal.valueOf(6);
    private static final BigDecimal MAX_TOTAL_SCORE = BigDecimal.valueOf(30);

    @Autowired
    private ProcessEvaluationService processEvaluationService;

    @Operation(summary = "讲师评分")
    @PostMapping("/score")
    public Result<Map<String, Object>> score(@RequestBody @Validated ScoreRequest request) {
        if (!isValidDimensionScore(request.getScoreProgress())
                || !isValidDimensionScore(request.getScorePrep())
                || !isValidDimensionScore(request.getScoreInteraction())
                || !isValidDimensionScore(request.getScoreDiscussion())
                || !isValidDimensionScore(request.getScorePractical())) {
            return Result.fail("每个维度分数必须在0到6之间");
        }
        BigDecimal totalScore = request.getScoreProgress()
                .add(request.getScorePrep())
                .add(request.getScoreInteraction())
                .add(request.getScoreDiscussion())
                .add(request.getScorePractical());
        if (totalScore.compareTo(MAX_TOTAL_SCORE) > 0) {
            return Result.fail("总分不能超过30分");
        }

        ProcessEvaluation evaluation = processEvaluationService.getOne(new LambdaQueryWrapper<ProcessEvaluation>()
                .eq(ProcessEvaluation::getUserId, request.getUserId())
                .eq(ProcessEvaluation::getCourseId, request.getCourseId()));
        boolean isNew = evaluation == null;
        if (isNew) {
            evaluation = new ProcessEvaluation();
            evaluation.setUserId(request.getUserId());
            evaluation.setCourseId(request.getCourseId());
        }
        evaluation.setScoreProgress(request.getScoreProgress());
        evaluation.setScorePrep(request.getScorePrep());
        evaluation.setScoreInteraction(request.getScoreInteraction());
        evaluation.setScoreDiscussion(request.getScoreDiscussion());
        evaluation.setScorePractical(request.getScorePractical());
        evaluation.setTotalScore(totalScore);

        boolean success = isNew ? processEvaluationService.save(evaluation) : processEvaluationService.updateById(evaluation);
        if (!success) {
            return Result.fail("评分保存失败");
        }
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("totalScore", totalScore);
        result.put("recordId", evaluation.getId());
        result.put("updated", !isNew);
        return Result.success(result);
    }

    private boolean isValidDimensionScore(BigDecimal score) {
        return score != null
                && score.compareTo(BigDecimal.ZERO) >= 0
                && score.compareTo(MAX_DIMENSION_SCORE) <= 0;
    }

    @Data
    public static class ScoreRequest {
        @NotNull(message = "学员ID不能为空")
        private Long userId;
        @NotNull(message = "课程ID不能为空")
        private Long courseId;
        @NotNull(message = "进度分不能为空")
        private BigDecimal scoreProgress;
        @NotNull(message = "预习分不能为空")
        private BigDecimal scorePrep;
        @NotNull(message = "互动分不能为空")
        private BigDecimal scoreInteraction;
        @NotNull(message = "讨论分不能为空")
        private BigDecimal scoreDiscussion;
        @NotNull(message = "实操分不能为空")
        private BigDecimal scorePractical;
    }
}
