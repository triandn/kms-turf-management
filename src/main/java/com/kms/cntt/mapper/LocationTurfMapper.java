package com.kms.cntt.mapper;

import com.kms.cntt.config.SpringMapStructConfig;
import com.kms.cntt.model.LocationTurf;
import com.kms.cntt.payload.request.LocationTurfRequest;
import com.kms.cntt.payload.response.LocationTurfResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = SpringMapStructConfig.class)
public interface LocationTurfMapper {
  void toEntity(@MappingTarget LocationTurf locationTurf, LocationTurfRequest locationTurfRequest);

  @Mapping(source = "locationTurf.user.id", target = "userId")
  LocationTurfResponse toResponse(LocationTurf locationTurf);
}
