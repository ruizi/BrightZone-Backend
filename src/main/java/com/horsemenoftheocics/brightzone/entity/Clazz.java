package com.horsemenoftheocics.brightzone.entity;

import com.horsemenoftheocics.brightzone.enums.ClassStatus;
import com.horsemenoftheocics.brightzone.enums.ScheduleType;
import com.horsemenoftheocics.brightzone.enums.StartDayOfWeek;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Time;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "class")
public class Clazz {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int classId;
    private int courseId;
    private String classDesc;
    @Enumerated(EnumType.STRING)
    private ClassStatus classStatus;
    private int section;
    private int enrolled;
    private int enrollCapacity;
    private int profId;
    private Timestamp enrollDeadline;
    private Timestamp dropNoPenaltyDeadline;
    private Timestamp dropNoFailDeadline;
}
