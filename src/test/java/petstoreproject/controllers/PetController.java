package petstoreproject.controllers;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import petstoreproject.configurations.TestConfig;
import petstoreproject.models.StorePet;

import static io.restassured.RestAssured.given;

public class PetController {
    TestConfig config = new TestConfig();
    RequestSpecification requestSpecification = given();

    public PetController() {
        RestAssured.defaultParser = Parser.JSON;
        this.requestSpecification.header("api_key", config.getApiKey());
        this.requestSpecification.contentType(ContentType.JSON);
        this.requestSpecification.accept(ContentType.JSON);
        this.requestSpecification.baseUri(config.getBaseUrl());
        this.requestSpecification.filter(new AllureRestAssured());
    }

    @Step("Add new pet")
    public Response addStorePet (StorePet pet) {
        this.requestSpecification.body(pet);
        return given(this.requestSpecification).post("pet").andReturn();
    }

    @Step("Get pet by id")
    public Response getStorePetById(long petId) {
        return given(this.requestSpecification).get("pet/" + petId).andReturn();
    }

    @Step
    public Response updateStorePet (StorePet pet) {
        this.requestSpecification.body(pet);
        return  given(this.requestSpecification).put("pet").andReturn();
    }

    @Step("Delete pet by id")
    public Response deletePetById(long petId) {
        return given(this.requestSpecification).delete("pet/" + petId).andReturn();
    }
}
