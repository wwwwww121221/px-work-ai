package com.pxwork.course.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("offline_attendance")
public class OfflineAttendance implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long courseId;

    private Long sessionId;

    private LocalDateTime punchTime;

    private Integer punchType;

    private Integer signMethod;

    private String location;

    private BigDecimal latitude;

    private BigDecimal longitude;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
