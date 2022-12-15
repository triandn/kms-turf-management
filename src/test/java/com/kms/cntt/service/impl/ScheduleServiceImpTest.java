package com.kms.cntt.service.impl;

import com.kms.cntt.enums.Role;
import com.kms.cntt.enums.ScheduleStatus;
import com.kms.cntt.exception.BadRequestException;
import com.kms.cntt.exception.ForbiddenException;
import com.kms.cntt.model.Schedule;
import com.kms.cntt.model.Turf;
import com.kms.cntt.model.User;
import com.kms.cntt.payload.general.ResponseDataAPI;
import com.kms.cntt.payload.request.ScheduleUpdateRequest;
import com.kms.cntt.repository.ScheduleRepository;
import com.kms.cntt.repository.TurfRepository;
import com.kms.cntt.repository.UserRepository;
import com.kms.cntt.security.oauth2.UserPrincipal;
import com.kms.cntt.service.ScheduleService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Test for ScheduleServiceImp")
class ScheduleServiceImpTest {

    @Mock
    private ScheduleRepository scheduleRepository;
    @Mock
    private TurfRepository turfRepository;
    @Mock
    private UserRepository userRepository;

    @Mock
    private ScheduleService scheduleService;

    @InjectMocks
    private ScheduleServiceImp scheduleServiceImp;

    @Test
    @DisplayName("Should return schedule when the schedule is exist")
    void findByIdWhenScheduleIsExist() {
        UUID id = UUID.randomUUID();
        Schedule schedule = new Schedule();
        schedule.setId(id);
        schedule.setUser(new User());
        schedule.setTurf(new Turf());
        schedule.setTitle("title");
        schedule.setDescription("description");
        schedule.setCreatedAt(new Timestamp(new Date().getTime()));
        schedule.setStartTime(new Timestamp(new Date().getTime()));
        schedule.setEndTime(new Timestamp(new Date().getTime()));
        schedule.setStatus(ScheduleStatus.WAITING_FOR_ADMIN);
        schedule.setRequireReferee(true);

        when(scheduleRepository.findByIdAndDeletedAtIsNull(id)).thenReturn(Optional.of(schedule));

        Schedule result = scheduleServiceImp.findById(id);

        assertEquals(schedule, result);
    }
}