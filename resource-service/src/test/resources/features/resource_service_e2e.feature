Feature: Resource Controller E2E

  @E2E
  Scenario: Upload a file
    Given a valid audio file
    When the file is uploaded
    Then the response should contain 200 status
    Then the response should contain application-json content-type header
    Then the response should contain the file's id

  @E2E
  Scenario: Retrieve a file
    Given a valid file exists
    When the file is retrieved by ID
    Then the response should contain the file's data
    Then the response should contain content-type audio-mpeg