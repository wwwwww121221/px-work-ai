package com.pxwork.course.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pxwork.course.entity.CourseChapter;

public interface CourseChapterService extends IService<CourseChapter> {
    boolean removeChapterWithHours(Long chapterId);
}
