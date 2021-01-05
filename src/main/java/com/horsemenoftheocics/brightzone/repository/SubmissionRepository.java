package com.horsemenoftheocics.brightzone.repository;

import com.horsemenoftheocics.brightzone.entity.Submission;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SubmissionRepository extends CrudRepository<Submission, Integer> {
    List<Submission> findByDeliverableIdAndStudentIdOrderBySubmitTimeDesc(int deliverable_id, int student_id);

    List<Submission> findByDeliverableIdOrderBySubmitTimeDesc(int deliverable_id);

    List<Submission> findByStudentId(int studentId);

    void deleteByDeliverableId(int deliverable_id);

    void deleteByDeliverableIdAndStudentId(int deliverableId, int studentId);

    void deleteByStudentId(int studentId);

}