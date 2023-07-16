package com.praveen.mqbatch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MqBatchApplication {

  public static void main(String[] args) {
    System.exit(SpringApplication.exit(SpringApplication.run(MqBatchApplication.class, args)));
  }
}
