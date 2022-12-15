package com.kms.cntt.controller;

import com.kms.cntt.mapper.LocationTurfMapper;
import com.kms.cntt.mapper.TurfMapper;
import com.kms.cntt.mapper.UserMapper;
import com.kms.cntt.model.Schedule;
import com.kms.cntt.payload.general.ResponseDataAPI;
import com.kms.cntt.payload.request.BaseReasonRequest;
import com.kms.cntt.payload.request.ScheduleRequest;
import com.kms.cntt.payload.request.ScheduleUpdateRequest;
import com.kms.cntt.payload.response.ScheduleResponse;
import com.kms.cntt.security.annotations.CurrentUser;
import com.kms.cntt.security.oauth2.UserPrincipal;
import com.kms.cntt.service.ScheduleService;
import com.kms.cntt.utils.PagingUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.ParseException;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ScheduleController {
  private final ScheduleService scheduleService;
  private final TurfMapper turfMapper;
  private final LocationTurfMapper locationTurfMapper;
  private final UserMapper userMapper;

  @PostMapping("/schedules")
  @PreAuthorize("hasRole('ROLE_USER')")
  public ResponseEntity<ResponseDataAPI> createSchedule(
      @Valid @RequestBody ScheduleRequest ScheduleRequest,
      @CurrentUser UserPrincipal userPrincipal) {
    return ResponseEntity.ok(
        scheduleService.createSchedule(userPrincipal.getId(), ScheduleRequest));
  }

  @PatchMapping("/schedules/{id}")
  @PreAuthorize("hasRole('ROLE_USER')")
  public ResponseEntity<ResponseDataAPI> updateSchedule(
      @PathVariable UUID id,
      @Valid @RequestBody ScheduleUpdateRequest scheduleUpdateRequest,
      @CurrentUser UserPrincipal userPrincipal) {
    return ResponseEntity.ok(
        scheduleService.updateSchedule(userPrincipal, id, scheduleUpdateRequest));
  }

  @PatchMapping("/schedules/{id}/referee_join")
  @PreAuthorize("hasRole('ROLE_REFEREE')")
  public ResponseEntity<ResponseDataAPI> refereeJoinSchedule(
      @PathVariable UUID id, @CurrentUser UserPrincipal userPrincipal) {
    return ResponseEntity.ok(scheduleService.refereeJoin(userPrincipal.getId(), id));
  }

  @PatchMapping("/schedules/{id}/accept")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public ResponseEntity<ResponseDataAPI> adminAcceptSchedule(@PathVariable UUID id) {
    return ResponseEntity.ok(scheduleService.adminAccept(id));
  }

  @PatchMapping("/schedules/{id}/cancel")
  @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
  public ResponseEntity<ResponseDataAPI> cancelSchedule(
      @PathVariable UUID id,
      @CurrentUser UserPrincipal userPrincipal,
      @Valid @RequestBody BaseReasonRequest baseReasonRequest) {
    return ResponseEntity.ok(
        scheduleService.cancelSchedule(userPrincipal, id, baseReasonRequest.getReasonCancel()));
  }

  @GetMapping("/schedules/{id}")
  public ResponseEntity<ResponseDataAPI> getSchedule(@PathVariable UUID id) {
    Schedule schedule = scheduleService.findById(id);
    return ResponseEntity.ok(
        ResponseDataAPI.successWithoutMeta(
            ScheduleResponse.createScheduleResponseWithFullInfoAdmin(
                schedule,
                userMapper.toResponse(schedule.getUser()),
                turfMapper.toResponse(schedule.getTurf()),
                locationTurfMapper.toResponse(schedule.getTurf().getLocationTurf()),
                userMapper.toResponse(schedule.getReferee()))));
  }

  @GetMapping("/admins/schedules/turfs/{id}")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public ResponseEntity<ResponseDataAPI> getAllScheduleOfTurfByAdmin(
      @RequestParam(value = "page", defaultValue = "1") int page,
      @RequestParam(value = "paging", defaultValue = "10") int paging,
      @RequestParam(value = "sort", defaultValue = "created_at") String sortBy,
      @RequestParam(value = "order", defaultValue = "desc") String order,
      @RequestParam(value = "start_day", defaultValue = "2022-11-2 00:00:00") String startDay,
      @RequestParam(value = "end_day", defaultValue = "2122-11-2 00:00:00") String endDay,
      @PathVariable UUID id)
      throws ParseException {
    Pageable pageable = PagingUtils.makePageRequest(sortBy, order, page, paging);
    return ResponseEntity.ok(
        scheduleService.findScheduleOfTurfByAdmin(pageable, id, startDay, endDay));
  }

  @GetMapping("/schedules/turfs/{id}")
  @PreAuthorize("hasRole('ROLE_USER')")
  public ResponseEntity<ResponseDataAPI> getAllScheduleByTurf(
      @RequestParam(value = "page", defaultValue = "1") int page,
      @RequestParam(value = "paging", defaultValue = "10") int paging,
      @RequestParam(value = "sort", defaultValue = "created_at") String sortBy,
      @RequestParam(value = "order", defaultValue = "desc") String order,
      @RequestParam(value = "start_day", defaultValue = "2022-11-2 00:00:00") String startDay,
      @RequestParam(value = "end_day", defaultValue = "2122-11-2 00:00:00") String endDay,
      @PathVariable UUID id)
      throws ParseException {
    Pageable pageable = PagingUtils.makePageRequest(sortBy, order, page, paging);
    return ResponseEntity.ok(scheduleService.findScheduleByTurf(pageable, id, startDay, endDay));
  }

  @GetMapping("/referees/schedules/turfs/{id}")
  @PreAuthorize("hasRole('ROLE_REFEREE')")
  public ResponseEntity<ResponseDataAPI> getAllScheduleOfTurfByReferee(
      @RequestParam(value = "page", defaultValue = "1") int page,
      @RequestParam(value = "paging", defaultValue = "10") int paging,
      @RequestParam(value = "sort", defaultValue = "created_at") String sortBy,
      @RequestParam(value = "order", defaultValue = "desc") String order,
      @RequestParam(value = "start_day", defaultValue = "2022-11-2 00:00:00") String startDay,
      @RequestParam(value = "end_day", defaultValue = "2122-11-2 00:00:00") String endDay,
      @PathVariable UUID id)
      throws ParseException {
    Pageable pageable = PagingUtils.makePageRequest(sortBy, order, page, paging);
    return ResponseEntity.ok(
        scheduleService.findScheduleOfTurfByReferee(pageable, id, startDay, endDay));
  }

  @GetMapping("/users/schedules")
  @PreAuthorize("hasRole('ROLE_USER')")
  public ResponseEntity<ResponseDataAPI> getAllScheduleByUser(
      @RequestParam(value = "page", defaultValue = "1") int page,
      @RequestParam(value = "paging", defaultValue = "10") int paging,
      @RequestParam(value = "sort", defaultValue = "created_at") String sortBy,
      @RequestParam(value = "order", defaultValue = "desc") String order,
      @CurrentUser UserPrincipal userPrincipal) {
    Pageable pageable = PagingUtils.makePageRequest(sortBy, order, page, paging);
    return ResponseEntity.ok(scheduleService.findAllByUser(pageable, userPrincipal.getId()));
  }

  @GetMapping("/admins/schedules")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public ResponseEntity<ResponseDataAPI> getAllScheduleByAdmin(
      @RequestParam(value = "page", defaultValue = "1") int page,
      @RequestParam(value = "paging", defaultValue = "10") int paging,
      @RequestParam(value = "sort", defaultValue = "created_at") String sortBy,
      @RequestParam(value = "order", defaultValue = "desc") String order) {
    Pageable pageable = PagingUtils.makePageRequest(sortBy, order, page, paging);
    return ResponseEntity.ok(scheduleService.findAllByAdmin(pageable));
  }

  @GetMapping("/referees/schedules")
  @PreAuthorize("hasRole('ROLE_REFEREE')")
  public ResponseEntity<ResponseDataAPI> getAllScheduleByReferee(
      @RequestParam(value = "page", defaultValue = "1") int page,
      @RequestParam(value = "paging", defaultValue = "10") int paging,
      @RequestParam(value = "sort", defaultValue = "created_at") String sortBy,
      @RequestParam(value = "order", defaultValue = "desc") String order,
      @CurrentUser UserPrincipal userPrincipal) {
    Pageable pageable = PagingUtils.makePageRequest(sortBy, order, page, paging);
    return ResponseEntity.ok(scheduleService.findAllByReferee(pageable, userPrincipal.getId()));
  }
}
