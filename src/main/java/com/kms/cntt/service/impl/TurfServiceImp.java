package com.kms.cntt.service.impl;

import com.kms.cntt.common.CommonFunction;
import com.kms.cntt.common.MessageConstant;
import com.kms.cntt.enums.TurfType;
import com.kms.cntt.exception.NotFoundException;
import com.kms.cntt.mapper.TurfMapper;
import com.kms.cntt.model.Turf;
import com.kms.cntt.payload.general.PageInfo;
import com.kms.cntt.payload.general.ResponseDataAPI;
import com.kms.cntt.payload.request.TurfRequest;
import com.kms.cntt.payload.request.TurfUpdateRequest;
import com.kms.cntt.payload.response.TurfResponse;
import com.kms.cntt.repository.TurfRepository;
import com.kms.cntt.service.LocationTurfService;
import com.kms.cntt.service.TurfService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TurfServiceImp implements TurfService {
  private final TurfRepository turfRepository;
  private final TurfMapper turfMapper;
  private final LocationTurfService locationTurfService;

  @Override
  public ResponseDataAPI createTurf(TurfRequest turfRequest) {
    Turf turf = new Turf();
    this.toEntity(turf, turfRequest);
    turf.setLocationTurf(locationTurfService.findById(turfRequest.getLocationTurfId()));
    turf.setCreatedAt(CommonFunction.getCurrentDateTime());
    turf.setRating(5);
    Turf result = turfRepository.save(turf);
    return ResponseDataAPI.successWithoutMeta(turfMapper.toResponse(result));
  }

  @Override
  public ResponseDataAPI updateTurf(UUID turfId, TurfUpdateRequest turfUpdateRequest) {
    Turf turf = this.findById(turfId);
    turf.setName(turfUpdateRequest.getName());
    turf.setHourlyFee(turfUpdateRequest.getHourlyFee());
    turf.setType(TurfType.valueOf(turfUpdateRequest.getType()));
    turf.setImageLink(turfUpdateRequest.getImageLink());
    turf.setUpdatedAt(CommonFunction.getCurrentDateTime());
    Turf result = turfRepository.save(turf);
    return ResponseDataAPI.successWithoutMeta(turfMapper.toResponse(result));
  }

  @Override
  public ResponseDataAPI deleteTurf(UUID turfId) {
    Turf turf = this.findById(turfId);
    turf.setDeletedAt(CommonFunction.getCurrentDateTime());
    turfRepository.save(turf);
    return ResponseDataAPI.success(null, null);
  }

  @Override
  public Turf findById(UUID id) {
    return turfRepository
        .findByIdAndDeletedAtIsNull(id)
        .orElseThrow(() -> new NotFoundException(MessageConstant.TURF_NOT_FOUND));
  }

  @Override
  public ResponseDataAPI findAllByLocationTurf(Pageable pageable, UUID locationTurfId) {
    Page<Turf> page =
        turfRepository.findAllByLocationTurfIdAndDeletedAtIsNull(pageable, locationTurfId);
    var pageInfo =
        new PageInfo(pageable.getPageNumber() + 1, page.getTotalPages(), page.getTotalElements());
    List<TurfResponse> turfResponses =
        page.getContent().stream().map(turfMapper::toResponse).collect(Collectors.toList());
    return ResponseDataAPI.success(turfResponses, pageInfo);
  }

  private void toEntity(Turf turf, TurfRequest turfRequest) {
    turfMapper.toEntity(turf, turfRequest);
  }
}
