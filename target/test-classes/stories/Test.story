Feature: MES

Narrative:

As a user
I want to perform an action

Scenario: 1 Connect to process engine
Given that fulfilment process prepare-batch has been deployed into the process engine http://192.168.7.147:8080
When a user triggers execution of the prepare-batch daily process processurl /engine-rest/process-definition/key/prepare-batch
Then the process should successfully complete 200

Scenario: 2 Connect start process check 
Given that  process batch-start-new has been deployed into the process engine http://192.168.7.147:8080
When a user triggers execution of the batch-start-new daily process processurl /engine-rest/process-definition/key/prepare-batch/start
Then the process-start-new should successfully complete 200

Scenario: 3 Validate batch file with status 'New' is being picked for execution 
Given that  process validate-new has been deployed into the process engine http://192.168.7.147:8080
When a user triggers execution of the validate-new daily process processurl /engine-rest/process-definition/key/prepare-batch/start
Then the validate-new should successfully complete 200

Scenario: 4 Validate batch file with status 'Rerun' is being picked for execution
Given that  process validate-rerun has been deployed into the process engine http://192.168.7.147:8080
When a user triggers execution of the validate-rerun daily process processurl /engine-rest/process-definition/key/prepare-batch/start
Then the validate-rerun should successfully complete 200

Scenario: 5 Validate batch file with status 'Picked' is not picked for execution
Given that  process validate-picked has been deployed into the process engine http://192.168.7.147:8080
When a user triggers execution of the validate-picked daily process processurl /engine-rest/process-definition/key/prepare-batch/start
Then the validate-picked should successfully complete 200

Scenario: 6 Validate if an execution instance has been created for a picked batch
Given that  process instance-picked has been deployed into the process engine http://192.168.7.147:8080
When a user triggers execution of the instance-picked daily process processurl /engine-rest/process-definition/key/prepare-batch/start
Then the instance-picked should successfully complete 200

Scenario: 7 Connect process status change
Given that  process batch-status has been deployed into the process engine http://192.168.7.147:8080
When a user triggers execution of the batch-status daily process processurl /engine-rest/history/process-instance
Then the batch-status should successfully complete 200