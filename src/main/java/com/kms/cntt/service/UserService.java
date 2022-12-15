package com.kms.cntt.service;

import com.kms.cntt.model.User;
import com.kms.cntt.payload.general.ResponseDataAPI;
import com.kms.cntt.payload.request.ChangePasswordRequest;
import com.kms.cntt.payload.request.UserSignUpRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface UserService {
  User findById(UUID id);

  ResponseDataAPI findAll(Pageable pageable);

  ResponseEntity<ResponseDataAPI> registerUser(UserSignUpRequest userSignUpRequest);

  ResponseDataAPI changePassword(
      UUID userId, String oldPassword, ChangePasswordRequest changePasswordRequest);
}
