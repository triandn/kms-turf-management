package com.kms.cntt.repository;

import com.kms.cntt.enums.ScheduleStatus;
import com.kms.cntt.model.Schedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, UUID> {
  Optional<Schedule> findByIdAndDeletedAtIsNull(UUID id);

  @Query(
      value =
          "select "
              + "    s "
              + "from "
              + "    Schedule s "
              + "where "
              + "    s.turf.id = :turfId "
              + "    and s.status in :statuses "
              + "    and ( (s.startTime >= :startDay "
              + "        and s.startTime > :endDay) "
              + "    or (s.endTime > :startDay "
              + "        and s.endTime <= :endDay))")
  Page<Schedule> findScheduleByTurfId(
      Pageable pageable,
      UUID turfId,
      Timestamp startDay,
      Timestamp endDay,
      List<ScheduleStatus> statuses);

  Page<Schedule> findAllByUserId(Pageable pageable, UUID userId);

  Page<Schedule> findAllByRefereeId(Pageable pageable, UUID refereeId);

  @Query(
      value =
          "select "
              + "    (exists( "
              + "    select "
              + "        * "
              + "    from "
              + "        schedules s "
              + "    where "
              + "        s.turf_id = :turfId "
              + "        and s.id != :scheduleId "
              + "        and s.status in ('WAITING_FOR_ADMIN', 'ADMIN_ACCEPTED') "
              + "        and "
              + "        ( "
              + "        (start_time <= :startTime "
              + "            and end_time > :startTime) "
              + "        or (start_time < :endTime "
              + "            and end_time >= :endTime)) "
              + ")) ",
      nativeQuery = true)
  boolean checkExistScheduleOfTurf(
      UUID turfId, Timestamp startTime, Timestamp endTime, UUID scheduleId);

  @Query(
      value =
          "select "
              + "    s "
              + "from "
              + "    Schedule s "
              + "where "
              + "    s.turf.id = :turfId "
              + "    and s.status in :statuses "
              + "    and s.referee is null "
              + "    and s.requireReferee = true "
              + "    and ( (s.startTime >= :startDay "
              + "        and s.startTime > :endDay) "
              + "    or (s.endTime > :startDay "
              + "        and s.endTime <= :endDay))")
  Page<Schedule> findScheduleOfTurfByReferee(
      Pageable pageable,
      UUID turfId,
      Timestamp startDay,
      Timestamp endDay,
      List<ScheduleStatus> statuses);
}
