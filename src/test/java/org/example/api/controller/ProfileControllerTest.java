package org.example.api.controller;

import org.example.api.controller.ProfileController;
import org.example.api.dto.request.PasswordChangeRequest;
import org.example.api.dto.response.ProfileResponse;
import org.example.exception.UserNotFoundException;
import org.example.services.ProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProfileControllerTest {

    @Mock
    private ProfileService profileService;

    @InjectMocks
    private ProfileController profileController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testChangeActiveStatusSuccess() {
        String username = "user1";
        when(profileService.toggleUserActiveStatus(username)).thenReturn(true);

        ResponseEntity<ProfileResponse> response = profileController.changeActiveStatus(username);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().isSuccess());
        assertEquals(username, response.getBody().getUsername());
        verify(profileService, times(1)).toggleUserActiveStatus(username);
    }

    @Test
    void testChangeActiveStatusUserNotFound() {
        String username = "user1";
        when(profileService.toggleUserActiveStatus(username)).thenThrow(new UserNotFoundException("Not found"));

        assertThrows(UserNotFoundException.class, () -> profileController.changeActiveStatus(username));
        verify(profileService, times(1)).toggleUserActiveStatus(username);
    }

    @Test
    void testChangePasswordSuccess() {
        String username = "user1";
        PasswordChangeRequest request = new PasswordChangeRequest();
        request.setOldPassword("oldpass".toCharArray());
        request.setNewPassword("newpass".toCharArray());

        when(profileService.passwordMatches(username, request.getOldPassword())).thenReturn(true);
        doNothing().when(profileService).changePassword(username, request.getNewPassword());

        ResponseEntity<ProfileResponse> response = profileController.changePassword(username, request);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().isSuccess());
        assertEquals(username, response.getBody().getUsername());

        verify(profileService, times(1)).passwordMatches(username, request.getOldPassword());
        verify(profileService, times(1)).changePassword(username, request.getNewPassword());
    }

    @Test
    void testChangePasswordOldPasswordIncorrect() {
        String username = "user1";
        PasswordChangeRequest request = new PasswordChangeRequest();
        request.setOldPassword("wrongOld".toCharArray());
        request.setNewPassword("newpass".toCharArray());

        when(profileService.passwordMatches(username, request.getOldPassword())).thenReturn(false);

        ResponseEntity<ProfileResponse> response = profileController.changePassword(username, request);

        assertEquals(400, response.getStatusCodeValue());
        assertFalse(response.getBody().isSuccess());
        assertEquals(username, response.getBody().getUsername());
        assertEquals("Old password is incorrect", response.getBody().getMessage());

        verify(profileService, times(1)).passwordMatches(username, request.getOldPassword());
        verify(profileService, never()).changePassword(anyString(), any());
    }

    @Test
    void testChangePasswordMissingFields() {
        String username = "user1";
        PasswordChangeRequest request = new PasswordChangeRequest();
        request.setOldPassword(null);
        request.setNewPassword(null);

        ResponseEntity<ProfileResponse> response = profileController.changePassword(username, request);

        assertEquals(400, response.getStatusCodeValue());
        assertFalse(response.getBody().isSuccess());
        assertEquals(username, response.getBody().getUsername());
        assertEquals("Old password and new password are required", response.getBody().getMessage());
        verify(profileService, never()).passwordMatches(anyString(), any());
        verify(profileService, never()).changePassword(anyString(), any());
    }
}
