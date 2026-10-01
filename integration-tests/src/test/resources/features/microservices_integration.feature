Feature: Integration between gym-crm-service and trainer-workload-service

  Scenario: Scheduling a training eventually updates the trainer's workload hours
    Given a registered trainer "Ivan" "Pro" with specialization "FITNESS" on the live gym-crm service
    And a registered trainee "Olga" "Student" on the live gym-crm service
    When the trainer schedules a training named "Cross training" in 3 days with duration 50
    Then the training response status is 200
    And within 10 seconds the trainer-workload-service reports duration 50 for that training's month

  Scenario: Cancelling a future training eventually decreases the trainer's workload hours
    Given a registered trainer "Petro" "Strong" with specialization "STRENGTH" on the live gym-crm service
    And a registered trainee "Olena" "Learner" on the live gym-crm service
    And the trainer has scheduled a training named "Strength session" in 3 days with duration 40
    When the trainer cancels that training
    Then the training response status is 204
    And within 10 seconds the trainer-workload-service reports duration 0 for that training's month

  Scenario: A failed training creation never reaches the trainer-workload-service
    Given a registered trainer "Hugo" "Solid" with specialization "CARDIO" on the live gym-crm service
    When the trainer schedules a training for unknown trainee "ghost.user" named "Ghost run" in 3 days with duration 20
    Then the training response status is 404
    And the trainer-workload-service has no record for that trainer
