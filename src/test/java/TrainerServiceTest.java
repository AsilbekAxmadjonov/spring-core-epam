import org.example.dao.TrainerDao;
import org.example.model.Trainer;
import org.example.service.TrainerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainerServiceTest {

    @Mock
    private TrainerDao trainerDao;

    @InjectMocks
    private TrainerService trainerService;

    private Trainer trainer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        trainer = new Trainer("trainer_1", "Alice", "Smith", "Fitness");
    }

    @Test
    void testCreateTrainer() {
        trainerService.createTrainer(trainer);
        verify(trainerDao, times(1)).save(trainer);
    }

    @Test
    void testUpdateTrainer() {
        trainerService.updateTrainer(trainer);
        verify(trainerDao, times(1)).update(trainer);
    }

    @Test
    void testGetTrainer() {
        when(trainerDao.findByUsername("trainer_1")).thenReturn(trainer);

        Trainer result = trainerService.getTrainer("trainer_1");

        assertNotNull(result);
        assertEquals("Alice", result.getFirstName());
        assertEquals("Fitness", result.getSpecialization());
        verify(trainerDao, times(1)).findByUsername("trainer_1");
    }

    @Test
    void testListAll() {
        when(trainerDao.findAll()).thenReturn(List.of(trainer));

        List<Trainer> trainers = trainerService.listAll();

        assertEquals(1, trainers.size());
        assertEquals("trainer_1", trainers.get(0).getUsername());
        assertEquals("Fitness", trainers.get(0).getSpecialization());
        verify(trainerDao, times(1)).findAll();
    }
}
