package com.example.instagrambe.domain.auth.api.controller;

import com.example.instagrambe.common.support.response.ApiResponse;
import com.example.instagrambe.domain.auth.api.controller.dto.request.AuthCodeRequestDto;
import com.example.instagrambe.domain.auth.api.controller.dto.request.JoinRequestDto;
import com.example.instagrambe.domain.auth.api.controller.dto.request.VerifyCodeRequestDto;
import com.example.instagrambe.domain.auth.api.controller.dto.response.MemberResponseDto;
import com.example.instagrambe.domain.auth.api.service.AuthService;
import com.example.instagrambe.domain.auth.api.service.dto.response.MemberResponseServiceDto;
import com.example.instagrambe.domain.auth.jwt.constant.JwtProperties;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;
  private final JwtProperties jwtProperties;

  @PostMapping("/join")
  public ResponseEntity<ApiResponse<MemberResponseDto>> join(
      @Valid @RequestBody JoinRequestDto joinRequestDto) {
    MemberResponseServiceDto responseServiceDto = authService.join(joinRequestDto.toServiceDto());
    return new ResponseEntity<>(ApiResponse.success(MemberResponseDto.from(responseServiceDto)),
        HttpStatus.OK);
  }

  @PostMapping("/send-code")
  public ResponseEntity<ApiResponse<Object>> sendCode(
      @Valid @RequestBody AuthCodeRequestDto authCodeRequestDto) {
    authService.sendCode(authCodeRequestDto.getEmail());
    return new ResponseEntity<>(ApiResponse.success(), HttpStatus.OK);
  }

  @PostMapping("/verify-code")
  public ResponseEntity<ApiResponse<Object>> verifyCode(
      @Valid @RequestBody VerifyCodeRequestDto verifyCodeRequestDto) {
    authService.verifyCode(verifyCodeRequestDto.toServiceDto());
    return new ResponseEntity<>(ApiResponse.success(), HttpStatus.OK);
  }

  @PostMapping("/logout")
  public ResponseEntity<ApiResponse<Object>> logout(HttpServletRequest request) {
    authService.logout(request.getHeader(jwtProperties.getAccessHeader()), new Date());
    return new ResponseEntity<>(ApiResponse.success(), HttpStatus.OK);
  }
}
