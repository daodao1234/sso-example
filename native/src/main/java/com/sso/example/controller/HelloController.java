package com.sso.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sso.example.model.User;
import com.sso.example.service.SsoService;

@RestController
public class HelloController {

  @Autowired
  private SsoService ssoService;

  @RequestMapping(method = RequestMethod.GET, value = "/hello")
  public String hello() {
    User user = ssoService.getMe();
    return String.format("<h1>Hello, %s %s!</h1>", user.getFirstName(), user.getLastName());
  }

}
