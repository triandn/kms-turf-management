package com.kms.cntt.mapper;

import com.kms.cntt.config.SpringMapStructConfig;
import com.kms.cntt.model.User;
import com.kms.cntt.payload.request.UserSignUpRequest;
import com.kms.cntt.payload.response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = SpringMapStructConfig.class)
public interface UserMapper {

  void toEntity(@MappingTarget User user, UserSignUpRequest userSignUpRequest);

  UserResponse toResponse(User user);
}
