Feature: ResourceService Component Scenarios
  As a developer
  I want to test the ResourceService component
  So that I can ensure it behaves correctly under various business scenarios

  @Component
  Scenario: Successfully save a file
    Given a valid file
    When the file is saved
    Then the file should be uploaded to S3 with identifier
    And the file identifier should be saved in the database
    And the returned ResourceDTO should have id

  @Component
  Scenario: Fail to save a file when S3 upload fails
    Given a valid file
    When the file is saved with null value
    Then a ResourceSaveException should be thrown
