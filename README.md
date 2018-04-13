# SSO 整合教學

PaaS 提供單一登入身份驗證方案。使用單一登入, 使用者只需登入一次即可使用在 PaaS 上不同的應用程式。

單一登入提供了兩種整合方式, 一種用於前端應用程式, 另一種則用於後端（原生）應用程式。

您可以下載本教學的開源代碼庫, 或使用 Git clone：
```
git clone https://github.com/ironman1990/sso-example
```

## API 文件

範例程式碼中所使用的 API 可以在此[文件](../../../doc/document-portal.html#SSO-2)中找到。

## 前端應用程式

以前端的整合方式來說，使用者必須使用單一登入方案進行登入才能獲得 **EIToken** cookie 來完成身份驗證。例如 [Technical Portal](../../../index.html) 就是這樣。

此外，由於安全問題，前端應用程式是無法直接從瀏覽器取得此 cookie 的。但它仍然可以透過 Ajax 取得使用者資訊。

  ![](../uploads/images/SSO/frontendSignIn.png)

### [HTML]
#### 步驟 1. 建立 index.html
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
您可以自行決定是否更改在 `<title>` 中的網頁標題。此範例預設為 **SSO Example**。

#### 步驟 2. 在結束標記 `</body>` 之前導入 [jQuery](https://jquery.com/) 函式庫與 index.js
```
    <script src="https://code.jquery.com/jquery-3.3.1.min.js"></script>
    <script src="index.js"></script>
```
本範例將使用 jQuery 和自訂的 JavaScript：index. js。

#### 步驟 3. 在起始標記 `<body>` 之後新增 `<button>` 與 `<h1>`
```
    <button class="btn btn-primary" id="signInBtn" style="display: none;">Sign in</button>
    <button class="btn btn-primary" id="signOutBtn" style="display: none;">Sign out</button>
    <h1 id="helloMsg"></h1>
```
兩個 `<button>` 分別用於執行登入和登出操作。`<h1>` 則用於區分登入和登出的訊息顯示。

### [JavaScript]
#### 步驟 1. 建立 index.js
```
$(function  ()  {
    // Add Step 2. to Step 4. here
});
```
jQuery 提供了一種當頁面的[文件物件模型（DOM）](https://developer.mozilla.org/zh-TW/docs/Web/API/Document_Object_Model)變為可安全操作狀態後立即執行 JavaScript 程式碼的方法。

#### 步驟 2. 新增 myUrl 與 ssoUrl 變數
```
    var myUrl = window.location.protocol + '//' + window.location.hostname;
    var ssoUrl = myUrl.replace('sso-web-ex', 'portal-sso');
```
**myUrl** 是此範例程式在 PaaS 上的 URL。
**ssoUrl** 是 SSO 在 PaaS 上的 URL。這裡的 **sso-web-ex** 是根據應用程式的子網域名稱設定的，必須與 manifest.yml 裡的 name 參數保持一致，因為 name 參數即 PaaS 給予應用程式的子網域名稱，可依需求進行變更。

#### 步驟 3. 新增登入和登出按鈕的 click 函式
```
    $('#signInBtn').click(function () {
        window.location.href = ssoUrl + '/web/signIn.html?redirectUri=' + myUrl;
    });

    $('#signOutBtn').click(function () {
        window.location.href = ssoUrl + '/web/signOut.html?redirectUri=' + myUrl;
    });
```
這裡的主要行為是重新導向並傳遞 **redirectUri** 參數至 SSO 頁面。

#### 步驟 4. 新增一個 [Ajax](http://api.jquery.com/jquery.ajax/) 函式用以識別使用者的登入狀態
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
由於應用程式與 SSO 屬跨網域的關係，因此必須將 **withCredentials** 設置為 true。
HTTP 請求完成後，使用者已登入會執行 **done** 回調函式中的語法，否則將執行 **fail** 回調函式中的語法。**done** 回調函式將會顯示登出按鈕與 'Hello, O O!' 的歡迎訊息，**fail** 回調函式則將會顯示登入按鈕與 'Hi, please sign in first' 的登入訊息。

### [測試]
1. 修改 manifest.yml
2. 登入並將應用程式推送到 PaaS
```
    $ cf login -a api.iii-cflab.com -u {your username} -p {password}
    $ cf push -f manifest.yml
```
3. 進入 **https://{應用程式的子網域名稱}.{PaaS的主網域名稱}** 執行登入與登出操作。

## 後端（原生）應用程式
以後端（原生）的整合方式來說，這裡提供的是一個基於 Java 的範例。本範例將學習如何取得 **EIToken** 並透過它取得使用者資訊。

  ![](../uploads/images/SSO/nativeSignIn.png)

### [前置條件]
1. [Java 1.8](https://java.com/zh_TW/)
2. [Gradle](https://gradle.org/)
3. [Spring Boot](https://projects.spring.io/spring-boot/)

### [資料夾目錄樹狀圖]
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
 |--manifest.yml
```

### [設定]
#### 步驟 1. 建立 build.gradle
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

#### 步驟 2. 建立 application.yml
```
server:
  port: 9453

sso:
  apiUrl: https://portal-sso.{Paas domain name}.com/v1.3
  username: {username on PaaS}
  password: {password on PaaS}
```
如果要在本機測試應用程式，請將 port 的值更改為執行環境上未使用的連接埠，這裡預設連接埠為**9453**。
將 **{PaaS domain name}** 替換為 PaaS 的主網域名稱。
將 **{username on PaaS}** 替換為您在 PaaS 上的使用者名稱。
將 **{password on PaaS}** 替換為您在 PaaS 上的密碼。

### [Java]
#### 步驟 1. 建立 App.java
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

#### 步驟 2. 建立 Auth.java
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
**Auth** 模型是向 SSO 發送身份驗證的 HTTP 請求時所須包含的請求正文（request body）。

### 步驟 3. 建立 TokenPackage.java
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
**TokenPackage** 模型是在身份驗證通過後 SSO 回傳的回應正文（response body）。這裡的 **AccessToken** 屬性即 **EIToken**。

#### 步驟 4. 建立 User.java
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
**User** 模型是在取得使用者資訊成功時 SSO 回傳的回應正文。

#### 步驟 5. 建立 SsoService.java
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
部份 SSO API 端點不需要存取權杖，例如 '/auth/native',使用 **getHeadersWithoutToken()** 方法。反之如 '/users/me'，則使用 **getHeadersWithToken()** 方法。

#### 步驟 6. 建立 HelloController.java
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
最後再產生 API 端點 **/hello** 來顯示通過 SSO 身份驗證的歡迎訊息。

### [測試]
#### 方法 1 
1. 本機執行應用程式並於網址列輸入 **http://localhost:9453/hello** 將會顯示 Hello, O O!

#### 方法 2
1. 修改 manifest.yml
2. 登入並將應用程式推送到 PaaS
```
    $ cf login -a api.iii-cflab.com -u {your username} -p {password}
    $ cf push -f manifest.yml
```
3. 輸入 **https://{應用程式的子網域名稱}.{PaaS 主網域名稱}/hello** 將會顯示 Hello, O O!
