package com.kms.cntt.controller;

import com.kms.cntt.mapper.LocationTurfMapper;
import com.kms.cntt.model.LocationTurf;
import com.kms.cntt.payload.general.ResponseDataAPI;
import com.kms.cntt.payload.request.LocationTurfRequest;
import com.kms.cntt.security.annotations.CurrentUser;
import com.kms.cntt.security.oauth2.UserPrincipal;
import com.kms.cntt.service.LocationTurfService;
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
@RequestMapping("/api/v1/location_turfs")
@RequiredArgsConstructor
public class LocationTurfController {
  private final LocationTurfService locationTurfService;

  private final LocationTurfMapper locationTurfMapper;

  @PostMapping()
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public ResponseEntity<ResponseDataAPI> createLocationTurf(
      @Valid @RequestBody LocationTurfRequest locationTurfRequest,
      @CurrentUser UserPrincipal userPrincipal) {
    return ResponseEntity.ok(
        locationTurfService.createLocationTurf(userPrincipal.getId(), locationTurfRequest));
  }

  @PatchMapping("/{id}")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public ResponseEntity<ResponseDataAPI> updateLocationTurf(
      @PathVariable UUID id, @Valid @RequestBody LocationTurfRequest locationTurfRequest) {
    return ResponseEntity.ok(locationTurfService.updateLocationTurf(id, locationTurfRequest));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public ResponseEntity<ResponseDataAPI> deleteLocationTurf(@PathVariable UUID id) {
    return ResponseEntity.ok(locationTurfService.deleteLocationTurf(id));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ResponseDataAPI> getLocationTurf(@PathVariable UUID id) {
    LocationTurf locationTurf = locationTurfService.findById(id);
    return ResponseEntity.ok(
        ResponseDataAPI.successWithoutMeta(locationTurfMapper.toResponse(locationTurf)));
  }

  @GetMapping()
  public ResponseEntity<ResponseDataAPI> getAllLocationTurf(
      @RequestParam(value = "page", defaultValue = "1") int page,
      @RequestParam(value = "paging", defaultValue = "10") int paging,
      @RequestParam(value = "sort", defaultValue = "created_at") String sortBy,
      @RequestParam(value = "order", defaultValue = "desc") String order) {
    Pageable pageable = PagingUtils.makePageRequest(sortBy, order, page, paging);
    return ResponseEntity.ok(locationTurfService.findAll(pageable));
  }
}
