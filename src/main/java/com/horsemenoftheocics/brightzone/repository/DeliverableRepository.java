package com.horsemenoftheocics.brightzone.repository;

import com.horsemenoftheocics.brightzone.entity.Deliverable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface DeliverableRepository extends CrudRepository<Deliverable, Integer> {
    List<Deliverable> findByClassId(int class_id);

    void deleteByClassId(int class_id);

}
