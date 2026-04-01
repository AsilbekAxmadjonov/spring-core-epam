package org.example.cucumber.component.training;

import io.cucumber.java.Before;
import io.cucumber.java.en.*;
import org.example.api.dto.request.TrainingRequest;
import org.example.integration.messaging.WorkloadEventPublisher;
import org.example.persistance.entity.*;
import org.example.persistance.repository.TraineeRepo;
import org.example.persistance.repository.TrainerRepo;
import org.example.persistance.repository.TrainingRepo;
import org.example.persistance.repository.TrainingTypeRepo;
import org.example.persistance.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class TrainingComponentSteps {

    @LocalServerPort
    private int port;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private TraineeRepo traineeRepo;

    @Autowired
    private TrainerRepo trainerRepo;

    @Autowired
    private TrainingTypeRepo trainingTypeRepo;

    @Autowired
    private TrainingRepo trainingRepo;

    @Autowired
    private WorkloadEventPublisher workloadEventPublisher;

    private final RestTemplate restTemplate = new RestTemplate();

    private TrainingRequest request;
    private ResponseEntity<Map> mapResponse;
    private ResponseEntity<List> listResponse;

    public TrainingComponentSteps() {
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            protected boolean hasError(org.springframework.http.HttpStatusCode statusCode) {
                return false;
            }
        });
    }

    @Before
    public void cleanDb() {
        trainingRepo.deleteAll();
        traineeRepo.deleteAll();
        trainerRepo.deleteAll();
        userRepo.deleteAll();
        trainingTypeRepo.deleteAll();
        reset(workloadEventPublisher);

        request = null;
        mapResponse = null;
        listResponse = null;
    }

    @Given("trainee {string} exists")
    public void trainee_exists(String username) {
        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setFirstName("First");
        user.setLastName("Last");
        user.setPassword("password123".toCharArray());
        user.setIsActive(true);
        userRepo.save(user);

        TraineeEntity trainee = new TraineeEntity();
        trainee.setUserEntity(user);
        traineeRepo.save(trainee);
    }

    @Given("trainer {string} exists")
    @Transactional
    public void trainer_exists(String username) {
        TrainingTypeEntity specialization = trainingTypeRepo.findByTrainingTypeName("Yoga")
                .orElseGet(() -> trainingTypeRepo.save(
                        TrainingTypeEntity.builder().trainingTypeName("Yoga").build()
                ));

        UserEntity trainerUser = new UserEntity();
        trainerUser.setFirstName("Jane");
        trainerUser.setLastName("Smith");
        trainerUser.setUsername(username);
        trainerUser.setPassword("password123".toCharArray());
        trainerUser.setIsActive(true);
        userRepo.save(trainerUser);

        TrainerEntity trainer = TrainerEntity.builder()
                .specialization(specialization)
                .userEntity(trainerUser)
                .build();

        trainerRepo.save(trainer);
    }

    @Given("training type {string} exists")
    public void training_type_exists(String typeName) {
        trainingTypeRepo.findByTrainingTypeName(typeName)
                .orElseGet(() -> trainingTypeRepo.save(
                        TrainingTypeEntity.builder()
                                .trainingTypeName(typeName)
                                .build()
                ));
    }

    @Given("a valid training creation request")
    public void a_valid_training_creation_request() {
        request = TrainingRequest.builder()
                .traineeUsername("trainee1")
                .trainerUsername("trainer1")
                .trainingName("Morning Yoga")
                .trainingType("Yoga")
                .trainingDate(LocalDate.now().plusDays(1))
                .trainingDurationMinutes(60)
                .build();
    }

    @Given("a training request without trainee username")
    public void a_training_request_without_trainee_username() {
        request = TrainingRequest.builder()
                .traineeUsername("")
                .trainerUsername("trainer1")
                .trainingName("Morning Yoga")
                .trainingType("Yoga")
                .trainingDate(LocalDate.now().plusDays(1))
                .trainingDurationMinutes(60)
                .build();
    }

    @Given("an existing training named {string}")
    public void an_existing_training_named(String trainingName) {
        TraineeEntity trainee = traineeRepo.findByUsername("trainee1").orElseThrow();
        TrainerEntity trainer = trainerRepo.findByUsername("trainer1").orElseThrow();
        TrainingTypeEntity trainingType = trainingTypeRepo.findByTrainingTypeName("Yoga").orElseThrow();

        TrainingEntity entity = TrainingEntity.builder()
                .traineeEntity(trainee)
                .trainerEntity(trainer)
                .trainingName(trainingName)
                .trainingTypeEntity(trainingType)
                .trainingDate(LocalDate.now().plusDays(1))
                .trainingDurationMinutes(60)
                .build();

        trainingRepo.save(entity);
    }

    @When("the client sends POST request to create training")
    public void the_client_sends_post_request_to_create_training() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<TrainingRequest> entity = new HttpEntity<>(request, headers);

        mapResponse = restTemplate.exchange(
                baseUrl() + "/api/trainings",
                HttpMethod.POST,
                entity,
                Map.class
        );
    }

    @When("the client sends GET request for all trainings")
    public void the_client_sends_get_request_for_all_trainings() {
        listResponse = restTemplate.exchange(
                baseUrl() + "/api/trainings",
                HttpMethod.GET,
                null,
                List.class
        );
    }

    @When("the client sends GET request for training {string}")
    public void the_client_sends_get_request_for_training(String trainingName) {
        mapResponse = restTemplate.exchange(
                baseUrl() + "/api/trainings/" + trainingName,
                HttpMethod.GET,
                null,
                Map.class
        );
    }

    @Then("the response status should be {int}")
    public void the_response_status_should_be(Integer status) {
        if (mapResponse != null) {
            assertThat(mapResponse.getStatusCode().value()).isEqualTo(status);
        } else {
            assertThat(listResponse.getStatusCode().value()).isEqualTo(status);
        }
    }

    @Then("the response training name should be {string}")
    public void the_response_training_name_should_be(String trainingName) {
        assertThat(mapResponse.getBody()).isNotNull();
        assertThat(mapResponse.getBody().get("trainingName")).isEqualTo(trainingName);
    }

    @Then("the response should contain {int} trainings")
    public void the_response_should_contain_trainings(Integer count) {
        assertThat(listResponse.getBody()).hasSize(count);
    }

    @Then("workload event should be published once")
    public void workload_event_should_be_published_once() {
        verify(workloadEventPublisher, times(1)).publish(any());
    }

    private String baseUrl() {
        return "http://localhost:" + port;
    }
}