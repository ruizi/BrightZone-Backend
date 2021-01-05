package com.horsemenoftheocics.brightzone.service;


import com.horsemenoftheocics.brightzone.entity.Account;
import com.horsemenoftheocics.brightzone.entity.Classroom;
import com.horsemenoftheocics.brightzone.entity.ClassroomSchedule;
import com.horsemenoftheocics.brightzone.entity.Clazz;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

public interface AdminClazzService {

    ArrayList<Clazz> getClassInfoByCourseId(int courseId);

    ArrayList<ClassroomSchedule> getClassSchedulesByClassId(int classId);

    Account getProfessorById(int id);

    Account getProfessorByEmail(String email);

    ArrayList<Account> getProfessorList();

    TreeSet<Integer> getClassroomSizeList();

    ArrayList<Classroom> classroomSchedule(HashMap<String, String> checkMap) throws ParseException;

    Clazz addNewClassInfo(Clazz newClazz);

    Clazz updateClassInfo(Clazz newEditClazz);

    Integer addNewClassSchedules(ArrayList<HashMap<String, String>> newClassroomSchedule);

    Integer updateClassSchedules(ArrayList<HashMap<String, String>> newEditClassroomSchedule);

    Integer deleteClassByClassId(int classId);

}
