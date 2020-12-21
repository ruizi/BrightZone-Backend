package com.horsemenoftheocics.brightzone.repository;

import com.horsemenoftheocics.brightzone.entity.Prerequisite;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public interface PrerequisiteRepository extends CrudRepository<Prerequisite, Integer> {
    Set<Prerequisite> findByCourseId(int courseId);

    Set<Prerequisite> findAllByCourseId(int courseId);

    ArrayList<Prerequisite> findAllByCourseIdAndAndPrerequisiteId(int courseId, int prerequisiteId);

    boolean existsPrerequisiteByCourseIdAndPrerequisiteId(int courseId, int prerequisiteId);

    boolean existsByCourseIdAndPrerequisiteId(int courseId, int prerequisiteId);

    boolean existsByCourseId(int courseId);

    Integer deleteAllByCourseId(int courseId);

    Integer deleteByCourseIdAndPrerequisiteId(int courseId, int prerequisiteId);

}
