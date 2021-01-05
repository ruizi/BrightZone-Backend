package com.horsemenoftheocics.brightzone.repository;

import com.horsemenoftheocics.brightzone.entity.Request;
import com.horsemenoftheocics.brightzone.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Integer> {
    void deleteAllByUserId(int userId);

    List<Request> findAllByStatus(RequestStatus requestStatus);
}
