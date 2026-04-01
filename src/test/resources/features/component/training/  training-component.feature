Feature: Training component tests

  Scenario: Create training successfully
    Given trainee "trainee1" exists
    And trainer "trainer1" exists
    And training type "Yoga" exists
    And a valid training creation request
    When the client sends POST request to create training
    Then the response status should be 201
    And the response training name should be "Morning Yoga"
    And workload event should be published once

  Scenario: Reject training creation with missing trainee username
    Given a training request without trainee username
    When the client sends POST request to create training
    Then the response status should be 400

  Scenario: Return not found when trainee does not exist
    Given trainer "trainer1" exists
    And training type "Yoga" exists
    And a valid training creation request
    When the client sends POST request to create training
    Then the response status should be 404

  Scenario: Return all trainings
    Given trainee "trainee1" exists
    And trainer "trainer1" exists
    And training type "Yoga" exists
    And an existing training named "Morning Yoga"
    When the client sends GET request for all trainings
    Then the response status should be 200
    And the response should contain 1 trainings

  Scenario: Return training by name
    Given trainee "trainee1" exists
    And trainer "trainer1" exists
    And training type "Yoga" exists
    And an existing training named "Morning Yoga"
    When the client sends GET request for training "Morning Yoga"
    Then the response status should be 200
    And the response training name should be "Morning Yoga"

  Scenario: Return not found for unknown training
    When the client sends GET request for training "Unknown Training"
    Then the response status should be 404