package com.kms.cntt.service.impl;

import com.kms.cntt.common.CommonFunction;
import com.kms.cntt.common.MessageConstant;
import com.kms.cntt.enums.NotificationType;
import com.kms.cntt.enums.Role;
import com.kms.cntt.enums.ScheduleStatus;
import com.kms.cntt.exception.BadRequestException;
import com.kms.cntt.exception.ForbiddenException;
import com.kms.cntt.exception.NotFoundException;
import com.kms.cntt.mapper.LocationTurfMapper;
import com.kms.cntt.mapper.TurfMapper;
import com.kms.cntt.mapper.UserMapper;
import com.kms.cntt.model.Schedule;
import com.kms.cntt.model.Turf;
import com.kms.cntt.model.User;
import com.kms.cntt.payload.general.PageInfo;
import com.kms.cntt.payload.general.ResponseDataAPI;
import com.kms.cntt.payload.request.ScheduleRequest;
import com.kms.cntt.payload.request.ScheduleUpdateRequest;
import com.kms.cntt.payload.response.ScheduleResponse;
import com.kms.cntt.repository.ScheduleRepository;
import com.kms.cntt.security.oauth2.UserPrincipal;
import com.kms.cntt.service.NotificationService;
import com.kms.cntt.service.ScheduleService;
import com.kms.cntt.service.TurfService;
import com.kms.cntt.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImp implements ScheduleService {
  private final ScheduleRepository scheduleRepository;
  private final TurfMapper turfMapper;
  private final LocationTurfMapper locationTurfMapper;
  private final UserService userService;
  private final TurfService turfService;
  private final UserMapper userMapper;
  private final NotificationService notificationService;

  @Override
  @Transactional
  public ResponseDataAPI createSchedule(UUID userId, ScheduleRequest scheduleRequest) {
    this.checkTimeSchedule(
        scheduleRequest.getTurfId(),
        scheduleRequest.getStartTime(),
        scheduleRequest.getEndTime(),
        UUID.fromString("400ed97f-dd6b-4a7f-b60e-ce90311bead8"));
    Schedule schedule = new Schedule();
    schedule.setUser(userService.findById(userId));
    Turf turf = turfService.findById(scheduleRequest.getTurfId());
    schedule.setTurf(turf);
    schedule.setTitle(scheduleRequest.getTitle());
    schedule.setDescription(scheduleRequest.getDescription());
    schedule.setCreatedAt(CommonFunction.getCurrentDateTime());
    schedule.setStartTime(scheduleRequest.getStartTime());
    schedule.setEndTime(scheduleRequest.getEndTime());
    schedule.setStatus(ScheduleStatus.WAITING_FOR_ADMIN);
    schedule.setRequireReferee(scheduleRequest.isRequireReferee());
    schedule.setIsPayment(false);
    long diff = scheduleRequest.getEndTime().getTime() - scheduleRequest.getStartTime().getTime();
    long diffHours = diff / (60 * 60 * 1000) % 24;
    schedule.setPrice(turf.getHourlyFee().multiply(new BigDecimal(Long.toString(diffHours))));
    Schedule result = scheduleRepository.save(schedule);
    notificationService.createNotificationMain(
        userId, result.getId(), NotificationType.USER_CREATE_SCHEDULE, "", userId, true);
    ScheduleResponse response =
        ScheduleResponse.createScheduleResponseWithFullInfo(
            result, turfMapper.toResponse(result.getTurf()));
    return ResponseDataAPI.successWithoutMeta(response);
  }

  @Override
  public ResponseDataAPI updateSchedule(
      UserPrincipal userPrincipal, UUID scheduleId, ScheduleUpdateRequest scheduleUpdateRequest) {
    Schedule schedule = this.findById(scheduleId);
    this.checkTimeSchedule(
        schedule.getTurf().getId(),
        scheduleUpdateRequest.getStartTime(),
        scheduleUpdateRequest.getEndTime(),
        scheduleId);
    if (!schedule.getUser().getId().equals(userPrincipal.getId())) {
      throw new ForbiddenException(MessageConstant.FORBIDDEN_ERROR);
    }
    if (!schedule.getStatus().equals(ScheduleStatus.WAITING_FOR_ADMIN)) {
      throw new ForbiddenException(MessageConstant.FORBIDDEN_ERROR);
    }
    schedule.setTitle(scheduleUpdateRequest.getTitle());
    schedule.setDescription(scheduleUpdateRequest.getDescription());
    schedule.setStartTime(scheduleUpdateRequest.getStartTime());
    schedule.setEndTime(scheduleUpdateRequest.getEndTime());
    schedule.setUpdatedAt(CommonFunction.getCurrentDateTime());
    schedule.setRequireReferee(scheduleUpdateRequest.isRequireReferee());
    Schedule result = scheduleRepository.save(schedule);
    ScheduleResponse response =
        ScheduleResponse.createScheduleResponseWithFullInfo(
            result, turfMapper.toResponse(result.getTurf()));
    return ResponseDataAPI.successWithoutMeta(response);
  }

  @Override
  public ResponseDataAPI refereeJoin(UUID refereeId, UUID scheduleId) {
    User user = userService.findById(refereeId);
    Schedule schedule = this.findById(scheduleId);
    if (!schedule.getStatus().equals(ScheduleStatus.WAITING_FOR_ADMIN)
        || !schedule.isRequireReferee()) {
      throw new ForbiddenException(MessageConstant.FORBIDDEN_ERROR);
    }
    if (schedule.getReferee() != null) {
      throw new BadRequestException(MessageConstant.OTHER_REFEREE_JOINED);
    }
    schedule.setReferee(user);
    Schedule result = scheduleRepository.save(schedule);
    notificationService.createNotificationMain(
        refereeId,
        result.getId(),
        NotificationType.REFEREE_JOIN_SCHEDULE,
        "",
        schedule.getUser().getId(),
        true);
    ScheduleResponse response =
        ScheduleResponse.createScheduleResponseWithFullInfo(
            result, turfMapper.toResponse(result.getTurf()));
    return ResponseDataAPI.successWithoutMeta(response);
  }

  @Override
  public ResponseDataAPI adminAccept(UUID scheduleId) {
    Schedule schedule = this.findById(scheduleId);
    if (!schedule.getStatus().equals(ScheduleStatus.WAITING_FOR_ADMIN)) {
      throw new ForbiddenException(MessageConstant.FORBIDDEN_ERROR);
    }
    schedule.setStatus(ScheduleStatus.ADMIN_ACCEPTED);
    Schedule result = scheduleRepository.save(schedule);
    notificationService.createNotificationMain(
        null,
        result.getId(),
        NotificationType.ADMIN_ACCEPT_SCHEDULE,
        "Admin accept",
        schedule.getUser().getId(),
        true);
    ScheduleResponse response =
        ScheduleResponse.createScheduleResponseWithFullInfo(
            result, turfMapper.toResponse(result.getTurf()));
    return ResponseDataAPI.successWithoutMeta(response);
  }

  @Override
  public ResponseDataAPI cancelSchedule(
      UserPrincipal userPrincipal, UUID scheduleId, String reasonCancel) {
    Schedule schedule = this.findById(scheduleId);
    schedule.setReasonCancel(reasonCancel);
    if (!schedule.getStatus().equals(ScheduleStatus.WAITING_FOR_ADMIN)) {
      throw new ForbiddenException(MessageConstant.FORBIDDEN_ERROR);
    }
    if (userPrincipal.getRole().equals(Role.ROLE_USER)) {
      if (!schedule.getUser().getId().equals(userPrincipal.getId())) {
        throw new ForbiddenException(MessageConstant.FORBIDDEN_ERROR);
      }
      schedule.setStatus(ScheduleStatus.USER_CANCELED);
    } else if (userPrincipal.getRole().equals(Role.ROLE_ADMIN)) {
      schedule.setStatus(ScheduleStatus.ADMIN_CANCELED);
    }
    Schedule result = scheduleRepository.save(schedule);
    notificationService.createNotificationMain(
        null,
        result.getId(),
        NotificationType.ADMIN_ACCEPT_SCHEDULE,
        "Admin cancel",
        schedule.getUser().getId(),
        true);
    ScheduleResponse response =
        ScheduleResponse.createScheduleResponseWithFullInfo(
            result, turfMapper.toResponse(result.getTurf()));
    return ResponseDataAPI.successWithoutMeta(response);
  }

  @Override
  public Schedule findById(UUID id) {
    return scheduleRepository
        .findByIdAndDeletedAtIsNull(id)
        .orElseThrow(() -> new NotFoundException(MessageConstant.SCHEDULE_NOT_FOUND));
  }

  @Override
  public ResponseDataAPI findScheduleOfTurfByAdmin(
      Pageable pageable, UUID turfId, String startDay, String endDay) throws ParseException {
    Page<Schedule> page =
        scheduleRepository.findScheduleByTurfId(
            pageable,
            turfId,
            CommonFunction.yyyyMMddHHmmSSFormat(startDay),
            CommonFunction.yyyyMMddHHmmSSFormat(endDay),
            List.of(
                new ScheduleStatus[] {
                  ScheduleStatus.WAITING_FOR_ADMIN,
                  ScheduleStatus.ADMIN_ACCEPTED,
                  ScheduleStatus.ADMIN_CANCELED,
                  ScheduleStatus.USER_CANCELED
                }));
    var pageInfo =
        new PageInfo(pageable.getPageNumber() + 1, page.getTotalPages(), page.getTotalElements());
    List<ScheduleResponse> scheduleResponses = new ArrayList<>();
    for (Schedule schedule : page.getContent()) {
      scheduleResponses.add(ScheduleResponse.createScheduleResponseWithMainInfo(schedule));
    }
    return ResponseDataAPI.success(scheduleResponses, pageInfo);
  }

  @Override
  public ResponseDataAPI findScheduleByTurf(
      Pageable pageable, UUID turfId, String startDay, String endDay) throws ParseException {
    Page<Schedule> page =
        scheduleRepository.findScheduleByTurfId(
            pageable,
            turfId,
            CommonFunction.yyyyMMddHHmmSSFormat(startDay),
            CommonFunction.yyyyMMddHHmmSSFormat(endDay),
            List.of(
                new ScheduleStatus[] {
                  ScheduleStatus.WAITING_FOR_ADMIN, ScheduleStatus.ADMIN_ACCEPTED
                }));
    var pageInfo =
        new PageInfo(pageable.getPageNumber() + 1, page.getTotalPages(), page.getTotalElements());
    List<ScheduleResponse> scheduleResponses = new ArrayList<>();
    for (Schedule schedule : page.getContent()) {
      scheduleResponses.add(ScheduleResponse.createScheduleResponseWithMainInfo(schedule));
    }
    return ResponseDataAPI.success(scheduleResponses, pageInfo);
  }

  @Override
  public ResponseDataAPI findScheduleOfTurfByReferee(
      Pageable pageable, UUID turfId, String startDay, String endDay) throws ParseException {
    Page<Schedule> page =
        scheduleRepository.findScheduleOfTurfByReferee(
            pageable,
            turfId,
            CommonFunction.yyyyMMddHHmmSSFormat(startDay),
            CommonFunction.yyyyMMddHHmmSSFormat(endDay),
            List.of(new ScheduleStatus[] {ScheduleStatus.WAITING_FOR_ADMIN}));
    var pageInfo =
        new PageInfo(pageable.getPageNumber() + 1, page.getTotalPages(), page.getTotalElements());
    List<ScheduleResponse> scheduleResponses = new ArrayList<>();
    for (Schedule schedule : page.getContent()) {
      scheduleResponses.add(ScheduleResponse.createScheduleResponseWithMainInfo(schedule));
    }
    return ResponseDataAPI.success(scheduleResponses, pageInfo);
  }

  @Override
  public ResponseDataAPI findAllByUser(Pageable pageable, UUID userId) {
    Page<Schedule> page = scheduleRepository.findAllByUserId(pageable, userId);
    var pageInfo =
        new PageInfo(pageable.getPageNumber() + 1, page.getTotalPages(), page.getTotalElements());
    List<ScheduleResponse> scheduleResponses = new ArrayList<>();
    for (Schedule schedule : page.getContent()) {
      scheduleResponses.add(
          ScheduleResponse.createScheduleResponseWithFullInfo(
              schedule,
              turfMapper.toResponse(schedule.getTurf()),
              locationTurfMapper.toResponse(schedule.getTurf().getLocationTurf())));
    }
    return ResponseDataAPI.success(scheduleResponses, pageInfo);
  }

  @Override
  public ResponseDataAPI findAllByAdmin(Pageable pageable) {
    Page<Schedule> page = scheduleRepository.findAll(pageable);
    var pageInfo =
        new PageInfo(pageable.getPageNumber() + 1, page.getTotalPages(), page.getTotalElements());
    List<ScheduleResponse> scheduleResponses = new ArrayList<>();
    for (Schedule schedule : page.getContent()) {
      scheduleResponses.add(
          ScheduleResponse.createScheduleResponseWithFullInfoAdmin(
              schedule,
              userMapper.toResponse(schedule.getUser()),
              turfMapper.toResponse(schedule.getTurf()),
              locationTurfMapper.toResponse(schedule.getTurf().getLocationTurf()),
              userMapper.toResponse(schedule.getReferee())));
    }
    return ResponseDataAPI.success(scheduleResponses, pageInfo);
  }

  @Override
  public ResponseDataAPI findAllByReferee(Pageable pageable, UUID refereeId) {
    Page<Schedule> page = scheduleRepository.findAllByRefereeId(pageable, refereeId);
    var pageInfo =
        new PageInfo(pageable.getPageNumber() + 1, page.getTotalPages(), page.getTotalElements());
    List<ScheduleResponse> scheduleResponses = new ArrayList<>();
    for (Schedule schedule : page.getContent()) {
      scheduleResponses.add(
          ScheduleResponse.createScheduleResponseWithFullInfo(
              schedule,
              turfMapper.toResponse(schedule.getTurf()),
              locationTurfMapper.toResponse(schedule.getTurf().getLocationTurf())));
    }
    return ResponseDataAPI.success(scheduleResponses, pageInfo);
  }

  private void checkTimeSchedule(UUID turfId, Date startTime, Date endTime, UUID scheduleId) {
    if (startTime.compareTo(new Date()) < 0 || startTime.compareTo(endTime) >= 0) {
      throw new BadRequestException(MessageConstant.INCORRECT_TIME);
    }
    if (scheduleRepository.checkExistScheduleOfTurf(
        turfId, new Timestamp(startTime.getTime()), new Timestamp(endTime.getTime()), scheduleId)) {
      throw new BadRequestException(MessageConstant.TURF_NO_AVAILABLE);
    }
  }
}
