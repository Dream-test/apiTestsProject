package petstoreproject.controllers;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import petstoreproject.configurations.TestConfig;
import petstoreproject.models.StoreOrder;

import static io.restassured.RestAssured.given;

public class StoreController {
    TestConfig config = new TestConfig();
    RequestSpecification requestSpecification = given();

    public StoreController() {
        RestAssured.defaultParser = Parser.JSON;
        this.requestSpecification.contentType(ContentType.JSON);
        this.requestSpecification.accept(ContentType.JSON);
        this.requestSpecification.baseUri(config.getBaseUrl());
        this.requestSpecification.filter(new AllureRestAssured());
    }

    @Step("Add new order")
    public Response addStoreOrder(StoreOrder order) {
        this.requestSpecification.body(order);
        return given(this.requestSpecification).post("store/order").andReturn();
    }

    @Step("Get order by id")
    public Response getStoreOrderById(long orderId) {
        return given(this.requestSpecification).get("store/order/" + orderId).andReturn();
    }

    @Step("Get all store inventories")
    public Response getStoreInventories() {
        this.requestSpecification.header("api_key", config.getApiKey());
        return given(this.requestSpecification).get("store/inventory").andReturn();
    }

    @Step("Delete order by id")
    public Response deleteOrderById(long orderId) {
        return given(this.requestSpecification).delete("store/order/" + orderId).andReturn();
    }
}
