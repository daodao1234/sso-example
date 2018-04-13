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
