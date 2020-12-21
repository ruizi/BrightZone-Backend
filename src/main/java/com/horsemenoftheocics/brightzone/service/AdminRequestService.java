package com.horsemenoftheocics.brightzone.service;

import com.horsemenoftheocics.brightzone.entity.Request;

import java.util.List;

public interface AdminRequestService {
    boolean updateRequestStatus(int requestId, String newStatus);

    boolean deleteAllByUserId(int userId);

    List<Request> getAllOpenRequest();
}
