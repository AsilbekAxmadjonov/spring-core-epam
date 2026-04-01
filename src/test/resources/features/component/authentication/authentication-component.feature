Feature: Authentication component tests

  Scenario: Login successfully with valid credentials
    Given a user "john.doe" exists with password "password1234"
    When the client sends POST request to login with username "john.doe" and password "password1234"
    Then the auth response status should be 200
    And the login should be successful
    And the auth response should contain token
    And the auth response username should be "john.doe"

  Scenario: Reject login with wrong password
    Given a user "john.doe" exists with password "password1234"
    When the client sends POST request to login with username "john.doe" and password "wrongpass123"
    Then the auth response status should be 401

  Scenario: Reject login for unknown user
    When the client sends POST request to login with username "unknown.user" and password "password1234"
    Then the auth response status should be 401

  Scenario: Reject login with missing username
    When the client sends POST request to login with username "" and password "password1234"
    Then the auth response status should be 400

  Scenario: Reject login with missing password
    When the client sends POST request to login with username "john.doe" and password null
    Then the auth response status should be 400