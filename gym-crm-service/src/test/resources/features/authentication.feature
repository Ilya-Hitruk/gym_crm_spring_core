Feature: Trainer authentication

  Background:
    Given a registered trainer "Nina" "Fit" with specialization "CARDIO"

  Scenario: Login succeeds with correct credentials
    When the trainer logs in with the correct password
    Then the response status is 200
    And the login response contains a token

  Scenario: Login fails with wrong password
    When the trainer logs in with password "wrong-password"
    Then the response status is 401
