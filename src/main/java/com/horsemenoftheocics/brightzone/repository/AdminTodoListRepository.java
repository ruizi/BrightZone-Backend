package com.horsemenoftheocics.brightzone.repository;

import com.horsemenoftheocics.brightzone.entity.AdminTodoList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdminTodoListRepository extends JpaRepository<AdminTodoList, Integer> {
    List<AdminTodoList> findAllByAdminIdAndStatusEquals(int adminId, boolean isFinished);

    AdminTodoList findByAdminId(int adminId);

    void deleteAllByAdminId(int adminId);
}
