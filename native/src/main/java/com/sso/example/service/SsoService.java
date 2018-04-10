package com.sso.example.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.sso.example.model.Auth;
import com.sso.example.model.TokenPackage;
import com.sso.example.model.User;

@Service
public class SsoService {

  @Value("${sso.apiUrl}")
  private String apiUrl;

  @Value("${sso.username}")
  private String username;

  @Value("${sso.password}")
  private String password;

  private RestTemplate restTemplate() {
    RestTemplate restTemplate = new RestTemplate();
    return restTemplate;
  }

  private HttpHeaders getHeadersWithoutToken() {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    return headers;
  }

  private HttpHeaders getHeadersWithToken() {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("Authorization", String.format("Bearer %s", getTokenPackage().getAccessToken()));
    return headers;
  }

  private TokenPackage getTokenPackage() {
    Auth auth = new Auth(username, password);
    HttpEntity<Auth> request = new HttpEntity<Auth>(auth, getHeadersWithoutToken());
    TokenPackage response = restTemplate().postForObject(String.format("%s%s", apiUrl, "/auth/native"), request,
        TokenPackage.class);
    return response;
  }

  public User getMe() {
    HttpEntity<Object> request = new HttpEntity<Object>(getHeadersWithToken());
    User response = restTemplate()
        .exchange(String.format("%s%s", apiUrl, "/users/me"), HttpMethod.GET, request, User.class).getBody();
    return response;
  }

}
