package com.horsemenoftheocics.brightzone.repository;

import com.horsemenoftheocics.brightzone.entity.Account;
import com.horsemenoftheocics.brightzone.entity.ClassroomSchedule;
import com.horsemenoftheocics.brightzone.enums.WeekDay;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;

public interface ClassroomScheduleRepository extends JpaRepository<ClassroomSchedule, Integer> {
    //ClassroomSchedule findByWeekdayAndRoomCapacity(WeekDay weekDay, int roomCapacity);

    ArrayList<ClassroomSchedule> findAllByWeekdayAndRoomCapacity(WeekDay weekDay, int roomCapacity);

    ArrayList<ClassroomSchedule> findAllByClassId(int classId);

    int deleteByClassId(int classId);

    int deleteByProfessorId(int professorId);
}
