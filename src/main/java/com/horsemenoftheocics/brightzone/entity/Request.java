package com.horsemenoftheocics.brightzone.entity;

import com.horsemenoftheocics.brightzone.enums.RequestStatus;
import com.horsemenoftheocics.brightzone.enums.RequestType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer requestId;  // primary key and auto increment
    private Integer userId;

    @Enumerated(EnumType.STRING)
    private RequestType type;

    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    private String message;
}
