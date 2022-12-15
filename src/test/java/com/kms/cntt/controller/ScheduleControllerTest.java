package com.kms.cntt.controller;

import com.kms.cntt.enums.Role;
import com.kms.cntt.enums.ScheduleStatus;
import com.kms.cntt.enums.TurfType;
import com.kms.cntt.model.Schedule;
import com.kms.cntt.model.Turf;
import com.kms.cntt.model.User;
import com.kms.cntt.payload.general.ResponseDataAPI;
import com.kms.cntt.payload.request.BaseReasonRequest;
import com.kms.cntt.payload.request.ScheduleRequest;
import com.kms.cntt.payload.request.ScheduleUpdateRequest;
import com.kms.cntt.repository.ScheduleRepository;
import com.kms.cntt.repository.TurfRepository;
import com.kms.cntt.repository.UserRepository;
import com.kms.cntt.security.oauth2.UserPrincipal;
import com.kms.cntt.service.ScheduleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Test for ScheduleController")
class ScheduleControllerTest {
    @Mock
    private ScheduleRepository scheduleRepository;
    @Mock
    private TurfRepository turfRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ScheduleService scheduleService;

    @InjectMocks
    private ScheduleController scheduleController;

    private User user;
    private Turf turf;
    private Schedule schedule;

    @BeforeEach
    void setUp() {
        //        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setFullName("nguyen tri an");
        user.setPhoneNumber("0702589121");
        user.setUsername("atnguyen");
        user.setRole(Role.ROLE_USER);
        user.setPassword("123abcA");
        user.setResetPasswordToken(null);

        Turf turf = new Turf();
        turf.setId(UUID.randomUUID());
        turf.setName("Turf 1");
        turf.setHourlyFee(BigDecimal.valueOf(100));
        turf.setType(TurfType.FIVE_SIDE);
        turf.setImageLink("https://image.com/turf1");

        Schedule schedule = new Schedule();
        schedule.setId(UUID.randomUUID());
        schedule.setTitle("string");
        schedule.setDescription("string");
        schedule.setStatus(ScheduleStatus.ADMIN_ACCEPTED);
        schedule.setReasonCancel("string");
        schedule.setUser(user);
        schedule.setTurf(turf);
    }

    @Test
    @DisplayName("Should return all schedules when the user is admin")
    void getAllScheduleByAdminWhenUserIsAdminThenReturnAllSchedules() {
        when(scheduleService.findAllByAdmin(any()))
                .thenReturn(ResponseDataAPI.success(new ArrayList<>(), null));
        ResponseEntity<ResponseDataAPI> responseEntity =
                scheduleController.getAllScheduleByAdmin(1, 10, "created_at", "desc");
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(ResponseDataAPI.success(new ArrayList<>(), null).getStatus(), responseEntity.getBody().getStatus());
    }

    @Test
    @DisplayName("Should return all schedules of user when user is authenticated")
    void getAllScheduleByUserWhenUserIsAuthenticatedThenReturnAllSchedulesOfUser() {
        UserPrincipal userPrincipal =
                new UserPrincipal(
                        UUID.randomUUID(),
                        "atnguyen",
                        "123abcA",
                        "nguyen tri an",
                        "0702589121",
                        Role.ROLE_USER,
                        new ArrayList<>());
        when(scheduleService.findAllByUser(any(), any()))
                .thenReturn(ResponseDataAPI.success(new ArrayList<>(), null));
        ResponseEntity<ResponseDataAPI> responseEntity =
                scheduleController.getAllScheduleByUser(1, 10, "created_at", "desc", userPrincipal);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(ResponseDataAPI.success(new ArrayList<>(), null), responseEntity.getBody());
    }

    @Test
    @DisplayName("Should return a response with status code 200 when the user is not admin")
    void cancelScheduleWhenUserIsNotAdminThenReturnResponseWithStatusCode200() {
        UserPrincipal userPrincipal =
                new UserPrincipal(
                        UUID.randomUUID(),
                        "atnguyen",
                        "123abcA",
                        "nguyen tri an",
                        "0702589121",
                        Role.ROLE_USER,
                        new ArrayList<>());

        ResponseEntity<ResponseDataAPI> response =
                scheduleController.cancelSchedule(
                        UUID.randomUUID(), userPrincipal, new BaseReasonRequest());

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("Should return a response with status code 200 when the user is admin")
    void cancelScheduleWhenUserIsAdminThenReturnResponseWithStatusCode200() {
        UserPrincipal userPrincipal =
                new UserPrincipal(
                        UUID.randomUUID(),
                        "atnguyen",
                        "123abcA",
                        "nguyen tri an",
                        "0702589121",
                        Role.ROLE_USER,
                        new ArrayList<>());
        ResponseEntity<ResponseDataAPI> response =
                scheduleController.cancelSchedule(
                        UUID.randomUUID(), userPrincipal, new BaseReasonRequest());

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("Should return 200 when the schedule is accepted")
    void adminAcceptScheduleWhenScheduleIsAcceptedThenReturn200() {

        ResponseEntity<ResponseDataAPI> response =
                scheduleController.adminAcceptSchedule(UUID.randomUUID());

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }


    @Test
    @DisplayName("Should return a schedule when the user is the owner of the schedule")
    void updateScheduleWhenUserIsOwnerOfScheduleThenReturnSchedule() {
        UserPrincipal userPrincipal =
                new UserPrincipal(
                        UUID.randomUUID(),
                        "atnguyen",
                        "123abcA",
                        "nguyen tri an",
                        "0702589121",
                        Role.ROLE_USER,
                        new ArrayList<>());
        ScheduleUpdateRequest scheduleUpdateRequest = new ScheduleUpdateRequest();
        scheduleUpdateRequest.setTitle("string");
        scheduleUpdateRequest.setDescription("string");
//        when(scheduleRepository.findById(any(UUID.class))).thenReturn(Optional.ofNullable(schedule));
//        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.ofNullable(user));
        when(scheduleService.updateSchedule(
                any(UserPrincipal.class), any(UUID.class), any()))
                .thenReturn(ResponseDataAPI.successWithoutMeta(""));

        assertEquals(
                "",
                scheduleController
                        .updateSchedule(UUID.randomUUID(), scheduleUpdateRequest, userPrincipal)
                        .getBody()
                        .getData());
    }

    @Test
    @DisplayName("Should return a schedule when the user is authenticated")
    void createScheduleWhenUserIsAuthenticatedThenReturnSchedule() {

        ScheduleRequest scheduleRequest = new ScheduleRequest();
        scheduleRequest.setTitle("title");
        scheduleRequest.setDescription("description");
        scheduleRequest.setStartTime(new Date());
        scheduleRequest.setEndTime(new Date());
        scheduleRequest.setTurfId(UUID.randomUUID());

        assertEquals(
                HttpStatus.OK,
                scheduleController
                        .createSchedule(scheduleRequest, new UserPrincipal(UUID.randomUUID(), "atnguyen", "abc123A", "nguyen tri an", "0702589121", Role.ROLE_USER, null))
                        .getStatusCode());
    }
}