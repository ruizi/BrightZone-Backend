package com.horsemenoftheocics.brightzone.service;

import com.horsemenoftheocics.brightzone.enums.DropStatus;
import com.horsemenoftheocics.brightzone.enums.RegisterStatus;
import com.horsemenoftheocics.brightzone.vo.CourseVo;

import java.util.List;
import java.util.Set;

public interface CourseService {
    RegisterStatus registerCourse(int studentId, int clazzId);

    DropStatus dropCourse(int studentId, int clazzId);

    List<CourseVo> getAllOpenedCourse(int studentId);

    Set<CourseVo> getAllRegisteredCourse(int studentId);

    CourseVo getCourse(int clazzId);

    void dropAllCourse(int studentId);

    void dropAllCourseByClazz(int clazzId);

    void deletePreByCourseId(int courseId);

}
