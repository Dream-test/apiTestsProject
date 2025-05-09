package petstoreproject.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import petstoreproject.configurations.TestConfig;
import petstoreproject.models.StoreUser;
import petstoreproject.utils.StoreUserList;

import static io.restassured.RestAssured.given;

public class UserController {
    //private final ObjectMapper objectMapper = new ObjectMapper();
    TestConfig config = new TestConfig();
    RequestSpecification requestSpecification = given();

    public UserController() {
        RestAssured.defaultParser = Parser.JSON;
        this.requestSpecification.contentType(ContentType.JSON);
        this.requestSpecification.accept(ContentType.JSON);
        this.requestSpecification.baseUri(config.getBaseUrl());
        this.requestSpecification.filter(new AllureRestAssured());
    }

    @Step("Add new user")
    public Response addStoreUser (StoreUser user) {
        /* //Используется ObjectMapper для сериализации данных объекта user в строку json
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonBody = objectMapper.writeValueAsString(user);
            this.requestSpecification.body(jsonBody);
        } catch (JsonProcessingException e) {
            logger.error("Can not serialize user to string", e);
            this.requestSpecification.body("{}");
        } */
        //Используется встроенная сериализация RestAssured в строку json для объекта user
        this.requestSpecification.body(user);
        return given(this.requestSpecification).post("user").andReturn();
    }

    @Step("Get user by name")
    public Response getUserByName(String username) {
        return given(this.requestSpecification).get("user/" + username).andReturn();
    }

    @Step("Delete user by name")
    public Response deleteUserByName(String username) {
        return given(this.requestSpecification).delete("user/" + username).andReturn();
    }

    @Step("Update user by name")
    public Response updateUserByName(String username, StoreUser updatedUser) {
        this.requestSpecification.body(updatedUser);
        return given(this.requestSpecification).put("user/" + username).andReturn();
    }

    @Step("Add users by string")
    public Response addStoreUsersByString(String allUsersAsString) {
        this.requestSpecification.body(allUsersAsString);
        return given(this.requestSpecification).post("user/createWithList").andReturn();
    }

    @Step("Delete all users by list")
        public void deleteAllUsersByList (StoreUserList usersList) {
        for (int i = 1; i < usersList.getNumberOfUsers(); i++) {
            given(this.requestSpecification).delete("user/" + usersList.getByIndex(i).getUsername());
        }
    }
}
