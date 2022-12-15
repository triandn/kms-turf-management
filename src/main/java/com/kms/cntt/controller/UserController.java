package com.kms.cntt.controller;

import com.kms.cntt.mapper.UserMapper;
import com.kms.cntt.model.User;
import com.kms.cntt.payload.general.ResponseDataAPI;
import com.kms.cntt.payload.request.ChangePasswordRequest;
import com.kms.cntt.payload.request.UserSignUpRequest;
import com.kms.cntt.payload.response.UserResponse;
import com.kms.cntt.security.annotations.CurrentUser;
import com.kms.cntt.security.oauth2.UserPrincipal;
import com.kms.cntt.service.UserService;
import com.kms.cntt.utils.PagingUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  private final UserMapper userMapper;

  @PostMapping("/user/register")
  public ResponseEntity<ResponseDataAPI> registerUser(
      @Valid @RequestBody UserSignUpRequest userSignUpRequest) {
    return userService.registerUser(userSignUpRequest);
  }

  @GetMapping("/user")
  public ResponseEntity<ResponseDataAPI> getUser(@CurrentUser UserPrincipal userPrincipal) {
    User user = userService.findById(userPrincipal.getId());
    UserResponse userResponse = userMapper.toResponse(user);
    return ResponseEntity.ok(ResponseDataAPI.success(userResponse, null));
  }

  @GetMapping("/users")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public ResponseEntity<ResponseDataAPI> getAllUser(
      @RequestParam(value = "page", defaultValue = "1") int page,
      @RequestParam(value = "paging", defaultValue = "10") int paging,
      @RequestParam(value = "sort", defaultValue = "created_at") String sortBy,
      @RequestParam(value = "order", defaultValue = "desc") String order) {
    Pageable pageable = PagingUtils.makePageRequest(sortBy, order, page, paging);
    return ResponseEntity.ok(userService.findAll(pageable));
  }

  @PostMapping("/user/change_password")
  public ResponseEntity<ResponseDataAPI> changePassword(
      @CurrentUser UserPrincipal userPrincipal,
      @Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
    return ResponseEntity.ok(
        userService.changePassword(
            userPrincipal.getId(), userPrincipal.getPassword(), changePasswordRequest));
  }
}
