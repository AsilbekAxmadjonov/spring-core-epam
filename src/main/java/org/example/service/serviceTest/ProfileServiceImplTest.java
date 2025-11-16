package org.example.service.serviceTest;

import org.example.dao.ProfileSavableDao;
import org.example.model.Trainee;
import org.example.service.ProfileServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ProfileServiceImplTest {

    private ProfileServiceImpl service;
    private ProfileSavableDao profileDao;

    @BeforeEach
    void setup() {
        service = new ProfileServiceImpl();
        profileDao = Mockito.mock(ProfileSavableDao.class);
        service.setProfileSavableDao(profileDao);
    }

    @Test
    void testCreateProfileAssignsUsernameAndPassword() {
        // Use a concrete subclass instead of abstract User
        Trainee trainee = new Trainee();
        trainee.setFirstName("John");
        trainee.setLastName("Doe");

        // Simulate existing usernames
        when(profileDao.findAllUsernames()).thenReturn(List.of("John.Doe"));

        service.createProfile(trainee);

        ArgumentCaptor<Trainee> captor = ArgumentCaptor.forClass(Trainee.class);
        verify(profileDao, times(1)).save(captor.capture());

        Trainee saved = captor.getValue();
        assertEquals("John.Doe1", saved.getUsername());
        assertEquals(10, saved.getPassword().length);
    }
}
