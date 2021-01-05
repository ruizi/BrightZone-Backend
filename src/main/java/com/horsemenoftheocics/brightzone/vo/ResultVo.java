package com.horsemenoftheocics.brightzone.vo;

import lombok.Data;

@Data
public class ResultVo<T> {
    private T data;
    private int status;
    private String desc;
}
