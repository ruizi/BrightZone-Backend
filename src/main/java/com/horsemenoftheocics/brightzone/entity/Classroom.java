package com.horsemenoftheocics.brightzone.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Classroom {
    @Id
    private Integer roomId;
    private Integer roomCapacity;
    private String roomDesc;
}
