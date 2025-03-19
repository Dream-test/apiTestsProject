package apiproject;

import apiproject.objects.PetStoreUser;
import apiproject.utils.RandomGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class SimpleRestApiTests {
    private static final Logger logger = LoggerFactory.getLogger(SimpleRestApiTests.class);
    private static final String BASE_URL = "https://petstore.swagger.io/v2";

    @Test
    void simpleGetInventoryTest() {
        String endpoint = BASE_URL + "/store/inventory";
        given()
                .when()
                .get(endpoint)
                .then()
                .log()
                .all();
    }

    @Test
    void exampleRestAssuredTest() {
        String jsonBody = """
                {
                  "id": 0,
                  "username": "Legion212",
                  "firstName": "Valentin",
                  "lastName": "Chromov",
                  "email": "myemail@gmail.com",
                  "password": "stringTest321",
                  "phone": "+0928369456987",
                  "userStatus": 0
                }""";
        String endpoint = BASE_URL + "/user";

        ValidatableResponse response =
                given()
                        .header("accept", "application/json")
                        .header("Content-Type", "application/json")
                        .body(jsonBody)
                        .when()
                        .post(endpoint)
                        .then();
        Response responseAsResponse = response.extract().response();
        int status = responseAsResponse.getStatusCode();
        String responseTxt = responseAsResponse.body().prettyPrint();
        Assertions.assertThat(status).isEqualTo(200);
        logger.info("Response in string: {}", responseTxt);
    }

    @Test
    void getUserWithPathParamTest() throws IOException {

            ObjectMapper objectMapper = new ObjectMapper();
            // List<Map<String, Object>> usersList = objectMapper.readValue(new File("src/test/resources/users.json"), new TypeReference<>() {
            // });//Считываю список из json объектов преобразованных в строки


        //считываю файл и сохраняю список объектов
        List<PetStoreUser> usersList = objectMapper.readValue(new File("src/test/resources/users.json"), new TypeReference<List<PetStoreUser>>() {});
        int numberOfUsers = usersList.size();// Определяю количество строк - объектов

        RandomGenerator generator = new RandomGenerator();
        //int index = 0;
        int index = generator.getIndex(numberOfUsers);//случайный индекс в диапазоне количества объектов
        logger.info("index: {}", index);
        PetStoreUser newUser = usersList.get(index);//выбираю один объект по индексу
        logger.info("newUser: {}", newUser);

        String username = newUser.getUsername();
        logger.error("username: {}", username);
        String userEndpoint = BASE_URL + "/user";
        logger.info("postNewUserEndpoint: {}", userEndpoint);

        String jsonBody = objectMapper.writeValueAsString(newUser);
        logger.info("jsonBody: {}", jsonBody);

        given()
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .body(jsonBody)
                .when()
                .post(userEndpoint)
                .then()
                .statusCode(200)
                .body("message", notNullValue());

        String newUserEndpoint = BASE_URL + "/user/" + username;
        logger.info("getUserEndpoint: {}", newUserEndpoint);

        Response getResponse = given()
                .get(newUserEndpoint)
                .then()
                .log()
                .all()
                .assertThat()
                .statusCode(200)
                .extract().response();

        PetStoreUser actualUser = getResponse.as(PetStoreUser.class);
        logger.info("actualUser: {}", actualUser);
        Assertions.assertThat(actualUser)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(newUser);
    }

    @Test
    void simpleDeleteTest() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        List<PetStoreUser> usersList = objectMapper.readValue(new File("src/test/resources/users.json"), new TypeReference<List<PetStoreUser>>() {});
        int numberOfUsers = usersList.size();// Определяю количество строк - объектов

        RandomGenerator generator = new RandomGenerator();
        //int index = 0;
        int index = generator.getIndex(numberOfUsers);//случайный индекс в диапазоне количества объектов
        logger.info("index: {}", index);
        PetStoreUser newUser = usersList.get(index);//выбираю один объект по индексу
        logger.info("newUser: {}", newUser);

        String username = newUser.getUsername();
        logger.error("username: {}", username);
        String userEndpoint = BASE_URL + "/user";
        logger.info("postNewUserEndpoint: {}", userEndpoint);

        String jsonBody = objectMapper.writeValueAsString(newUser);
        logger.info("jsonBody: {}", jsonBody);

        var response = given()
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .body(jsonBody)
                .when()
                .post(userEndpoint)
                .then()
                .statusCode(200)
                .body("message", notNullValue());
        response.log().body();

        String newUserEndpoint = BASE_URL + "/user/" + username;

        var deleteResponse = given()
                .when()
                .delete(newUserEndpoint)
                .then();
        deleteResponse.log().body();
        deleteResponse.statusCode(200);


        ValidatableResponse getResponse = given()
                .when()
                .get(newUserEndpoint)
                .then()
                .log()
                .all()
                .assertThat()
                .statusCode(404)
                .body("message", equalTo("User not found"));
    }

    @Test
    void postWithModelHamcrestAssertTest() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        List<PetStoreUser> usersList = objectMapper.readValue(new File("src/test/resources/users.json"), new TypeReference<List<PetStoreUser>>() {});
        int numberOfUsers = usersList.size();// Определяю количество строк - объектов

        RandomGenerator generator = new RandomGenerator();
        //int index = 0;
        int index = generator.getIndex(numberOfUsers);//случайный индекс в диапазоне количества объектов
        logger.info("index: {}", index);
        PetStoreUser newUser = usersList.get(index);//выбираю один объект по индексу
        logger.info("newUser: {}", newUser);

        String username = newUser.getUsername();
        logger.error("username: {}", username);
        String userEndpoint = BASE_URL + "/user";
        logger.info("postNewUserEndpoint: {}", userEndpoint);

        String jsonBody = objectMapper.writeValueAsString(newUser);
        logger.info("jsonBody: {}", jsonBody);

        var response = given()
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .body(jsonBody)
                .when()
                .post(userEndpoint)
                .then()
                .statusCode(200)
                .body("message", notNullValue());
        response.log().body();

        String newUserEndpoint = BASE_URL + "/user/" + username;
        logger.info("newUserEndpoint: {}", newUserEndpoint);

        ValidatableResponse getResponse = given()
                .when()
                .get(newUserEndpoint)
                .then()
                .log()
                .all()
                .assertThat()
                .statusCode(200)
                .body("username", equalTo(newUser.getUsername()))
                .body("firstName", startsWith(newUser.getFirstName()))
                .body("lastName", equalToIgnoringCase(newUser.getLastName()))
                .body("email", matchesPattern("^[a-zA-Z0-9._%+-]+@gmail\\.com$"))
                .body("password", equalTo(newUser.getPassword()))
                .body("phone", equalTo(newUser.getPhone()));
    }

    @Test
    void postWithModelSoftAssertTest() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        List<PetStoreUser> usersList = objectMapper.readValue(new File("src/test/resources/users.json"), new TypeReference<List<PetStoreUser>>() {});
        int numberOfUsers = usersList.size();// Определяю количество строк - объектов

        RandomGenerator generator = new RandomGenerator();
        //int index = 0;
        int index = generator.getIndex(numberOfUsers);//случайный индекс в диапазоне количества объектов
        logger.info("index: {}", index);
        PetStoreUser newUser = usersList.get(index);//выбираю один объект по индексу
        logger.info("newUser: {}", newUser);

        String username = newUser.getUsername();
        logger.error("username: {}", username);
        String userEndpoint = BASE_URL + "/user";
        logger.info("postNewUserEndpoint: {}", userEndpoint);

        String jsonBody = objectMapper.writeValueAsString(newUser);
        logger.info("jsonBody: {}", jsonBody);

        given()
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .body(jsonBody)
                .when()
                .post(userEndpoint)
                .then()
                .statusCode(200)
                .body("message", notNullValue());

        String newUserEndpoint = BASE_URL + "/user/" + username;
        logger.info("getUserEndpoint: {}", newUserEndpoint);

        Response getResponse = given()
                .get(newUserEndpoint)
                .then()
                .log()
                .all()
                .assertThat()
                .statusCode(200)
                .extract().response();

        PetStoreUser actualUser = getResponse.as(PetStoreUser.class);
        logger.info("actualUser: {}", actualUser);

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(actualUser.getUsername()).isEqualTo(newUser.getUsername());
        softly.assertThat(actualUser.getFirstName()).isEqualTo(newUser.getFirstName());
        softly.assertThat(actualUser.getLastName()).as("get LastName").isEqualTo(newUser.getLastName());
        softly.assertThat(actualUser.getEmail()).as("get email").isEqualTo(newUser.getEmail());
        softly.assertThat(actualUser.getPassword()).as("get password").isEqualTo(newUser.getPassword());
        softly.assertThat(actualUser.getPhone()).as("get phone").isEqualTo(newUser.getPhone());
        softly.assertThat(actualUser.getUserStatus()).isEqualTo(newUser.getUserStatus());
        softly.assertAll();
    }

    @Test
    void getComplexResponseWithQueryParamTest() {
        String petStatusEndpoint = BASE_URL + "/pet/findByStatus";
        logger.info("petStatusEndpoint: {}", petStatusEndpoint);
        Response response = given()
                .header("accept", "application/json")
                .queryParam("status", "available")
                .when()
                .get(petStatusEndpoint)
                .then()
                .assertThat()
                .statusCode(200)
                .header("content-type", equalTo("application/json"))
                .body("id", everyItem(notNullValue()))
                .body("status", everyItem(equalTo("available")))
                .body("size()", greaterThan(2))
                .extract().response();
        response.getBody().prettyPrint();
    }

}
