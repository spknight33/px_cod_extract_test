Feature: Organisation Extract_Load DB test feature

  Background: 
    # Note that these are really pre-conditions
    Given An initial extract_load run
    And the database is empty
    When extract_load run is completed

  # Exact Volumes will vary depending on which cip extract is used, so the volumes here
  # values that should apply to any source
  # Regardless of voulmes there will always be more relation records than organisation records
  Scenario: DB test initial load sanity counts
    Then the database contains at least 1 "organisation" records
    And the database contains at least 1 "organisation_relation" records
    And there are more "organisation_relation" records than "organisation"  records

  # These are the source types which come from the CIP part of the Cod extract
  # AT record type is 37
  # PCO record type is 46
  # Contractor type is 8
  # Division type is 31
  Scenario: DB test initial load record types from db2
    Then the database contains "AT" record type
    And the database contains "PCO" record type
    And the database contains "Contractor" record type
    And the database contains "Division" record type

  # Check that we dont have any other than the expected record types
  Scenario: DB test initial load only record types expected are present
    Then the database contains only expected record types

  Scenario: DB test initial load no duplicate organisation records
    Then there are no duplicate organisation records

  # These are the source types which come from the dpc part of the Cod extract
  # Pharmacy-YP type is arbitary 1337, chosen by developer
  # Pharnacy-Grouping type is arbitary 1338, chosen by developer
  Scenario: DB test initial load record types from DPC
    Then the database contains "Pharmacy-YP" record type
    Then the database contains "Pharmacy-Grouping" record type

  # TODO - check with Andrew if this is always true? including DPC
  # apparently address line can be null in some case, so cant use this test
  #   Scenario: DB test initial load record data integrity
  #  Then the "organisation" records will have data in all "addressline1" fields
  # We can check the parent child relations for a specific pharmacy
  Scenario Outline: DB test specific structures
    Then the database contains "<parent_code>" of "<parent_type>"
    And the database contains "<child_code>" of "<child_type>"
    And the database contains a relation record between "<parent_code>" and "<child_code>"

    Examples: 
      | parent_code | parent_type       | child_code | child_type |
      | Q98         | AT                | 07Z00      | PCO        |
      | 07Z00       | PCO               | FXXXX      | Contractor |
      | ES00106     | Pharmacy-Grouping | FHE42      | Contractor |
      | ES00106     | Pharmacy-Grouping | FHL56      | Contractor |
      | ES00106     | Pharmacy-Grouping | FFG82      | Contractor |
      | ES00106     | Pharmacy-Grouping | FA676      | Contractor |

  # Check that the Bulk load tables are cleared down
  Scenario: DB test initial load Bulk control tables are empty at end
    Then the database contains exactly 0 "Bi_organisation" records
    Then the database contains exactly 0 "Bi_organisation_relation" records

  # Check that inactive cases on DPC are not loaded as part of extract_load
  Scenario Outline: DB test inactive organisations on DPC
    Then the database does not contain organisation for "<org_type>" for inactive code "<code>"

    # ES00021 (KentPharm) is inactive on production & all relations are also inactive
    # ES00011 (Co-operative) is inactive on production with active relation (yp02747)
    #
    Examples: 
      | org_type          | code    |
      | Pharmacy-Grouping | ES00021 |
      | Pharmacy-Grouping | ES00011 |

  # Check that inactive relations on DPC are not loaded, even if the parent org is active
  # ES00034 (superdrug) is active, but relation to yp02787 is inactive
  Scenario Outline: DB test inactive relation with active organisation not loaded
    Then the database contains "<parent_code>" of "<parent_type>"
    And the database does not contain a relation record between "<parent_code>" and "<child_code>"

    Examples: 
      | parent_code | parent_type       | child_code | child_type |
      | ES00034     | Pharmacy-Grouping | YP02787    | Contractor |
