package com.horsemenoftheocics.brightzone.repository;

import com.horsemenoftheocics.brightzone.entity.Clazz;
import com.horsemenoftheocics.brightzone.enums.ClassStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import javax.persistence.LockModeType;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface ClazzRepository extends JpaRepository<Clazz, Integer> {
    List<Clazz> findAllByClassStatus(ClassStatus classStatus);

    ArrayList<Clazz> findAllByCourseId(Integer courseId);

    List<Clazz> findByProfId(int prof_id);

    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    Optional<Clazz> findByClassId(int classId);

    int deleteByProfId(int profId);
}
