package org.example.services.impl.dbImpl;

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