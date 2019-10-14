Feature: MES

Narrative:

As a user
I want to perform an action

Scenario: 1 Connect to process engine
Given that fulfilment process prepare-batch has been deployed into the process engine
When a user triggers execution of the prepare-batch daily process processurl
Then the process should successfully complete 200

Scenario: 2 Connect start process check 
Given that  process batch-start-new has been deployed into the process engine
When a user triggers execution of the batch-start-new daily process processurl
Then the process-start-new should successfully complete 200

Scenario: 3 Validate batch file with status 'New' is being picked for execution
Given that  process validate-new has been deployed into the process engine
When a user triggers execution of the validate-new daily process processurl
Then the validate-new should successfully complete 200

Scenario: 4 Validate batch file with status 'Rerun' is being picked for execution
Given that  process validate-rerun has been deployed into the process engine
When a user triggers execution of the validate-rerun daily process processurl
Then the validate-rerun should successfully complete 200