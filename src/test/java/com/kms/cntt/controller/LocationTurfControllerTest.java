package com.kms.cntt.controller;

import com.kms.cntt.common.Common;
import com.kms.cntt.enums.Role;
import com.kms.cntt.model.LocationTurf;
import com.kms.cntt.payload.general.ResponseDataAPI;
import com.kms.cntt.payload.request.LocationTurfRequest;
import com.kms.cntt.security.oauth2.UserPrincipal;
import com.kms.cntt.service.LocationTurfService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LocationTurfControllerTest {
    @Mock
    private LocationTurfService locationTurfService;

    @InjectMocks
    private LocationTurfController locationTurfController;

    @Mock
    private UserPrincipal userPrincipal;

    @Test
    @DisplayName("Should return all location turfs with paging and sorting")
    void getAllLocationTurfWithPagingAndSorting() {
        int page = 1;
        int paging = 10;
        String sortBy = "created_at";
        String order = "desc";

        LocationTurf locationTurf = new LocationTurf();
        locationTurf.setId(UUID.randomUUID());
        locationTurf.setName("name");
        locationTurf.setAddress("address");
        locationTurf.setImageLink("imageLink");

        ResponseDataAPI responseDataAPI = new ResponseDataAPI();
        responseDataAPI.setStatus(Common.SUCCESS);
        responseDataAPI.setData(locationTurf);

        when(locationTurfService.findAll(any())).thenReturn(responseDataAPI);

        ResponseEntity<ResponseDataAPI> responseEntity =
                locationTurfController.getAllLocationTurf(page, paging, sortBy, order);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Should return all location turfs when the user is user")
    void getAllLocationTurfWhenUserIsUser() {
        int page = 1;
        int paging = 10;
        String sortBy = "created_at";
        String order = "desc";

        LocationTurf locationTurf = new LocationTurf();
        locationTurf.setId(UUID.randomUUID());
        locationTurf.setName("name");
        locationTurf.setAddress("address");
        locationTurf.setImageLink("imageLink");

        ResponseDataAPI responseDataAPI = new ResponseDataAPI();
        responseDataAPI.setStatus(Common.SUCCESS);
        responseDataAPI.setData(locationTurf);
        when(locationTurfService.findAll(any())).thenReturn(responseDataAPI);

        ResponseEntity<ResponseDataAPI> responseEntity =
                locationTurfController.getAllLocationTurf(page, paging, sortBy, order);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Should return all location turfs when the user is admin")
    void getAllLocationTurfWhenUserIsAdmin() {
        int page = 1;
        int paging = 10;
        String sortBy = "created_at";
        String order = "desc";

        LocationTurf locationTurf = new LocationTurf();
        locationTurf.setId(UUID.randomUUID());
        locationTurf.setName("name");
        locationTurf.setAddress("address");
        locationTurf.setImageLink("imageLink");

        ResponseDataAPI responseDataAPI = new ResponseDataAPI();
        responseDataAPI.setStatus(Common.SUCCESS);
        responseDataAPI.setData(locationTurf);

        when(locationTurfService.findAll(any())).thenReturn(responseDataAPI);

        ResponseEntity<ResponseDataAPI> responseEntity =
                locationTurfController.getAllLocationTurf(page, paging, sortBy, order);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Should return 200 when the location turf is deleted")
    void deleteLocationTurfWhenLocationTurfIsDeletedThenReturn200() {
        UUID locationTurfId = UUID.randomUUID();
        ResponseDataAPI responseDataAPI = new ResponseDataAPI();
        responseDataAPI.setStatus(Common.SUCCESS);
        when(locationTurfService.deleteLocationTurf(locationTurfId)).thenReturn(responseDataAPI);

        ResponseEntity<ResponseDataAPI> responseEntity =
                locationTurfController.deleteLocationTurf(locationTurfId);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Should return 200 when the location turf is updated successfully")
    void updateLocationTurfWhenSuccess() {
        UUID locationTurfId = UUID.randomUUID();
        LocationTurfRequest locationTurfRequest = new LocationTurfRequest();
        ResponseDataAPI responseDataAPI = new ResponseDataAPI();

        when(locationTurfService.updateLocationTurf(locationTurfId, locationTurfRequest))
                .thenReturn(responseDataAPI);

        ResponseEntity<ResponseDataAPI> responseEntity =
                locationTurfController.updateLocationTurf(locationTurfId, locationTurfRequest);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(responseDataAPI, responseEntity.getBody());

        verify(locationTurfService, times(1))
                .updateLocationTurf(locationTurfId, locationTurfRequest);
    }

    @Test
    @DisplayName("Should return a response with status code 200 when the request is valid")
    void createLocationTurfWhenRequestIsValidThenReturnResponseWithStatusCode200() {
        UUID userId = UUID.randomUUID();
        LocationTurfRequest locationTurfRequest = new LocationTurfRequest();
        locationTurfRequest.setName("name");
        locationTurfRequest.setAddress("address");
        locationTurfRequest.setImageLink("imageLink");

        when(userPrincipal.getId()).thenReturn(userId);

        ResponseEntity<ResponseDataAPI> response =
                locationTurfController.createLocationTurf(locationTurfRequest, userPrincipal);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}