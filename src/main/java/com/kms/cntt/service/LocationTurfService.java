package com.kms.cntt.service;

import com.kms.cntt.model.LocationTurf;
import com.kms.cntt.payload.general.ResponseDataAPI;
import com.kms.cntt.payload.request.LocationTurfRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface LocationTurfService {
    ResponseDataAPI createLocationTurf(UUID userId, LocationTurfRequest locationTurfRequest);

    ResponseDataAPI updateLocationTurf(UUID locationTurfId, LocationTurfRequest locationTurfRequest);

    ResponseDataAPI deleteLocationTurf(UUID locationTurfId);

    LocationTurf findById(UUID id);

    ResponseDataAPI findAll(Pageable pageable);
}
