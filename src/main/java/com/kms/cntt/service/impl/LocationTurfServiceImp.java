package com.kms.cntt.service.impl;

import com.kms.cntt.common.CommonFunction;
import com.kms.cntt.common.MessageConstant;
import com.kms.cntt.exception.NotFoundException;
import com.kms.cntt.mapper.LocationTurfMapper;
import com.kms.cntt.model.LocationTurf;
import com.kms.cntt.payload.general.PageInfo;
import com.kms.cntt.payload.general.ResponseDataAPI;
import com.kms.cntt.payload.request.LocationTurfRequest;
import com.kms.cntt.payload.response.LocationTurfResponse;
import com.kms.cntt.repository.LocationTurfRepository;
import com.kms.cntt.service.LocationTurfService;
import com.kms.cntt.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LocationTurfServiceImp implements LocationTurfService {
  private final LocationTurfRepository locationTurfRepository;
  private final LocationTurfMapper locationTurfMapper;
  private final UserService userService;

  @Override
  public ResponseDataAPI createLocationTurf(UUID userId, LocationTurfRequest locationTurfRequest) {
    LocationTurf locationTurf = new LocationTurf();
    this.toEntity(locationTurf, locationTurfRequest);
    locationTurf.setUser(userService.findById(userId));
    locationTurf.setCreatedAt(CommonFunction.getCurrentDateTime());
    LocationTurf result = locationTurfRepository.save(locationTurf);
    return ResponseDataAPI.successWithoutMeta(locationTurfMapper.toResponse(result));
  }

  @Override
  public ResponseDataAPI updateLocationTurf(
      UUID locationTurfId, LocationTurfRequest locationTurfRequest) {
    LocationTurf locationTurf = this.findById(locationTurfId);
    this.toEntity(locationTurf, locationTurfRequest);
    locationTurf.setUpdatedAt(CommonFunction.getCurrentDateTime());
    LocationTurf result = locationTurfRepository.save(locationTurf);
    return ResponseDataAPI.successWithoutMeta(locationTurfMapper.toResponse(result));
  }

  @Override
  public ResponseDataAPI deleteLocationTurf(UUID locationTurfId) {
    LocationTurf locationTurf = this.findById(locationTurfId);
    locationTurf.setDeletedAt(CommonFunction.getCurrentDateTime());
    locationTurfRepository.save(locationTurf);
    return ResponseDataAPI.success(null, null);
  }

  @Override
  public LocationTurf findById(UUID id) {
    return locationTurfRepository
        .findByIdAndDeletedAtIsNull(id)
        .orElseThrow(() -> new NotFoundException(MessageConstant.LOCATION_TURF_NOT_FOUND));
  }

  @Override
  public ResponseDataAPI findAll(Pageable pageable) {
    Page<LocationTurf> page = locationTurfRepository.findAllByDeletedAtIsNull(pageable);
    var pageInfo =
        new PageInfo(pageable.getPageNumber() + 1, page.getTotalPages(), page.getTotalElements());
    List<LocationTurfResponse> locationTurfResponses =
        page.getContent().stream().map(locationTurfMapper::toResponse).collect(Collectors.toList());
    return ResponseDataAPI.success(locationTurfResponses, pageInfo);
  }

  private void toEntity(LocationTurf locationTurf, LocationTurfRequest locationTurfRequest) {
    locationTurfMapper.toEntity(locationTurf, locationTurfRequest);
  }
}
