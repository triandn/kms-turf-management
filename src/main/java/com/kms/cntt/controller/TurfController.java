package com.kms.cntt.controller;

import com.kms.cntt.mapper.TurfMapper;
import com.kms.cntt.model.Turf;
import com.kms.cntt.payload.general.ResponseDataAPI;
import com.kms.cntt.payload.request.TurfRequest;
import com.kms.cntt.payload.request.TurfUpdateRequest;
import com.kms.cntt.security.annotations.CurrentUser;
import com.kms.cntt.security.oauth2.UserPrincipal;
import com.kms.cntt.service.TurfService;
import com.kms.cntt.utils.PagingUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/turfs")
@RequiredArgsConstructor
public class TurfController {
  private final TurfService turfService;

  private final TurfMapper turfMapper;

  @PostMapping()
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public ResponseEntity<ResponseDataAPI> createTurf(
      @Valid @RequestBody TurfRequest turfRequest, @CurrentUser UserPrincipal userPrincipal) {
    return ResponseEntity.ok(turfService.createTurf(turfRequest));
  }

  @PatchMapping("/{id}")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public ResponseEntity<ResponseDataAPI> updateTurf(
      @PathVariable UUID id, @Valid @RequestBody TurfUpdateRequest turfUpdateRequest) {
    return ResponseEntity.ok(turfService.updateTurf(id, turfUpdateRequest));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public ResponseEntity<ResponseDataAPI> deleteTurf(@PathVariable UUID id) {
    return ResponseEntity.ok(turfService.deleteTurf(id));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ResponseDataAPI> getTurf(@PathVariable UUID id) {
    Turf Turf = turfService.findById(id);
    return ResponseEntity.ok(ResponseDataAPI.successWithoutMeta(turfMapper.toResponse(Turf)));
  }

  @GetMapping("/location_turfs/{id}")
  public ResponseEntity<ResponseDataAPI> getAllTurf(
      @RequestParam(value = "page", defaultValue = "1") int page,
      @RequestParam(value = "paging", defaultValue = "10") int paging,
      @RequestParam(value = "sort", defaultValue = "created_at") String sortBy,
      @RequestParam(value = "order", defaultValue = "desc") String order,
      @PathVariable UUID id) {
    Pageable pageable = PagingUtils.makePageRequest(sortBy, order, page, paging);
    return ResponseEntity.ok(turfService.findAllByLocationTurf(pageable, id));
  }
}
