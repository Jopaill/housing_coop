package com.jordanpaille.housing.coop.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Setter
@Getter
public class Person {
    private Long personId;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private String password;
    private Long coopId;
}
