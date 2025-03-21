package apiproject.tests;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasItems;

public class SimpleGraphQLApiTests {
    private static final Logger logger = LoggerFactory.getLogger(SimpleGraphQLApiTests.class);


    @Test
    void getAllUserTest() {
        String query = """
                {
                  "query": "query { getAllUsers { firstName lastName email id gender ipaddress __typename } }",
                  "variables": null
                }
                """;

        Response response = given()
                .header("Content-type", "application/json")
                .body(query)
                .when()
                .post("https://graphql-api-ppql.onrender.com/graphql")
                .then()
                .extract().response();

        logger.info("Response status code: {}", response.statusCode());
        logger.info("Response body: {}", response.getBody().asPrettyString());
        JsonPath path = new JsonPath(response.getBody().asPrettyString());
        List<String> firstNames = path.get("data.getAllUsers.firstName");
        logger.info("FirstNames list: {}", firstNames);

        Assertions.assertThat(response.statusCode()).isEqualTo(200);
        Assertions.assertThat(firstNames).contains("Wilbur", "Inigo");
    }

    @Test
    void getUserByIdTest() {
        String query = """
                {
                   "query": "query($id: Int) { getAllUsers(id: $id) { id ipaddress __typename } }",
                   "variables": { "id": 22 }
                 }
                """;
        //Особенность сервера - не выполняет фильтрации по id, всегда возвращается весь массив [user]
        //но возвращаются в массиве только перечисленные в запросе данные
        Response response = given()
                .header("Content-type", "application/json")
                .body(query)
                .when()
                .post("https://graphql-api-ppql.onrender.com/graphql")
                .then()
                .extract().response();

        logger.info("Response user by id status code: {}", response.statusCode());
        logger.info("Response user by id body: {}", response.getBody().asPrettyString());
        JsonPath path = new JsonPath(response.getBody().asPrettyString());
        List<Integer> userId = path.get("data.getAllUsers.id");
        logger.info("UserId list: {}", userId);

        Assertions.assertThat(response.statusCode()).isEqualTo(200);
        Assertions.assertThat(userId).contains(22);
    }

    @Test
    void getHamcrestAllUsersTests() {
        String graphQlUri = "https://graphql-api-ppql.onrender.com/graphql";
        String query = """
                {
                  "query": "query { getAllUsers { firstName lastName email id gender ipaddress __typename } }",
                  "variables": null
                }
                """;

        given()
                .header("Content-type", "application/json")
                .body(query)
                .when()
                .post(graphQlUri)
                .then()
                .assertThat()
                .body("data.getAllUsers.firstName", hasItems("Wilbur", "Inigo"))
                .statusCode(200);
    }

}
