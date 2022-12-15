package com.kms.cntt.controller;

import com.kms.cntt.enums.TurfType;
import com.kms.cntt.mapper.TurfMapper;
import com.kms.cntt.model.LocationTurf;
import com.kms.cntt.model.Turf;
import com.kms.cntt.payload.general.ResponseDataAPI;
import com.kms.cntt.payload.request.TurfRequest;
import com.kms.cntt.payload.request.TurfUpdateRequest;
import com.kms.cntt.payload.response.TurfResponse;
import com.kms.cntt.security.oauth2.UserPrincipal;
import com.kms.cntt.service.TurfService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TurfControllerTest {
    @Mock
    private TurfService turfService;

    @Mock
    private TurfMapper turfMapper;

    @InjectMocks
    private TurfController turfController;

    @Mock
    private UserPrincipal userPrincipal;

    @Test
    @DisplayName("Should return an empty list when the location turf id is invalid")
    void getAllTurfWhenLocationTurfIdIsInvalidThenReturnEmptyList() {
        UUID locationTurfId = UUID.randomUUID();
        int page = 1;
        int paging = 10;
        String sortBy = "created_at";
        String order = "desc";

        when(turfService.findAllByLocationTurf(any(), any()))
                .thenReturn(ResponseDataAPI.successWithoutMeta(null));

        ResponseEntity<ResponseDataAPI> responseEntity =
                turfController.getAllTurf(page, paging, sortBy, order, locationTurfId);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody().getData());
    }

    @Test
    @DisplayName("Should return a list of turfs when the location turf id is valid")
    void getAllTurfWhenLocationTurfIdIsValidThenReturnListOfTurfs() {
        Turf turf = new Turf();
        turf.setId(UUID.randomUUID());
        turf.setName("Turf 1");
        turf.setHourlyFee(BigDecimal.valueOf(100));
        turf.setRating(4);
        turf.setType(TurfType.FIVE_SIDE);
        turf.setImageLink("https://image.com/turf1");

        TurfResponse turfResponse = new TurfResponse();
        turfResponse.setId(turf.getId());
        turfResponse.setName(turf.getName());
        turfResponse.setHourlyFee(turf.getHourlyFee());
        turfResponse.setRating(turf.getRating());
        turfResponse.setType(turf.getType());
        turfResponse.setImageLink(turf.getImageLink());

        when(turfService.findAllByLocationTurf(any(), any()))
                .thenReturn(ResponseDataAPI.successWithoutMeta(turfResponse));

        ResponseEntity<ResponseDataAPI> responseEntity =
                turfController.getAllTurf(1, 10, "created_at", "desc", UUID.randomUUID());

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Should return a turf when the id is valid")
    void getTurfWhenIdIsValid() {
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

        when(turfService.findById(any())).thenReturn(turf);
        when(turfMapper.toResponse(any())).thenReturn(turfResponse);

        ResponseEntity<ResponseDataAPI> responseEntity = turfController.getTurf(UUID.randomUUID());

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("success", responseEntity.getBody().getStatus());
        assertEquals(turfResponse, responseEntity.getBody().getData());

        verify(turfService, times(1)).findById(any());
    }

    @Test
    @DisplayName("Should return 200 when the turf is deleted")
    void deleteTurfWhenTurfIsDeletedThenReturn200() {
        UUID turfId = UUID.randomUUID();
        ResponseDataAPI responseDataAPI = new ResponseDataAPI();
        responseDataAPI.setStatus("success");
        when(turfService.deleteTurf(turfId)).thenReturn(responseDataAPI);

        ResponseEntity<ResponseDataAPI> responseEntity = turfController.deleteTurf(turfId);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName(
            "Should return a response with status code 200 when the turf is updated successfully")
    void updateTurfWhenTurfIsUpdatedSuccessfullyThenReturnResponseWithStatusCode200() {
        Turf turf = new Turf();
        turf.setId(UUID.randomUUID());
        turf.setName("Turf 1");
        turf.setHourlyFee(BigDecimal.valueOf(100));
        turf.setType(TurfType.FIVE_SIDE);
        turf.setImageLink("https://image.com/turf1");

        LocationTurf locationTurf = new LocationTurf();
        locationTurf.setId(UUID.randomUUID());
        locationTurf.setName("Location Turf 1");
        locationTurf.setAddress("Address 1");
        locationTurf.setImageLink("https://image.com/location-turf1");

        turf.setLocationTurf(locationTurf);

        TurfUpdateRequest turfUpdateRequest = new TurfUpdateRequest();
        turfUpdateRequest.setName("Turf 1");
        turfUpdateRequest.setHourlyFee(BigDecimal.valueOf(100));
        turfUpdateRequest.setType(TurfType.FIVE_SIDE.toString());
        turfUpdateRequest.setImageLink("https://image.com/turf1");

        TurfResponse turfResponse = new TurfResponse();
        turfResponse.setId(turf.getId());
        turfResponse.setLocationTurfId(locationTurf.getId());
        turfResponse.setCreatedAt(turf.getCreatedAt());
        turfResponse.setName(turf.getName());
        turfResponse.setHourlyFee(turf.getHourlyFee());
        turfResponse.setRating(turf.getRating());
        turfResponse.setType(turf.getType());
        turfResponse.setImageLink(turf.getImageLink());

        when(turfService.updateTurf(any(), any()))
                .thenReturn(ResponseDataAPI.<TurfResponse>successWithoutMeta(turfResponse));

        ResponseEntity<ResponseDataAPI> responseEntity =
                turfController.updateTurf(UUID.randomUUID(), turfUpdateRequest);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName(
            "Should return a response with status code 200 when the turf is created successfully")
    void createTurfWhenTurfIsCreatedSuccessfullyThenReturnResponseWithStatusCode200() {
        TurfRequest turfRequest = new TurfRequest();
        Turf turf = new Turf();
        turf.setId(UUID.randomUUID());

        LocationTurf locationTurf = new LocationTurf();
        locationTurf.setId(UUID.randomUUID());
        locationTurf.setName("Hoa Khanh");

        turf.setLocationTurf(locationTurf);
        turf.setName("Chuyen Viet");
//        turf.setPrice(BigDecimal.ONE);
        turf.setType(TurfType.FIVE_SIDE);
        turf.setRating(Float.valueOf(5));
        turf.setImageLink("imageLink");
        TurfResponse turfResponse = new TurfResponse(turf.getId(), turf.getLocationTurf().getId(), turf.getCreatedAt(), turf.getName(),
                turf.getHourlyFee(), turf.getRating(), turf.getType(), turf.getImageLink());
        ResponseDataAPI responseDataAPI = new ResponseDataAPI();

        when(turfService.createTurf(turfRequest)).thenReturn(responseDataAPI);
//        when(turfMapper.toEntity(turf, turfRequest)).thenReturn(turf);
//        when(turfMapper.toResponse(turf)).thenReturn(turfResponse);

        ResponseEntity<ResponseDataAPI> responseEntity =
                turfController.createTurf(turfRequest, userPrincipal);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }
}