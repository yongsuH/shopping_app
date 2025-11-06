package com.bf.project1030;

import com.bf.project1030.entity.User;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Project1030Application {

  public static void main(String[] args) {
    SpringApplication.run(Project1030Application.class, args);
    User u = new User();
  }

}
