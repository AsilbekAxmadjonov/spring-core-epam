package org.example.mapper;

import org.example.entity.UserEntity;
import org.example.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        userMapper = Mappers.getMapper(UserMapper.class);
    }

    @Test
    void testToModel_Success() {
        UserEntity entity = UserEntity.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .username("john.doe")
                .password("password123".toCharArray())
                .isActive(true)
                .build();

        User result = userMapper.toModel(entity);

        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("john.doe", result.getUsername());
        assertArrayEquals("password123".toCharArray(), result.getPassword());
        assertTrue(result.isActive());
    }

    @Test
    void testToModel_WithNullEntity() {
        User result = userMapper.toModel(null);

        assertNull(result);
    }

    @Test
    void testToModel_WithInactiveUser() {
        UserEntity entity = UserEntity.builder()
                .id(1L)
                .firstName("Jane")
                .lastName("Smith")
                .username("jane.smith")
                .password("securepass".toCharArray())
                .isActive(false)
                .build();

        User result = userMapper.toModel(entity);

        assertNotNull(result);
        assertFalse(result.isActive());
    }

    @Test
    void testToEntity_Success() {
        User model = User.builder()
                .firstName("Alice")
                .lastName("Johnson")
                .username("alice.j")
                .password("mypassword".toCharArray())
                .isActive(true)
                .build();

        UserEntity result = userMapper.toEntity(model);

        assertNotNull(result);
        assertEquals("Alice", result.getFirstName());
        assertEquals("Johnson", result.getLastName());
        assertEquals("alice.j", result.getUsername());
        assertArrayEquals("mypassword".toCharArray(), result.getPassword());
        assertNull(result.getId());
    }

    @Test
    void testToEntity_WithNullModel() {
        UserEntity result = userMapper.toEntity(null);

        assertNull(result);
    }

    @Test
    void testToEntity_WithInactiveUser() {
        User model = User.builder()
                .firstName("Bob")
                .lastName("Brown")
                .username("bob.brown")
                .password("password456".toCharArray())
                .isActive(false)
                .build();

        UserEntity result = userMapper.toEntity(model);

        assertNotNull(result);
        assertEquals("Bob", result.getFirstName());
        assertEquals("Brown", result.getLastName());
        assertEquals("bob.brown", result.getUsername());
    }

    @Test
    void testToModels_Success() {
        UserEntity entity1 = UserEntity.builder()
                .id(1L)
                .firstName("User1")
                .lastName("Last1")
                .username("user1")
                .password("pass1".toCharArray())
                .isActive(true)
                .build();

        UserEntity entity2 = UserEntity.builder()
                .id(2L)
                .firstName("User2")
                .lastName("Last2")
                .username("user2")
                .password("pass2".toCharArray())
                .isActive(false)
                .build();

        UserEntity entity3 = UserEntity.builder()
                .id(3L)
                .firstName("User3")
                .lastName("Last3")
                .username("user3")
                .password("pass3".toCharArray())
                .isActive(true)
                .build();

        List<UserEntity> entities = Arrays.asList(entity1, entity2, entity3);

        List<User> result = userMapper.toModels(entities);

        assertNotNull(result);
        assertEquals(3, result.size());

        assertEquals("user1", result.get(0).getUsername());
        assertTrue(result.get(0).isActive());

        assertEquals("user2", result.get(1).getUsername());
        assertFalse(result.get(1).isActive());

        assertEquals("user3", result.get(2).getUsername());
        assertTrue(result.get(2).isActive());
    }

    @Test
    void testToModels_WithEmptyList() {
        List<UserEntity> emptyList = Collections.emptyList();

        List<User> result = userMapper.toModels(emptyList);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testToModels_WithNullList() {
        List<User> result = userMapper.toModels(null);

        assertNull(result);
    }

    @Test
    void testToModels_WithSingleElement() {
        UserEntity entity = UserEntity.builder()
                .id(1L)
                .firstName("Single")
                .lastName("User")
                .username("single.user")
                .password("singlepass".toCharArray())
                .isActive(true)
                .build();

        List<UserEntity> entities = Collections.singletonList(entity);

        List<User> result = userMapper.toModels(entities);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("single.user", result.get(0).getUsername());
    }

    @Test
    void testRoundTripMapping() {
        UserEntity originalEntity = UserEntity.builder()
                .id(1L)
                .firstName("Round")
                .lastName("Trip")
                .username("round.trip")
                .password("roundpass".toCharArray())
                .isActive(true)
                .build();

        User model = userMapper.toModel(originalEntity);
        UserEntity entityAgain = userMapper.toEntity(model);

        assertEquals(originalEntity.getFirstName(), entityAgain.getFirstName());
        assertEquals(originalEntity.getLastName(), entityAgain.getLastName());
        assertEquals(originalEntity.getUsername(), entityAgain.getUsername());
        assertArrayEquals(originalEntity.getPassword(), entityAgain.getPassword());
    }

    @Test
    void testModelToEntityToModel() {
        User originalModel = User.builder()
                .firstName("Original")
                .lastName("Model")
                .username("original.model")
                .password("modelpass".toCharArray())
                .isActive(false)
                .build();

        UserEntity entity = userMapper.toEntity(originalModel);
        User modelAgain = userMapper.toModel(entity);

        assertEquals(originalModel.getFirstName(), modelAgain.getFirstName());
        assertEquals(originalModel.getLastName(), modelAgain.getLastName());
        assertEquals(originalModel.getUsername(), modelAgain.getUsername());
        assertArrayEquals(originalModel.getPassword(), modelAgain.getPassword());
        assertEquals(originalModel.isActive(), modelAgain.isActive());
    }

    @Test
    void testPasswordArrayMapping() {
        char[] password = "testPassword123!@#".toCharArray();
        UserEntity entity = UserEntity.builder()
                .id(1L)
                .firstName("Test")
                .lastName("Password")
                .username("test.password")
                .password(password)
                .isActive(true)
                .build();

        User model = userMapper.toModel(entity);

        assertNotNull(model.getPassword());
        assertArrayEquals(password, model.getPassword());
        assertEquals(password.length, model.getPassword().length);
    }

    @Test
    void testEmptyPasswordArray() {
        UserEntity entity = UserEntity.builder()
                .id(1L)
                .firstName("Empty")
                .lastName("Pass")
                .username("empty.pass")
                .password(new char[0])
                .isActive(true)
                .build();

        User model = userMapper.toModel(entity);

        assertNotNull(model.getPassword());
        assertEquals(0, model.getPassword().length);
    }

    @Test
    void testBooleanIsActiveMapping() {
        UserEntity activeEntity = UserEntity.builder()
                .id(1L)
                .firstName("Active")
                .lastName("User")
                .username("active.user")
                .password("pass".toCharArray())
                .isActive(true)
                .build();

        UserEntity inactiveEntity = UserEntity.builder()
                .id(2L)
                .firstName("Inactive")
                .lastName("User")
                .username("inactive.user")
                .password("pass".toCharArray())
                .isActive(false)
                .build();

        User activeModel = userMapper.toModel(activeEntity);
        User inactiveModel = userMapper.toModel(inactiveEntity);

        assertTrue(activeModel.isActive());
        assertFalse(inactiveModel.isActive());
    }

    @Test
    void testUsernamePreserved() {
        String username = "complex.username_123";
        UserEntity entity = UserEntity.builder()
                .id(1L)
                .firstName("Test")
                .lastName("User")
                .username(username)
                .password("pass".toCharArray())
                .isActive(true)
                .build();

        User model = userMapper.toModel(entity);

        assertEquals(username, model.getUsername());
    }

    @Test
    void testAllFieldsPreserved() {
        UserEntity entity = UserEntity.builder()
                .id(99L)
                .firstName("Complete")
                .lastName("Test")
                .username("complete.test")
                .password("completePassword".toCharArray())
                .isActive(true)
                .build();

        User model = userMapper.toModel(entity);

        assertNotNull(model);
        assertEquals("Complete", model.getFirstName());
        assertEquals("Test", model.getLastName());
        assertEquals("complete.test", model.getUsername());
        assertNotNull(model.getPassword());
        assertArrayEquals("completePassword".toCharArray(), model.getPassword());
        assertTrue(model.isActive());
    }

    @Test
    void testEqualityBasedOnUsername() {
        User user1 = User.builder()
                .firstName("John")
                .lastName("Doe")
                .username("john.doe")
                .password("pass1".toCharArray())
                .isActive(true)
                .build();

        User user2 = User.builder()
                .firstName("Jane")
                .lastName("Smith")
                .username("john.doe")
                .password("pass2".toCharArray())
                .isActive(false)
                .build();

        User user3 = User.builder()
                .firstName("John")
                .lastName("Doe")
                .username("different.user")
                .password("pass1".toCharArray())
                .isActive(true)
                .build();

        assertEquals(user1, user2);
        assertNotEquals(user1, user3);
    }

    @Test
    void testPasswordNotIncludedInToString() {
        UserEntity entity = UserEntity.builder()
                .id(1L)
                .firstName("Secure")
                .lastName("User")
                .username("secure.user")
                .password("secretPassword".toCharArray())
                .isActive(true)
                .build();

        String entityString = entity.toString();

        assertFalse(entityString.contains("secretPassword"));
        assertFalse(entityString.contains("password="));
    }

    @Test
    void testMappingWithSpecialCharactersInNames() {
        UserEntity entity = UserEntity.builder()
                .id(1L)
                .firstName("Jean-Pierre")
                .lastName("O'Connor")
                .username("jp.oconnor")
                .password("pass".toCharArray())
                .isActive(true)
                .build();

        User model = userMapper.toModel(entity);
        UserEntity entityAgain = userMapper.toEntity(model);

        assertEquals("Jean-Pierre", model.getFirstName());
        assertEquals("O'Connor", model.getLastName());
        assertEquals("Jean-Pierre", entityAgain.getFirstName());
        assertEquals("O'Connor", entityAgain.getLastName());
    }

    @Test
    void testMultipleUsersInList() {
        List<UserEntity> entities = Arrays.asList(
                createUserEntity(1L, "Alice", "Anderson", "alice.a", true),
                createUserEntity(2L, "Bob", "Brown", "bob.b", false),
                createUserEntity(3L, "Charlie", "Clark", "charlie.c", true),
                createUserEntity(4L, "Diana", "Davis", "diana.d", false),
                createUserEntity(5L, "Eve", "Evans", "eve.e", true)
        );

        List<User> models = userMapper.toModels(entities);

        assertEquals(5, models.size());
        assertEquals("alice.a", models.get(0).getUsername());
        assertEquals("bob.b", models.get(1).getUsername());
        assertEquals("charlie.c", models.get(2).getUsername());
        assertEquals("diana.d", models.get(3).getUsername());
        assertEquals("eve.e", models.get(4).getUsername());
    }

    private UserEntity createUserEntity(Long id, String firstName, String lastName,
                                        String username, boolean isActive) {
        return UserEntity.builder()
                .id(id)
                .firstName(firstName)
                .lastName(lastName)
                .username(username)
                .password("password".toCharArray())
                .isActive(isActive)
                .build();
    }
}
