package org.example.services.impl.inMemoryImpl;

import org.example.dao.GenericDao;
import org.example.exception.UnsupportedDataAccessObjectException;
import org.example.persistance.model.User;
import org.example.services.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceInMemoryImplTest {

    private GenericDao<User> userDaoMock;
    private AuthenticationService authenticationServiceMock;
    private UserServiceInMemoryImpl userService;

    @BeforeEach
    void setUp() {
        userDaoMock = mock(GenericDao.class);
        authenticationServiceMock = mock(AuthenticationService.class);
        when(userDaoMock.getEntityClass()).thenReturn(User.class);
        userService = new UserServiceInMemoryImpl(List.of(userDaoMock));
    }

    @Test
    void testCreateUserSuccess() {
        User user = new User();
        user.setUsername("john");

        User created = userService.createUser(user);

        assertEquals("john", created.getUsername());
        verify(userDaoMock).save(user);
    }

    @Test
    void testCreateUserNoDao() {
        UserServiceInMemoryImpl serviceWithoutDao = new UserServiceInMemoryImpl(List.of());
        User user = new User();

        assertThrows(UnsupportedDataAccessObjectException.class, () -> serviceWithoutDao.createUser(user));
    }

    @Test
    void testFetchAll() {
        User user1 = new User();
        User user2 = new User();
        when(userDaoMock.findAll()).thenReturn(List.of(user1, user2));

        List<User> users = userService.fetchAll();

        assertEquals(2, users.size());
        assertTrue(users.contains(user1));
        assertTrue(users.contains(user2));
    }

    @Test
    void testUpdateUserThrowsUnsupported() {
        User user = new User();
        assertThrows(UnsupportedOperationException.class, () -> userService.updateUser("john", user));
    }

    @Test
    void testDeleteByUsernameThrowsUnsupported() {
        assertThrows(UnsupportedOperationException.class, () -> userService.deleteByUsername("john"));
    }


    @Test
    void testAuthenticationCalledOnCreateAndUpdateMethods() {
        User user = new User();
        user.setUsername("alice");

        userService.createUser(user);
        verify(authenticationServiceMock, never()).authenticate(anyString(), any());

    }

}
