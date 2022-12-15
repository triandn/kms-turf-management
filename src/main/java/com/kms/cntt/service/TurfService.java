package com.kms.cntt.service;

import com.kms.cntt.model.Turf;
import com.kms.cntt.payload.general.ResponseDataAPI;
import com.kms.cntt.payload.request.TurfRequest;
import com.kms.cntt.payload.request.TurfUpdateRequest;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface TurfService {

  ResponseDataAPI createTurf(TurfRequest turfRequest);

  ResponseDataAPI updateTurf(UUID turfId, TurfUpdateRequest turfUpdateRequest);

  ResponseDataAPI deleteTurf(UUID turfId);

  Turf findById(UUID id);

  ResponseDataAPI findAllByLocationTurf(Pageable pageable, UUID locationTurfId);
}
