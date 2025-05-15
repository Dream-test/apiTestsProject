## Description:
The project contains auto-tests for creating, reading, editing, deleting (CRUD – model) Pet, Store, User entities 
of https://petstore.swagger.io/#/ service using Rest Assured, JUnit and AssertJ. After the tests are executed,
Allure generates a report on test results.

## Comments on the project:
1. All project classes are located in the directory: ./src/test/java/petstoreproject/ which contains the following 
packages: <br>
   - /configurations - contains the basic configuration class; <br>
   - /controllers - contains classes implementing additional configurations and query logic; <br>
   - /models - contains classes of objects used in store with overridden toString for logging convenience; <br>
   - /tests - contains test classes divided by endpoints (user/pet/store); <br>
   - /utils - contains utility classes converting test data from json files into lists of objects; 
   src/test/resources directory - contains json files with test data (arrays of test objects). <br>

2. Generation of the initial test object for operations is performed by random selection from the array of test
   objects. The object is extracted from the list by a random index in the array range and placed in the log for
   further analysis when the test crashes. The random index is defined in the Randomgenerator class.

3. Endpoint pet update (put) test: checkUpdateValuesExistingPetTest - crashes due to the fact that the server
   does not update the object parameters in the database and the get request returns an unchanged object, which
   I consider as a bug on the server side. For endpoint user similar check was not included in the test - only
   status code.

4. I have not been able to achieve stable test execution - periodically I get crashes due to receiving a response
   that does not match the expected result. I tried to use sleep to provide time for the server to make changes to
   the database before executing the next assert query, but it did not lead to significant results.

5. Allure report generation: allure serve target/allure-results.
