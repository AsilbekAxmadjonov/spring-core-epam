package org.example.cucumber.component.authentication;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.example.api.dto.request.LoginRequest;
import org.example.persistance.entity.UserEntity;
import org.example.persistance.repository.UserRepo;
import org.example.persistance.repository.TraineeRepo;
import org.example.persistance.repository.TrainerRepo;
import org.example.persistance.repository.TrainingRepo;
import org.example.persistance.repository.TrainingTypeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class AuthenticationComponentSteps {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

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

    private ResponseEntity<Map> response;

    @Before
    public void cleanDb() {
        // Keep deletion order consistent with training component steps to avoid FK violations:
        // training → trainee → trainer → users → training_type
        trainingRepo.deleteAll();
        traineeRepo.deleteAll();
        trainerRepo.deleteAll();
        userRepo.deleteAll();
        trainingTypeRepo.deleteAll();
        response = null;
    }

    @Given("a user {string} exists with password {string}")
    public void a_user_exists_with_password(String username, String password) {
        UserEntity user = UserEntity.builder()
                .firstName("John")
                .lastName("Doe")
                .username(username)
                .password(passwordEncoder.encode(password).toCharArray())
                .isActive(true)
                .build();

        userRepo.save(user);
    }

    @When("the client sends POST request to login with username {string} and password {string}")
    public void the_client_sends_post_request_to_login_with_username_and_password(String username, String password) {
        LoginRequest request = LoginRequest.builder()
                .username(username)
                .password(password.toCharArray())
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<LoginRequest> entity = new HttpEntity<>(request, headers);

        response = restTemplate.exchange(
                baseUrl() + "/api/auth/login",
                HttpMethod.POST,
                entity,
                Map.class
        );
    }

    @When("the client sends POST request to login with username {string} and password null")
    public void the_client_sends_post_request_to_login_with_username_and_password_null(String username) {
        LoginRequest request = LoginRequest.builder()
                .username(username)
                .password(null)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<LoginRequest> entity = new HttpEntity<>(request, headers);

        response = restTemplate.exchange(
                baseUrl() + "/api/auth/login",
                HttpMethod.POST,
                entity,
                Map.class
        );
    }

    @Then("the auth response status should be {int}")
    public void the_auth_response_status_should_be(Integer status) {
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode().value()).isEqualTo(status);
    }

    @Then("the login should be successful")
    public void the_login_should_be_successful() {
        assertThat(response).isNotNull();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("success")).isEqualTo(true);
    }

    @Then("the auth response should contain token")
    public void the_auth_response_should_contain_token() {
        assertThat(response).isNotNull();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("token")).isNotNull();
    }

    @Then("the auth response username should be {string}")
    public void the_auth_response_username_should_be(String username) {
        assertThat(response).isNotNull();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("username")).isEqualTo(username);
    }

    private String baseUrl() {
        return "http://localhost:" + port;
    }
}