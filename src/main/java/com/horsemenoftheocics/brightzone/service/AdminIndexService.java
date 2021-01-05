package com.horsemenoftheocics.brightzone.service;

import com.horsemenoftheocics.brightzone.entity.AdminTodoList;
import net.minidev.json.JSONObject;

import java.util.List;


public interface AdminIndexService {
    Integer getAccountTableSize();

    Integer getCourseTableSize();

    Integer getClazzTableSize();

    Integer getClazzRoomTableSize();

    AdminTodoList getTodoListById(int todoId);

    List<AdminTodoList> getAdminTodoList(int adminId);

    Integer addAdminToDoList(AdminTodoList addForm);

    Integer changeToDoStatus(int todoListId);

    Integer modifyAdminTodoList(AdminTodoList editForm);
}
