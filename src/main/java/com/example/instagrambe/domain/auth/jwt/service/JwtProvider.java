package com.example.instagrambe.domain.auth.jwt.service;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.instagrambe.common.exception.custom.JwtValidationException;
import com.example.instagrambe.domain.auth.jwt.constant.JwtProperties;
import com.example.instagrambe.domain.auth.jwt.repository.TokenRepository;
import java.util.Date;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtProvider {

  private final TokenRepository tokenRepository;
  private final JwtProperties jwtProperties;

  public Optional<String> extractToken(String requestTokenHeader) {
    return Optional.ofNullable(requestTokenHeader)
        .filter(header -> header.startsWith(JwtProperties.BEARER))
        .map(header -> header.replace(JwtProperties.BEARER, JwtProperties.REPLACEMENT));
  }

  public Optional<String> extractEmail(String token) {
    try {
      return Optional.ofNullable(JWT.require(Algorithm.HMAC512(jwtProperties.secretKey))
          .build()
          .verify(token)
          .getClaim(JwtProperties.EMAIL_CLAIM)
          .asString());
    } catch (Exception e) {
      log.warn("토큰이 유효하지 않습니다. " + e.getMessage());
      throw new JwtValidationException("토큰으로 이메일 찾기 실패");
    }
  }

  public String createAccessToken(String username, Date now) {
    return JWT.create() // JWT 토큰을 생성하는 빌더 반환
        .withSubject(JwtProperties.ACCESS_TOKEN_SUBJECT) // JWT의 Subject 지정 -> AccessToken이므로 AccessToken
        .withExpiresAt(new Date(now.getTime() + jwtProperties.accessTokenExpirationPeriod)) // 토큰 만료 시간 설정
        .withClaim(JwtProperties.EMAIL_CLAIM, username)  // 클레임으로는 email 하나만 사용
        .sign(Algorithm.HMAC512(jwtProperties.secretKey)); // HMAC512 알고리즘 사용, application-jwt.yml에서 지정한 secret 키로 암호화
  }

  public String createRefreshToken(String username, Date now) {
    String refreshToken = JWT.create()
        .withSubject(JwtProperties.REFRESH_TOKEN_SUBJECT)
        .withExpiresAt(new Date(now.getTime() + jwtProperties.refreshTokenExpirationPeriod))
        .withClaim(JwtProperties.EMAIL_CLAIM, username) //리프레시 토큰에 사용자 정보를 저장하는게 맞는가? 고민
        .sign(Algorithm.HMAC512(jwtProperties.secretKey));
    tokenRepository.save(username, refreshToken, jwtProperties.refreshTokenExpirationPeriod);
    return refreshToken;
  }
}
