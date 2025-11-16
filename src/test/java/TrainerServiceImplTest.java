import org.example.dao.TrainerDao;
import org.example.model.Trainer;
import org.example.service.impl.TrainerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainerServiceImplTest {

    private TrainerDao trainerDao;
    private TrainerServiceImpl trainerService;

    @BeforeEach
    void setUp() {
        trainerDao = mock(TrainerDao.class);
        trainerService = new TrainerServiceImpl();
        trainerService.setTrainerDao(trainerDao);
    }

    private Trainer createTrainer(String username, String first, String last, String specialization) {
        return Trainer.builder()
                .username(username)
                .firstName(first)
                .lastName(last)
                .password("123".toCharArray())
                .specialization(specialization)
                .isActive(true)
                .build();
    }

    @Test
    void testCreateTrainer() {
        Trainer trainer = createTrainer("trainer01", "John", "Doe", "Backend");

        trainerService.createTrainer(trainer);

        verify(trainerDao, times(1)).save(trainer);
    }

    @Test
    void testUpdateTrainer() {
        Trainer trainer = createTrainer("trainer02", "Anna", "Smith", "Frontend");

        trainerService.updateTrainer(trainer);

        verify(trainerDao, times(1)).update(trainer);
    }

    @Test
    void testGetTrainer() {
        Trainer trainer = createTrainer("trainer03", "Mike", "Brown", "Data Science");
        when(trainerDao.findByUsername("trainer03")).thenReturn(trainer);

        Trainer result = trainerService.getTrainer("trainer03");

        assertNotNull(result);
        assertEquals("Mike", result.getFirstName());
        verify(trainerDao, times(1)).findByUsername("trainer03");
    }

    @Test
    void testListAll() {
        Trainer t1 = createTrainer("t1", "Alice", "White", "ML");
        Trainer t2 = createTrainer("t2", "Bob", "Green", "DevOps");

        when(trainerDao.findAll()).thenReturn(List.of(t1, t2));

        List<Trainer> trainers = trainerService.listAll();

        assertEquals(2, trainers.size());
        assertTrue(trainers.contains(t1));
        assertTrue(trainers.contains(t2));

        verify(trainerDao, times(1)).findAll();
    }

    @Test
    void testCreateTrainer_ArgumentPassedCorrectly() {
        Trainer trainer = createTrainer("check01", "Test", "Trainer", "Cloud");

        trainerService.createTrainer(trainer);

        ArgumentCaptor<Trainer> captor = ArgumentCaptor.forClass(Trainer.class);
        verify(trainerDao).save(captor.capture());

        assertEquals("check01", captor.getValue().getUsername());
        assertEquals("Cloud", captor.getValue().getSpecialization());
    }
}
