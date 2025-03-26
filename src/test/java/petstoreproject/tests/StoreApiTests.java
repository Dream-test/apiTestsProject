package petstoreproject.tests;

import apiproject.utils.RandomGenerator;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import petstoreproject.controllers.StoreController;
import petstoreproject.models.StoreOrder;
import petstoreproject.utils.StoreOrdersList;

@Feature("StoreControllerTests")
@Epic("Store API Tests")
@Tag("api")
public class StoreApiTests {
    Logger logger = LoggerFactory.getLogger(StoreApiTests.class);
    StoreController storeController = new StoreController();
    StoreOrdersList storeOrdersList = new StoreOrdersList();
    RandomGenerator randomGenerator = new RandomGenerator();

    @BeforeEach
    @AfterEach
    void cleanTestOrders() {
        for (long orderId : storeOrdersList.ordersId()) {
            storeController.deleteOrderById(orderId);
        }
    }

    @Story("Add new order")
    @Test
    @Tag("smoke")
    @DisplayName("Check status 200 when add order")
    void checkAddOrderTest() {
        //Arrange
        int index = randomGenerator.getIndex(storeOrdersList.getNumberOfOrders());
        StoreOrder currentOrder = storeOrdersList.getOrderByIndex(index);
        logger.info("checkAddOrderTest currentOrder: {}", currentOrder);

        //Act
        Response response = storeController.addStoreOrder(currentOrder);
        logger.info("checkAddOrderTest Add order response statusCode: {}", response.statusCode());
        logger.info("checkAddOrderTest Add order response body: {}", response.asString());

        //Assert
        Assertions.assertThat(response.statusCode()).isEqualTo(200);
        long expectedPetId = currentOrder.getPetId();
        logger.info("checkAddOrderTest Expected petId: {}", expectedPetId);
        long actualPetId = Long.parseLong(response.jsonPath().getString("petId"));
        logger.info("checkAddOrderTest Actual petId: {}", actualPetId);
        Assertions.assertThat(actualPetId).isEqualTo(expectedPetId);
    }

    @Story("Get order by id")
    @Test
    @Tag("smoke")
    @DisplayName("Check status 200 and id when get order")
    void checkGetOrderTest() {
        //Arrange
        int index = randomGenerator.getIndex(storeOrdersList.getNumberOfOrders());
        StoreOrder currentOrder = storeOrdersList.getOrderByIndex(index);
        logger.info("checkGetOrderTest currentOrder: {}", currentOrder);
        long orderId = currentOrder.getId();

        Response response = storeController.addStoreOrder(currentOrder);
        logger.info("checkGetOrderTest Add order response statusCode: {}", response.statusCode());
        logger.info("checkGetOrderTest Add order response body: {}", response.asString());
        Assertions.assertThat(response.statusCode()).isEqualTo(200);

        //Act
        Response getResponse = storeController.getStoreOrderById(orderId);
        logger.info("checkGetOrderTest Get order response statusCode: {}", getResponse.statusCode());
        logger.info("checkGetOrderTest Get order response body: {}", getResponse.asString());

        //Assert
        Assertions.assertThat(getResponse.statusCode()).isEqualTo(200);
        long expectedPetId = currentOrder.getPetId();
        logger.info("checkGetOrderTest Expected petId: {}", expectedPetId);
        long actualPetId = Long.parseLong(getResponse.jsonPath().getString("petId"));
        logger.info("checkGetOrderTest Actual petId: {}", actualPetId);
        Assertions.assertThat(actualPetId).isEqualTo(expectedPetId);
    }

    @Story("Get order by id")
    @Test
    @Tag("extended")
    @DisplayName("Check status 200 and object when get order")
    void checkGetAndDataOrderTest() {
        //Arrange
        int index = randomGenerator.getIndex(storeOrdersList.getNumberOfOrders());
        StoreOrder currentOrder = storeOrdersList.getOrderByIndex(index);
        logger.info("checkGetAndDataOrderTest currentOrder: {}", currentOrder);
        long orderId = currentOrder.getId();

        Response response = storeController.addStoreOrder(currentOrder);
        logger.info("checkGetAndDataOrderTest Add order response statusCode: {}", response.statusCode());
        logger.info("checkGetAndDataOrderTest Add order response body: {}", response.asString());
        Assertions.assertThat(response.statusCode()).isEqualTo(200);

        //Act
        Response getResponse = storeController.getStoreOrderById(orderId);
        logger.info("checkGetAndDataOrderTest response statusCode: {}", getResponse.statusCode());
        logger.info("checkGetAndDataOrderTest Get order response body: {}", getResponse.asString());
        StoreOrder actualOrder = getResponse.as(StoreOrder.class);

        //Assert
        Assertions.assertThat(getResponse.statusCode()).isEqualTo(200);
        Assertions.assertThat(actualOrder).usingRecursiveComparison().isEqualTo(currentOrder);
    }

    @Story("Delete order by id")
    @Test
    @Tag("smoke")
    @DisplayName("Check status 200 when delete and 404 when get deleted order")
    void checkDeleteOrderTest() {
        //Arrange
        int index = randomGenerator.getIndex(storeOrdersList.getNumberOfOrders());
        StoreOrder currentOrder = storeOrdersList.getOrderByIndex(index);
        logger.info("checkDeleteOrderTest currentOrder: {}", currentOrder);
        long orderId = currentOrder.getId();

        Response response = storeController.addStoreOrder(currentOrder);
        logger.info("checkDeleteOrderTest Add order response statusCode: {}", response.statusCode());
        logger.info("checkDeleteOrderTest Add order response body: {}", response.asString());
        Assertions.assertThat(response.statusCode()).isEqualTo(200);

        //Act
        Response deleteResponse = storeController.deleteOrderById(orderId);
        logger.info("checkDeleteOrderTest Delete order response statusCode: {}", deleteResponse.statusCode());
        logger.info("checkDeleteOrderTest Delete order response body: {}", deleteResponse.asString());

        //Assert
        Assertions.assertThat(deleteResponse.statusCode()).isEqualTo(200);
        long deletedOrderId = Long.parseLong(deleteResponse.jsonPath().getString("message"));
        logger.info("checkDeleteOrderTest deletedOrderId: {}", deletedOrderId);
        Assertions.assertThat(deletedOrderId).isEqualTo(orderId);

        Response getResponse = storeController.getStoreOrderById(orderId);
        logger.info("checkDeleteOrderTest Get order response statusCode: {}", getResponse.statusCode());
        logger.info("checkDeleteOrderTest Get order response body: {}", getResponse.asString());
        Assertions.assertThat(getResponse.statusCode()).isEqualTo(404);
    }

    @Story("Get store inventories")
    @Test
    @Tag("smoke")
    @DisplayName("Check that get store inventories response body isn't empty")
    void checkStoryInventoryTest() {
        //Act
        Response response = storeController.getStoreInventories();
        logger.info("checkStoryInventoryTest response statusCode: {}", response.statusCode());
        logger.info("checkStoryInventoryTest Get order response body: {}", response.asString());

        //Assert
        Assertions.assertThat(response.statusCode()).isEqualTo(200);
        Assertions.assertThat(response.getBody().asString()).isNotNull().isNotEmpty();
    }
}
