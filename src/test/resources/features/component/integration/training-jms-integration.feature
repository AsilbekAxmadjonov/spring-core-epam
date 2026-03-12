Feature: Training JMS integration

  Scenario: Create training and publish workload event
    Given training type "Yoga" exists
    And trainee "trainee.user" exists
    And trainer "trainer.user" exists with specialization "Yoga"
    When the client sends POST request to create training
    Then the training response status should be 201
    And workload event should be published to JMS queue
    And workload event username should be "trainer.user"
    And workload event action type should be "ADD"
    And workload event training duration should be 60