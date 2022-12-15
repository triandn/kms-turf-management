package com.kms.cntt.service.impl;

import com.kms.cntt.common.Common;
import com.kms.cntt.enums.TurfType;
import com.kms.cntt.exception.NotFoundException;
import com.kms.cntt.mapper.TurfMapper;
import com.kms.cntt.model.LocationTurf;
import com.kms.cntt.model.Turf;
import com.kms.cntt.payload.general.ResponseDataAPI;
import com.kms.cntt.payload.request.TurfRequest;
import com.kms.cntt.payload.request.TurfUpdateRequest;
import com.kms.cntt.payload.response.TurfResponse;
import com.kms.cntt.repository.TurfRepository;
import com.kms.cntt.service.LocationTurfService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TurfServiceImpTest {
    @Mock
    private TurfRepository turfRepository;
    @Mock
    private TurfMapper turfMapper;
    @Mock
    private LocationTurfService locationTurfService;

    @InjectMocks
    private TurfServiceImp turfService;


    @Test
    @DisplayName("Should return a list of turfs when the location turf id is valid")
    void findAllByLocationTurfWhenLocationTurfIdIsValidThenReturnListOfTurfs() {
        UUID locationTurfId = UUID.randomUUID();
        Turf turf = new Turf();
        turf.setId(UUID.randomUUID());
        turf.setName("Turf 1");
        turf.setHourlyFee(BigDecimal.valueOf(100));
        turf.setType(TurfType.FIVE_SIDE);
        turf.setImageLink("https://image.com/turf1");

        TurfResponse turfResponse = new TurfResponse();
        turfResponse.setId(turf.getId());
        turfResponse.setName(turf.getName());
        turfResponse.setHourlyFee(turf.getHourlyFee());
        turfResponse.setType(turf.getType());
        turfResponse.setImageLink(turf.getImageLink());

        when(turfRepository.findAllByLocationTurfIdAndDeletedAtIsNull(any(), any()))
                .thenReturn(new PageImpl<>(List.of(turf)));

        when(turfMapper.toResponse(any())).thenReturn(turfResponse);

        ResponseDataAPI responseDataAPI =
                turfService.findAllByLocationTurf(PageRequest.of(0, 10), locationTurfId);

        assertEquals(Common.SUCCESS, responseDataAPI.getStatus());
    }

    @Test
    @DisplayName("Should return the turf when the turf is found")
    void findByIdWhenTurfIsFound() {
        UUID id = UUID.randomUUID();
        Turf turf = new Turf();
        turf.setId(id);
        turf.setName("Turf 1");
        turf.setHourlyFee(new BigDecimal(100));
        turf.setType(TurfType.FIVE_SIDE);
        turf.setImageLink("https://image.com/turf1");

        when(turfRepository.findByIdAndDeletedAtIsNull(any()))
                .thenReturn(java.util.Optional.of(turf));

        Turf result = turfService.findById(id);

        assertEquals(id, result.getId());
    }

    @Test
    @DisplayName("Should return success response when turf is deleted")
    void deleteTurfWhenTurfIsDeletedThenReturnSuccessResponse() {
        Turf turf = new Turf();
        turf.setId(UUID.randomUUID());
        turf.setName("Turf 1");
        turf.setHourlyFee(new BigDecimal(100));
        turf.setType(TurfType.FIVE_SIDE);
        turf.setImageLink("https://image.com/turf1");
        turf.setLocationTurf(new LocationTurf());

        when(turfRepository.findByIdAndDeletedAtIsNull(any()))
                .thenReturn(java.util.Optional.of(turf));

        ResponseDataAPI response = turfService.deleteTurf(turf.getId());

        assertEquals(Common.SUCCESS, response.getStatus());
        assertNull(response.getData());
        assertNull(response.getMeta());
    }

    @Test
    @DisplayName(
            "Should return a response data api with success status when the turf is updated successfully")
    void updateTurfWhenSuccessThenReturnResponseDataAPIWithSuccessStatus() {
        Turf turf = new Turf();
        turf.setId(UUID.randomUUID());
        turf.setName("Turf 1");
        turf.setHourlyFee(new BigDecimal(100));
        turf.setType(TurfType.FIVE_SIDE);
        turf.setImageLink("https://image.com/turf1");

        TurfUpdateRequest turfUpdateRequest = new TurfUpdateRequest();
        turfUpdateRequest.setName("Turf 1");
        turfUpdateRequest.setHourlyFee(new BigDecimal(100));
        turfUpdateRequest.setType("FIVE_SIDE");
        turfUpdateRequest.setImageLink("https://image.com/turf1");

        when(turfRepository.findByIdAndDeletedAtIsNull(any()))
                .thenReturn(java.util.Optional.ofNullable(turf));

        ResponseDataAPI responseDataAPI =
                turfService.updateTurf(UUID.randomUUID(), turfUpdateRequest);

        assertEquals(Common.SUCCESS, responseDataAPI.getStatus());
    }

    @Test
    @DisplayName("Should throw an exception when the location turf is not found")
    void createTurfWhenLocationTurfIsNotFoundThenThrowException() {
        TurfRequest turfRequest = new TurfRequest();
        turfRequest.setName("Turf 1");
        turfRequest.setLocationTurfId(UUID.randomUUID());
        turfRequest.setHourlyFee(new BigDecimal(100));
        turfRequest.setType("FIVE_SIDE");
        turfRequest.setImageLink("https://image.com/turf1");

        when(locationTurfService.findById(any()))
                .thenThrow(new NotFoundException("Location turf not found"));

        assertThrows(NotFoundException.class, () -> turfService.createTurf(turfRequest));

        verify(locationTurfService, times(1)).findById(any());
    }

    @Test
    @DisplayName("Should return a turf when the location turf is found")
    void createTurfWhenLocationTurfIsFoundThenReturnATurf() {
        Turf turf = new Turf();
        turf.setId(UUID.randomUUID());
        turf.setName("Turf 1");
        turf.setHourlyFee(new BigDecimal(100));
        turf.setType(TurfType.FIVE_SIDE);
        turf.setImageLink("https://image.com/turf1");
        turf.setRating(5);

        TurfRequest turfRequest = new TurfRequest();
        turfRequest.setName("Turf 1");
        turfRequest.setHourlyFee(new BigDecimal(100));
        turfRequest.setType("FIVE_SIDE");
        turfRequest.setImageLink("https://image.com/turf1");

        LocationTurf locationTurf = new LocationTurf();
        locationTurf.setId(UUID.randomUUID());

        when(locationTurfService.findById(any())).thenReturn(locationTurf);
        when(turfRepository.save(any())).thenReturn(turf);

        ResponseDataAPI result = turfService.createTurf(turfRequest);

        assertEquals("success", result.getStatus());
    }
}