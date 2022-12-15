package com.kms.cntt.mapper;

import com.kms.cntt.config.SpringMapStructConfig;
import com.kms.cntt.model.Schedule;
import com.kms.cntt.payload.request.ScheduleRequest;
import com.kms.cntt.payload.response.ScheduleResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = SpringMapStructConfig.class)
public interface ScheduleMapper {

  void toEntity(@MappingTarget Schedule schedule, ScheduleRequest scheduleRequest);

}
