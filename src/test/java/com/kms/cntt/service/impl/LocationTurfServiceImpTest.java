package com.kms.cntt.service.impl;

import com.kms.cntt.common.Common;
import com.kms.cntt.enums.Role;
import com.kms.cntt.mapper.LocationTurfMapper;
import com.kms.cntt.model.LocationTurf;
import com.kms.cntt.model.User;
import com.kms.cntt.payload.general.ResponseDataAPI;
import com.kms.cntt.payload.request.LocationTurfRequest;
import com.kms.cntt.repository.LocationTurfRepository;
import com.kms.cntt.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LocationTurfServiceImpTest {
    @Mock
    private LocationTurfRepository locationTurfRepository;
    @Mock
    private LocationTurfMapper locationTurfMapper;
    @Mock
    private UserService userService;

    @InjectMocks
    private LocationTurfServiceImp locationTurfService;

    @Test
    @DisplayName("Should return location turf when the location turf is found")
    void findByIdWhenLocationTurfIsFound() {
        UUID id = UUID.randomUUID();
        LocationTurf locationTurf = new LocationTurf();
        locationTurf.setId(id);
        when(locationTurfRepository.findByIdAndDeletedAtIsNull(id))
                .thenReturn(java.util.Optional.of(locationTurf));

        LocationTurf result = locationTurfService.findById(id);

        assertEquals(id, result.getId());
    }

    @Test
    @DisplayName("Should return success response when the location turf is found")
    void deleteLocationTurfWhenLocationTurfIsFoundThenReturnSuccessResponse() {
        UUID locationTurfId = UUID.randomUUID();
        LocationTurf locationTurf = new LocationTurf();
        locationTurf.setId(locationTurfId);
        when(locationTurfRepository.findByIdAndDeletedAtIsNull(locationTurfId))
                .thenReturn(java.util.Optional.of(locationTurf));

        ResponseDataAPI responseDataAPI = locationTurfService.deleteLocationTurf(locationTurfId);

        assertEquals(Common.SUCCESS, responseDataAPI.getStatus());
    }

    @Test
    @DisplayName(
            "Should return a response data api with success status when the location turf is updated successfully")
    void updateLocationTurfWhenSuccessThenReturnResponseDataAPIWithSuccessStatus() {
        UUID locationTurfId = UUID.randomUUID();
        LocationTurfRequest locationTurfRequest = new LocationTurfRequest();
        locationTurfRequest.setName("name");
        locationTurfRequest.setAddress("address");
        locationTurfRequest.setImageLink("imageLink");

        LocationTurf locationTurf = new LocationTurf();
        locationTurf.setId(locationTurfId);
        locationTurf.setName("name");
        locationTurf.setAddress("address");
        locationTurf.setImageLink("imageLink");

        User user = new User();
        user.setId(UUID.randomUUID());

        when(locationTurfRepository.findByIdAndDeletedAtIsNull(locationTurfId))
                .thenReturn(java.util.Optional.of(locationTurf));

        ResponseDataAPI responseDataAPI =
                locationTurfService.updateLocationTurf(locationTurfId, locationTurfRequest);

        assertEquals(Common.SUCCESS, responseDataAPI.getStatus());
    }

    @Test
    @DisplayName("Should save the location turf when the userid is valid")
    void createLocationTurfWhenUserIdIsValid() {
        UUID userId = UUID.randomUUID();
        LocationTurfRequest locationTurfRequest = new LocationTurfRequest();
        locationTurfRequest.setName("name");
        locationTurfRequest.setAddress("address");
        locationTurfRequest.setImageLink("imageLink");

        LocationTurf locationTurf = new LocationTurf();
        locationTurf.setName(locationTurfRequest.getName());
        locationTurf.setAddress(locationTurfRequest.getAddress());
        locationTurf.setImageLink(locationTurfRequest.getImageLink());

        User user = new User();
        user.setId(userId);

        when(userService.findById(userId)).thenReturn(user);

        ResponseDataAPI responseDataAPI =
                locationTurfService.createLocationTurf(userId, locationTurfRequest);

        assertEquals(Common.SUCCESS, responseDataAPI.getStatus());
    }
}