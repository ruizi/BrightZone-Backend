package com.horsemenoftheocics.brightzone.entity;

import com.horsemenoftheocics.brightzone.enums.AccountStatus;
import com.horsemenoftheocics.brightzone.enums.AccountType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import java.sql.Timestamp;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Account {
    @Id
    private Integer userId;
    private String name;

    @Enumerated(EnumType.STRING)
    private AccountType type;
    private Integer facultyId;
    private String program;
    private String email;
    private String password;

    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus;
    private Timestamp lastLogin;
    private String verificationCode;
}