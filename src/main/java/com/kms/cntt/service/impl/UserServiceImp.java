package com.kms.cntt.service.impl;

import com.kms.cntt.common.CommonFunction;
import com.kms.cntt.common.MessageConstant;
import com.kms.cntt.enums.Role;
import com.kms.cntt.exception.BadRequestException;
import com.kms.cntt.exception.InternalServerException;
import com.kms.cntt.exception.NotFoundException;
import com.kms.cntt.mapper.UserMapper;
import com.kms.cntt.model.User;
import com.kms.cntt.payload.general.PageInfo;
import com.kms.cntt.payload.general.ResponseDataAPI;
import com.kms.cntt.payload.request.ChangePasswordRequest;
import com.kms.cntt.payload.request.UserSignUpRequest;
import com.kms.cntt.payload.response.UserResponse;
import com.kms.cntt.repository.UserRepository;
import com.kms.cntt.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImp implements UserService {
  private final UserRepository userRepository;

  private final PasswordEncoder passwordEncoder;

  private final UserMapper userMapper;

  @Override
  public User findById(UUID id) {
    Optional<User> user = userRepository.findById(id);
    return user.orElseThrow(() -> new NotFoundException(MessageConstant.USER_NOT_FOUND));
  }

  @Override
  public ResponseDataAPI findAll(Pageable pageable) {
    Page<User> page = userRepository.findAll(pageable);
    var pageInfo =
        new PageInfo(pageable.getPageNumber() + 1, page.getTotalPages(), page.getTotalElements());
    List<UserResponse> userResponses =
        page.getContent().stream().map(userMapper::toResponse).collect(Collectors.toList());
    return ResponseDataAPI.success(userResponses, pageInfo);
  }

  @Override
  public ResponseEntity<ResponseDataAPI> registerUser(UserSignUpRequest userSignUpRequest) {
    if (!userSignUpRequest.getPassword().equals(userSignUpRequest.getPasswordConfirmation())) {
      throw new BadRequestException(MessageConstant.PASSWORD_FIELDS_MUST_MATCH);
    }

    User user =
        this.toUserEntity(
            userSignUpRequest.getUsername(),
            userSignUpRequest.getPassword(),
            userSignUpRequest.getFullName(),
            userSignUpRequest.getPhoneNumber(),
            userSignUpRequest.getRole());

    if (userRepository.existsByUsername(userSignUpRequest.getUsername().toLowerCase())) {
      throw new BadRequestException(MessageConstant.REGISTER_USERNAME_ALREADY_IN_USE);
    }

    userRepository.save(user);
    return ResponseEntity.ok(ResponseDataAPI.success(null, null));
  }

  @Override
  public ResponseDataAPI changePassword(
      UUID userId, String oldPassword, ChangePasswordRequest changePasswordRequest) {
    try {
      if (!BCrypt.checkpw(changePasswordRequest.getOldPassword(), oldPassword)) {
        throw new BadRequestException(MessageConstant.OLD_PASSWORD_NOT_CORRECT);
      }
      if (!changePasswordRequest
          .getConfirmNewPassword()
          .equals(changePasswordRequest.getNewPassword())) {
        throw new BadRequestException(MessageConstant.PASSWORD_FIELDS_MUST_MATCH);
      }
      if (changePasswordRequest.getOldPassword().equals(changePasswordRequest.getNewPassword())) {
        throw new BadRequestException(MessageConstant.NEW_PASSWORD_NOT_SAME_OLD_PASSWORD);
      }
      User user = findById(userId);
      user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
      userRepository.save(user);
      return ResponseDataAPI.success(null, null);
    } catch (BadRequestException e) {
      throw e;
    } catch (Exception e) {
      throw new InternalServerException(MessageConstant.CHANGE_PASSWORD_FAIL);
    }
  }

  private User toUserEntity(
      String username, String password, String fullName, String phoneNumber, String role) {
    User user = new User();
    user.setCreatedAt(CommonFunction.getCurrentDateTime());
    user.setUsername(username);
    user.setPassword(passwordEncoder.encode(password));
    user.setFullName(fullName);
    user.setPhoneNumber(phoneNumber);
    user.setRole(Role.valueOf(role));
    return user;
  }
}
