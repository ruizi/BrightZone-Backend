package com.horsemenoftheocics.brightzone.service.impl;

import com.horsemenoftheocics.brightzone.entity.Person;
import com.horsemenoftheocics.brightzone.repository.PersonRepository;
import com.horsemenoftheocics.brightzone.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PersonServiceImpl implements PersonService {

    @Autowired
    private PersonRepository personRepository;

    @Override
    public boolean isExist(int personId) {
        return findById(personId) != null;
    }

    @Override
    public Person findById(int personId) {
        Optional<Person> person = personRepository.findById(personId);
        return person.orElse(null);
    }
}
