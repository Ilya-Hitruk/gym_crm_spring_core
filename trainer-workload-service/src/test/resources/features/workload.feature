Feature: Trainer workload tracking

  Scenario: A valid ADD event increases the recorded duration
    Given a workload ADD event for trainer "Nina.Fit" "Nina" "Fit" active true, date "2026-09-05", duration 45
    When I request the workload summary for trainer "Nina.Fit"
    Then the response status is 200
    And the summary for year 2026 month 9 shows duration 45

  Scenario: A second ADD event for the same month accumulates duration
    Given a workload ADD event for trainer "Omar.Bold" "Omar" "Bold" active true, date "2026-09-05", duration 30
    And a workload ADD event for trainer "Omar.Bold" "Omar" "Bold" active true, date "2026-09-15", duration 20
    When I request the workload summary for trainer "Omar.Bold"
    Then the response status is 200
    And the summary for year 2026 month 9 shows duration 50

  Scenario: A valid DELETE event decreases the recorded duration
    Given a workload ADD event for trainer "Leo.Pro" "Leo" "Pro" active true, date "2026-09-05", duration 60
    And a workload DELETE event for trainer "Leo.Pro" "Leo" "Pro" active true, date "2026-09-05", duration 20
    When I request the workload summary for trainer "Leo.Pro"
    Then the response status is 200
    And the summary for year 2026 month 9 shows duration 40

  Scenario: Requesting workload for unknown trainer returns 404
    When I request the workload summary for trainer "unknown.trainer"
    Then the response status is 404

  Scenario: A malformed message never creates a workload record
    Given a malformed workload message is sent to the queue
    When I request the workload summary for trainer "nobody.at.all"
    Then the response status is 404

  Scenario: Requesting workload without a valid token is rejected
    When I request the workload summary for trainer "Nina.Fit" without authorization
    Then the response status is 401
