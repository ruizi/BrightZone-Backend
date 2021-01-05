package com.horsemenoftheocics.brightzone.service;

import com.horsemenoftheocics.brightzone.entity.Person;

public interface PersonService {

    boolean isExist(int personId);

    Person findById(int personId);

}
