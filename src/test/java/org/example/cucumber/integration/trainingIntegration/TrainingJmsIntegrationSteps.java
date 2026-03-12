package org.example.cucumber.integration.trainingIntegration;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.jms.Message;
import jakarta.jms.TextMessage;
import org.example.api.dto.request.TrainingRequest;
import org.example.integration.messaging.Queues;
import org.example.integration.messaging.WorkloadEventMessage;
import org.example.persistance.entity.TraineeEntity;
import org.example.persistance.entity.TrainerEntity;
import org.example.persistance.entity.TrainingTypeEntity;
import org.example.persistance.entity.UserEntity;
import org.example.persistance.repository.TraineeRepo;
import org.example.persistance.repository.TrainerRepo;
import org.example.persistance.repository.TrainingRepo;
import org.example.persistance.repository.TrainingTypeRepo;
import org.example.persistance.repository.UserRepo;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;

public class TrainingJmsIntegrationSteps {

    @LocalServerPort
    private int port;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private TraineeRepo traineeRepo;

    @Autowired
    private TrainerRepo trainerRepo;

    @Autowired
    private TrainingRepo trainingRepo;

    @Autowired
    private TrainingTypeRepo trainingTypeRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private final RestTemplate restTemplate = new RestTemplate();

    private ResponseEntity<String> response;
    private WorkloadEventMessage workloadEventMessage;

    @Before
    public void cleanUp() {
        trainingRepo.deleteAll();
        traineeRepo.deleteAll();
        trainerRepo.deleteAll();
        userRepo.deleteAll();
        trainingTypeRepo.deleteAll();

        jmsTemplate.setReceiveTimeout(1000);
        purgeQueue();
    }

    @Given("training type {string} exists")
    @Transactional
    public void training_type_exists(String trainingTypeName) {
        TrainingTypeEntity type = TrainingTypeEntity.builder()
                .trainingTypeName(trainingTypeName)
                .build();

        trainingTypeRepo.save(type);
    }

    @Given("trainee {string} exists")
    public void trainee_exists(String username) {
        UserEntity traineeUser = UserEntity.builder()
                .firstName("Trainee")
                .lastName("User")
                .username(username)
                .password(passwordEncoder.encode("password1234").toCharArray())
                .isActive(true)
                .build();

        traineeUser = userRepo.save(traineeUser);

        TraineeEntity trainee = TraineeEntity.builder()
                .userEntity(traineeUser)
                .address("Tashkent")
                .dateOfBirth(LocalDate.of(2000, 1, 1))
                .build();

        traineeRepo.save(trainee);
    }

    @Given("trainer {string} exists with specialization {string}")
    @Transactional
    public void trainer_exists_with_specialization(String username, String specializationName) {
        TrainingTypeEntity specialization = trainingTypeRepo.findByTrainingTypeName(specializationName)
                .orElseThrow(() -> new IllegalStateException("Training type not found: " + specializationName));

        UserEntity trainerUser = UserEntity.builder()
                .firstName("Trainer")
                .lastName("User")
                .username(username)
                .password(passwordEncoder.encode("password1234").toCharArray())
                .isActive(true)
                .build();

        trainerUser = userRepo.save(trainerUser);

        TrainerEntity trainer = TrainerEntity.builder()
                .userEntity(trainerUser)
                .specialization(specialization)
                .build();

        trainerRepo.save(trainer);
    }

    @When("the client sends POST request to create training")
    public void the_client_sends_post_request_to_create_training() {
        TrainingRequest request = TrainingRequest.builder()
                .traineeUsername("trainee.user")
                .trainerUsername("trainer.user")
                .trainingName("Morning Yoga")
                .trainingType("Yoga")
                .trainingDate(LocalDate.of(2026, 3, 12))
                .trainingDurationMinutes(60)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<TrainingRequest> entity = new HttpEntity<>(request, headers);

        response = restTemplate.exchange(
                "http://localhost:" + port + "/api/trainings",
                HttpMethod.POST,
                entity,
                String.class
        );
    }

    @Then("the training response status should be {int}")
    public void the_training_response_status_should_be(Integer status) {
        Assertions.assertNotNull(response);
        Assertions.assertEquals(status, response.getStatusCode().value());
    }

    @And("workload event should be published to JMS queue")
    public void workload_event_should_be_published_to_jms_queue() throws Exception {
        Message message = jmsTemplate.receive(Queues.WORKLOAD_EVENTS);

        Assertions.assertNotNull(message, "Expected JMS message but queue was empty");
        Assertions.assertInstanceOf(TextMessage.class, message);

        String json = ((TextMessage) message).getText();
        workloadEventMessage = objectMapper.readValue(json, WorkloadEventMessage.class);

        Assertions.assertNotNull(workloadEventMessage);
        Assertions.assertNotNull(workloadEventMessage.getEventId());
        Assertions.assertNotNull(workloadEventMessage.getRequest());
    }

    @And("workload event username should be {string}")
    public void workload_event_username_should_be(String username) {
        Assertions.assertNotNull(workloadEventMessage);
        Assertions.assertEquals(username, workloadEventMessage.getRequest().getUsername());
    }

    @And("workload event action type should be {string}")
    public void workload_event_action_type_should_be(String actionType) {
        Assertions.assertNotNull(workloadEventMessage);
        Assertions.assertEquals(
                actionType,
                workloadEventMessage.getRequest().getActionType().name()
        );
    }

    @And("workload event training duration should be {int}")
    public void workload_event_training_duration_should_be(Integer duration) {
        Assertions.assertNotNull(workloadEventMessage);
        Assertions.assertEquals(duration, workloadEventMessage.getRequest().getTrainingDurationMinutes());
    }

    private void purgeQueue() {
        while (true) {
            Message message = jmsTemplate.receive(Queues.WORKLOAD_EVENTS);
            if (message == null) {
                break;
            }
        }
    }
}