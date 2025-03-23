package petstoreproject.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import petstoreproject.configurations.TestConfig;
import petstoreproject.models.StoreUser;
import petstoreproject.utils.StoreUserList;

import static io.restassured.RestAssured.given;

public class UserController {
    private final Logger logger = LoggerFactory.getLogger(StoreUserList.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    TestConfig config = new TestConfig();
    RequestSpecification requestSpecification = given();

    public UserController() {
        RestAssured.defaultParser = Parser.JSON;
        this.requestSpecification.contentType(ContentType.JSON);
        this.requestSpecification.accept(ContentType.JSON);
        this.requestSpecification.baseUri(config.getBaseUrl());
        this.requestSpecification.filter(new AllureRestAssured());
    }

    @Step("Add user")
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
}
