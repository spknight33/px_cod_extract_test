Feature: Organisation Extract_Load DB test feature

  Background: 
    Given An initial extract_load run
    And the database is empty
    When extract_load run is completed

  # TODO - volumes to be defined
  Scenario: DB test initial load sanity counts
    Then the database contains at least 1 "organisation" records
    And the database contains at least 1 "organisation_relation" records
    And there are more "organisation_relation" records than "organisation"  records

  # AT record type is 27
  # PCO record type is 46
  # Contractor type is 8
  # todo - need to differentiate Dispensing from appliance contrators
  Scenario: DB test initial load record types from db2
    Then the database contains "AT" record types
    And the database contains "PCO" record types
    And the database contains "Contractor" record types
    
     Scenario: DB test initial load only record types expected are present
    Then the database contains only expected record types

  #And the database contains "Dispensing" and "Appliance" contractors
  # Pharmacy-YP type is arbitary 1337, chosen by developer
  # Pharnacy-Grouping type is arbitary 1338, chosen by developer
  Scenario: DB test initial load record types from DPC
    Then the database contains "Pharmacy-YP" record types
    Then the database contains "Pharmacy-Grouping" record types



  # TODO - check with Andrew if this is always true? including DPC
  #   Scenario: DB test initial load record data integrity
  #  Then the "organisation" records will have data in all "addressline1" fields
  # We can check the parent child relations for a typical pharmacy
  Scenario: DB test specific structure
    Then the database contains "organisation" record for code "Q98"
    Then the database contains "organisation" record for code "07Z00"
    And the database contains "organisation_relation" record for parent "Q98" and child "07Z00"
