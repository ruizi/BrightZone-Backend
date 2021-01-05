package com.horsemenoftheocics.brightzone.repository;

import com.horsemenoftheocics.brightzone.entity.Enrollment;
import com.horsemenoftheocics.brightzone.enums.EnrollmentStatus;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface EnrollmentRepository extends CrudRepository<Enrollment, Integer> {
    List<Enrollment> findByClassIdAndStudentIdAndStatus(int class_id, int student_id, EnrollmentStatus status);
    List<Enrollment> findByClassIdAndStatus(int class_id, EnrollmentStatus status);
    Optional<Enrollment> findByClassIdAndStudentId(int class_id, int student_id);
    Set<Enrollment> findByStudentIdAndStatus(int studentId, EnrollmentStatus status);
    void deleteByStudentId(int studentId);
    int deleteByClassId(int classId);
}
