package com.horsemenoftheocics.brightzone.repository;

import com.horsemenoftheocics.brightzone.entity.Classroom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;

public interface ClassroomRepository extends JpaRepository<Classroom, Integer> {
    ArrayList<Classroom> findAllByRoomCapacity(int roomCapacity);

    //ArrayList<Classroom> findClassroomsByRoomCapacityGreaterThanEqual(int roomCapacity);

    boolean existsClassroomsByRoomCapacity(int roomCapacity);

    //ArrayList<Classroom> findAllByRoomCapacityEquals(int roomCapacity);

}
