Feature: Trainee and trainer registration

  Scenario: Successfully register a new trainee
    When I register a new trainee with first name "Anna" and last name "Client"
    Then the response status is 200
    And the registration response contains a username and a password

  Scenario: Successfully register a new trainer
    When I register a new trainer with first name "Alex" and last name "Coach" and specialization "FITNESS"
    Then the response status is 200
    And the registration response contains a username and a password

  Scenario: Registration fails when trainee first name is missing
    When I register a new trainee with first name "" and last name "Client"
    Then the response status is 400

  Scenario: Registration fails when trainer specialization is missing
    When I register a new trainer with first name "Alex" and last name "Coach" and specialization ""
    Then the response status is 400
