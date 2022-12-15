package com.kms.cntt.service;

import com.kms.cntt.model.Schedule;
import com.kms.cntt.payload.general.ResponseDataAPI;
import com.kms.cntt.payload.request.ScheduleRequest;
import com.kms.cntt.payload.request.ScheduleUpdateRequest;
import com.kms.cntt.security.oauth2.UserPrincipal;
import org.springframework.data.domain.Pageable;

import java.text.ParseException;
import java.util.UUID;

public interface ScheduleService {

  ResponseDataAPI createSchedule(UUID userId, ScheduleRequest scheduleRequest);

  ResponseDataAPI updateSchedule(
      UserPrincipal userPrincipal, UUID scheduleId, ScheduleUpdateRequest scheduleUpdateRequest);

  ResponseDataAPI refereeJoin(UUID refereeId, UUID scheduleId);

  ResponseDataAPI adminAccept(UUID scheduleId);

  ResponseDataAPI cancelSchedule(UserPrincipal userPrincipal, UUID scheduleId, String reasonCancel);

  Schedule findById(UUID id);

  ResponseDataAPI findScheduleOfTurfByAdmin(
      Pageable pageable, UUID turfId, String startDay, String endDay) throws ParseException;

  ResponseDataAPI findScheduleByTurf(Pageable pageable, UUID turfId, String startDay, String endDay)
      throws ParseException;

  ResponseDataAPI findScheduleOfTurfByReferee(
          Pageable pageable, UUID turfId, String startDay, String endDay) throws ParseException;

  ResponseDataAPI findAllByUser(Pageable pageable, UUID userId);

  ResponseDataAPI findAllByAdmin(Pageable pageable);

  ResponseDataAPI findAllByReferee(Pageable pageable, UUID refereeId);

}
