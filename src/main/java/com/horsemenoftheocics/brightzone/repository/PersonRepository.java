package com.horsemenoftheocics.brightzone.repository;

import com.horsemenoftheocics.brightzone.entity.Person;
import com.horsemenoftheocics.brightzone.enums.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface PersonRepository extends JpaRepository<Person, Integer> {
    Optional<Person> findByEmail(String email);

    List<Person> findAllByTypeEquals(AccountType accountType);

    boolean existsPersonByEmail(String email);
}
