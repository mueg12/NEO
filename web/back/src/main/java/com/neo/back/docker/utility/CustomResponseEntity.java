package com.neo.back.docker.utility;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CustomResponseEntity<T> {
    private T body;
    private String messege;

}
