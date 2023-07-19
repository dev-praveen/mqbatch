package com.praveen.mqbatch.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Employee /*implements Serializable*/ {

  // @Serial private static final long serialVersionUID = -5432207710325605746L;
  private Integer empNo;
  private String birthDate;
  private String firstName;
  private String lastName;
  private String gender;
  private String hireDate;
}
