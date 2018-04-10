# SSO Integration Tutorial
PaaS provides a single sign-on authentication scheme. With single sign-on, users only need to log in once to use different applications on the PaaS.

Single sign-on offers two ways to integrate, one for web-based applications and the other for native applications.

Download and unzip the source repository for this guide, or clone it using Git: 
```
git clone https://github.com/ironman1990/sso-example
```

## Web-Based Application
For web-based integration way, users need to log in using a single sign-on scheme to obtain a **EIToken** cookie to complete the authentication. For example, 'Technical Portal'.

Besides, front-end application can't directly get this cookie from browser due to security issue. But it can still get user information through Ajax.

### [HTML]
#### Step 1. Create index.html
```
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>SSO Example</title>
</head>
<body>
</body>
</html>
```
You can change title in `<title>` at your discretion. Here is **SSO Example**.

#### Step 2. Import [jQuery](https://jquery.com/) library and index.js before closing `</body>` tag
```
    <script src="https://code.jquery.com/jquery-3.3.1.min.js"></script>
    <script src="index.js"></script>
```
This example will use jQuery and custom JavaScript index.js.

#### Step 3. Add `<button>`s, `<h1>` after `<body>` tag
```
    <button class="btn btn-primary" id="signInBtn" style="display: none;">Sign in</button>
    <button class="btn btn-primary" id="signOutBtn" style="display: none;">Sign out</button>
    <h1 id="helloMsg"></h1>
```
Two `<button>`s are used to do login and logout respectively.`<h1>`is used to distinguish between login and logout message display.

### [JavaScript]
#### Step 1. Create index.js
```
$(function  ()  {
    // Add Step 2. to Step 4. here
});
```
The jQuery method offers a way to run JavaScript code as soon as the page's Document Object Model (DOM) becomes safe to manipulate.

#### Step 2. Add myUrl and ssoUrl variables
```
    var myUrl = window.location.protocol + '//' + window.location.hostname;
    var ssoUrl = myUrl.replace('sso-web-ex', 'portal-sso');
```
**myUrl** is the URL of your application on PaaS.
**ssoUrl** is the URL of SSO on PaaS. You should replace **sso-web-ex** with subdomain name of your application. The domain name for this example is assumed to be https://**sso-web-ex**.{PaaS domain name}.

#### Step 3. Add click function of login and logout buttons
```
    $('#signInBtn').click(function () {
        window.location.href = ssoUrl + '/web/signIn.html?redirectUri=' + myUrl;
    });

    $('#signOutBtn').click(function () {
        window.location.href = ssoUrl + '/web/signOut.html?redirectUri=' + myUrl;
    });
```
The main behavior here is to redirect to the SSO page with the parameter **redirectUri**.

#### Step 4. Add a [Ajax](http://api.jquery.com/jquery.ajax/) function to identify the user's login status
```
    $.ajax({
        url: ssoUrl + '/v1.3/users/me',
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        },
        xhrFields: {
            withCredentials: true
        }
    }).done(function (user) {
        $('#signOutBtn').show();
        $('#helloMsg').text('Hello, ' + user.firstName + ' ' + user.lastName + '!');
    }).fail(function (jqXHR, textStatus, errorThrown) {
        $('#signInBtn').show();
        $('#helloMsg').text('Hi, please sign in first.');
    });
```
Because your application has a cross-domain relationship with SSO, it is necessary to set **withCredentials** to true. 
After HTTP request completed, user logged in will execute scripts in **done** callback, otherwise scripts in **fail** callback will be executed. In **done** callback, will display logout button and 'Hello, O O!' message. In **fail** callback, will display login button and 'Hi, please sign in first.' message.

### [Testing]
Push application to PaaS, then enter **https://{subdomain name of your application}.{PaaS domain name}** to do login and logout.

## Native Application
For native integration way, here is a java-based tutorial. The example will learn how to obtain **EIToken** and use it to obtain user information.

### [Prerequisite]
1. [Java 1.8](https://java.com/zh_TW/)
2. [Gradle](https://gradle.org/)
3. [Spring Boot](https://projects.spring.io/spring-boot/)

### [Directory Tree]
```
sso-native-ex
 |--src
 |  |--main
 |     |--java
 |     |  |--com
 |     |     |--sso
 |     |        |--example
 |     |           |--App.java
 |     |           |--controller
 |     |           |  |--HelloController.java
 |     |           |--model
 |     |           |  |--Auth.java
 |     |           |  |--TokenPackage.java
 |     |           |  |--User.java
 |     |           |--service
 |     |              |--SsoService.java
 |     |--resources
 |        |--application.yml
 |--build.gradle
```

### [Configuration]
#### Step 1. Create build.gradle
```
buildscript {
	ext {
		springBootVersion = '2.0.1.RELEASE'
	}
	repositories {
		mavenCentral()
	}
	dependencies {
		classpath("org.springframework.boot:spring-boot-gradle-plugin:${springBootVersion}")
	}
}

apply plugin: 'java'
apply plugin: 'eclipse'
apply plugin: 'org.springframework.boot'
apply plugin: 'io.spring.dependency-management'

group = 'com.sso.example'
version = '0.1.0'
sourceCompatibility = 1.8

repositories {
	mavenCentral()
}

dependencies {
	compile('org.springframework.boot:spring-boot-starter-web')
	testCompile('org.springframework.boot:spring-boot-starter-test')
}
```

#### Step 2. Create application.yml
```
server:
  port: 9453

sso:
  apiUrl: https://portal-sso.{Paas domain name}.com/v1.3
  username: {username on PaaS}
  password: {password on PaaS}
```
If you want to test application locally, set **port** to unused port on your device, here is **9453** port.
Replace **{PaaS domain name}** with PaaS domain name.
Replace **{username on PaaS}** with your PaaS username.
Replace **{password on PaaS}** with your PaaS password.

### [Java]
#### Step 1. Create App.java
```
package com.sso.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class App {

  public static void main(String[] args) {
    SpringApplication.run(App.class, args);
  }

}
```

#### Step 2. Create Auth.java
```
package com.sso.example.model;

public class Auth {

  private String username;
  private String password;

  public Auth() {}

  public Auth(String username, String password) {
    this.username = username;
    this.password = password;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  @Override
  public String toString() {
    return "AuthRequest [username=" + username + ", password=" + password + "]";
  }

}
```
The **Auth** model is the request body that must be included when sending a HTTP request to SSO for authentication.

#### Step 3. Create TokenPackage.java
```
package com.sso.example.model;

public class TokenPackage {
  
  private String tokenType;
  private String accessToken;
  private Long expiresIn;
  private String refreshToken;

  public String getTokenType() {
    return tokenType;
  }

  public void setTokenType(String tokenType) {
    this.tokenType = tokenType;
  }

  public String getAccessToken() {
    return accessToken;
  }

  public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }

  public Long getExpiresIn() {
    return expiresIn;
  }

  public void setExpiresIn(Long expiresIn) {
    this.expiresIn = expiresIn;
  }

  public String getRefreshToken() {
    return refreshToken;
  }

  public void setRefreshToken(String refreshToken) {
    this.refreshToken = refreshToken;
  }

  @Override
  public String toString() {
    return "TokenPackage [tokenType=" + tokenType + ", accessToken=" + accessToken + ", expiresIn=" + expiresIn
        + ", refreshToken=" + refreshToken + "]";
  }

}
```
The **TokenPackage** model is the response body that SSO returns when authentication passes. The **AccessToken** attribute here is **EIToken**.

#### Step 4. Create User.java
```
package com.sso.example.model;

public class User {

  private String username;
  private String firstName;
  private String lastName;
  private String contactPhone;
  private String country;
  private String role;
  private String status;

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getContactPhone() {
    return contactPhone;
  }

  public void setContactPhone(String contactPhone) {
    this.contactPhone = contactPhone;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  @Override
  public String toString() {
    return "User [username=" + username + ", firstName=" + firstName + ", lastName=" + lastName + ", contactPhone="
        + contactPhone + ", country=" + country + ", role=" + role + ", status=" + status + "]";
  }

}
```
The **User** model is the response body that SSO returns when get user successfully.

#### Step 5. Create SsoService.java
```
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
```
Partial SSO API endpoints do not require access tokens, such as '/auth/native', using the **getHeadersWithoutToken()** method. Conversely, such as '/users/me ', using the **getHeadersWithToken()** method.

#### Step 6. Create HelloController.java
```
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
```
Generate an API endpoint **/hello** to show welcome message.

### [Testing]
1. Execute application locally and enter **http://localhost:9453/hello** will see Hello, O O!.
2. Push application to PaaS, then enter **https://{subdomain name of your application}.{PaaS domain name}/hello** will see Hello, O O!.
