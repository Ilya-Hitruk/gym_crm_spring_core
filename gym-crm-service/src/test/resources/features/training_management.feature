Feature: Training scheduling and cancellation

  Background:
    Given a registered trainer "Mia" "Trainer" with specialization "YOGA"
    And a registered trainee "Leo" "Client"

  Scenario: Trainer schedules a future training
    When the trainer schedules a training named "Morning session" in 5 days with duration 60
    Then the response status is 200
    And the training response contains an id

  Scenario: Scheduling a training fails for an unknown trainee
    When the trainer schedules a training for unknown trainee "ghost.nobody" named "Ghost session" in 5 days with duration 60
    Then the response status is 404

  Scenario: Scheduling a training fails when duration is missing
    When the trainer schedules a training named "Broken session" in 5 days with no duration
    Then the response status is 400

  Scenario: Trainer cancels a scheduled future training
    Given the trainer has scheduled a training named "Evening session" in 5 days with duration 45
    When the trainer cancels that training
    Then the response status is 204

  Scenario: Cancelling an already past training is rejected
    Given the trainer has scheduled a training named "Past session" 5 days ago with duration 30
    When the trainer cancels that training
    Then the response status is 409

  Scenario: Cancelling an unknown training is rejected
    When the trainer cancels training with id 999999
    Then the response status is 404
