package com.horsemenoftheocics.brightzone.repository;

import com.horsemenoftheocics.brightzone.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;

public interface CourseRepository extends JpaRepository<Course, Integer> {
    boolean existsCourseByCourseSubjectAndCourseNumber(String courseSubject, String courseNumber);

    Course findByCourseSubjectAndCourseNumber(String courseSubject, String courseNumber);

    ArrayList<Course> findAllByCourseSubject(String courseSubject);

    boolean existsCourseByCourseName(String courseName);

}
