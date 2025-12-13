package org.example.services.impl.dbImpl;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.TrainingTypeEntity;
import org.example.mapper.TrainingTypeMapper;
import org.example.model.TrainingType;
import org.example.repository.TrainingTypeRepo;
import org.example.services.TrainingTypeService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Primary
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TrainingTypeServiceDbImpl implements TrainingTypeService {

    private final TrainingTypeRepo trainingTypeRepo;
    private final TrainingTypeMapper trainingTypeMapper;

    @PostConstruct
    @Transactional
    public void initializeTrainingTypes() {
        if (trainingTypeRepo.count() == 0) {
            List<String> types = List.of(
                    "Fitness", "Yoga", "Zumba", "Stretching",
                    "Resistance", "Strength", "Cardio"
            );

            types.forEach(typeName -> {
                TrainingTypeEntity type = new TrainingTypeEntity();
                type.setTrainingTypeName(typeName);
                trainingTypeRepo.save(type);
            });

            log.info("Initialized training types: {}", types);
        } else {
            log.debug("Training types already initialized, count: {}", trainingTypeRepo.count());
        }
    }

    @Override
    public Optional<TrainingType> getTrainingTypeByName(String name) {
        log.debug("Fetching training type by name: {}", name);
        return trainingTypeRepo.findByTrainingTypeName(name)
                .map(entity -> {
                    log.debug("Training type found: {}", name);
                    return trainingTypeMapper.toModel(entity);
                });
    }

    @Override
    public List<TrainingType> getAllTrainingTypes() {
        log.debug("Fetching all training types");

        List<TrainingTypeEntity> entities = trainingTypeRepo.findAll();
        List<TrainingType> trainingTypes = trainingTypeMapper.toModels(entities);

        log.info("Total training types fetched: {}", trainingTypes.size());
        return trainingTypes;
    }
}