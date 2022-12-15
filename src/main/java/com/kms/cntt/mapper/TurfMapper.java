package com.kms.cntt.mapper;

import com.kms.cntt.config.SpringMapStructConfig;
import com.kms.cntt.model.Turf;
import com.kms.cntt.payload.request.TurfRequest;
import com.kms.cntt.payload.response.TurfResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = SpringMapStructConfig.class)
public interface TurfMapper {
  void toEntity(@MappingTarget Turf turf, TurfRequest turfRequest);

  @Mapping(source = "turf.locationTurf.id", target = "locationTurfId")
  TurfResponse toResponse(Turf turf);
}
