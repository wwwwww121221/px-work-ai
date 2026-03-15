package com.pxwork.api.controller.backend;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pxwork.common.utils.Result;
import com.pxwork.course.entity.ProcessEvaluation;
import com.pxwork.course.service.ProcessEvaluationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Tag(name = "3.2 后台-过程性评价")
@RestController
@RequestMapping("/backend/evaluation")
public class BackendEvaluationController {

    @Autowired
    private ProcessEvaluationService processEvaluationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Operation(summary = "讲师评分")
    @PutMapping("/score")
    public Result<Map<String, Object>> score(@RequestBody @Validated ScoreRequest request) {
        BigDecimal totalScore = BigDecimal.ZERO;
        for (EvaluationItem item : request.getEvaluationItems()) {
            if (item.getScore() == null) {
                return Result.fail("评价项分数不能为空");
            }
            if (item.getScore().compareTo(BigDecimal.ZERO) < 0) {
                return Result.fail("评价项分数不能为负数");
            }
            totalScore = totalScore.add(item.getScore());
        }
        String evaluationDetails;
        try {
            evaluationDetails = objectMapper.writeValueAsString(request.getEvaluationItems());
        } catch (JsonProcessingException e) {
            return Result.fail("评价明细序列化失败");
        }

        List<ProcessEvaluation> evaluationList = processEvaluationService.list(new LambdaQueryWrapper<ProcessEvaluation>()
                .eq(ProcessEvaluation::getUserId, request.getUserId())
                .eq(ProcessEvaluation::getCourseId, request.getCourseId())
                .orderByDesc(ProcessEvaluation::getId));
        ProcessEvaluation evaluation = evaluationList.isEmpty() ? null : evaluationList.get(0);
        boolean isNew = evaluation == null;
        if (isNew) {
            evaluation = new ProcessEvaluation();
            evaluation.setUserId(request.getUserId());
            evaluation.setCourseId(request.getCourseId());
        }
        evaluation.setEvaluationDetails(evaluationDetails);
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

    @Data
    public static class ScoreRequest {
        @NotNull(message = "学员ID不能为空")
        private Long userId;
        @NotNull(message = "课程ID不能为空")
        private Long courseId;
        @NotEmpty(message = "评价明细不能为空")
        @Valid
        private List<EvaluationItem> evaluationItems;
    }

    @Data
    public static class EvaluationItem {
        @NotBlank(message = "评价维度不能为空")
        private String dimension;
        @NotNull(message = "评价分数不能为空")
        private BigDecimal score;
    }
}
