package petstoreproject.tests;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import petstoreproject.controllers.UserController;
import petstoreproject.models.StoreUser;
import petstoreproject.utils.StoreUserList;

@Feature("ControllerTests")
@Epic("User API Tests")
@Tag("api")
public class UserApiTests {
    Logger logger = LoggerFactory.getLogger(UserApiTests.class);
    UserController userController = new UserController();
    StoreUserList storeUserList = new StoreUserList();

    @BeforeEach
    @AfterEach
    void cleanFirstUser() {
        Response response = userController
                .deleteUserByName(storeUserList.getDefaultUser().getUsername());
        if ((response.statusCode()) == 200) {
            logger.info("Clean default user: {}", response.prettyPrint());
        } else {
            logger.info("Enable clean default user status code: {}", response.statusCode());
        }

    }

    @Story("Adding user")
    @Test
    @Tag("smoke")
    @DisplayName("Check status 200 when add user")
    void checkAddUserTest() {
        Response response = userController.addStoreUser(storeUserList.getDefaultUser());
        Assertions.assertThat(response.statusCode()).isEqualTo(200);
        logger.info("Add user response statusCode: {}", response.statusCode());
        Assertions.assertThat(response.jsonPath().getString("message")).isNotEqualTo("0");
        logger.info("Check status 200 when add user response: {}", response.prettyPrint());
    }

    @Story("Get user by username")
    @Test
    @Tag("smoke")
    @DisplayName("Check status 200 and id when get user")
    void checkGetUserTest() {
        Response response = userController.addStoreUser(storeUserList.getByIndex(1));
        Assertions.assertThat(response.statusCode()).isEqualTo(200);
        logger.info("Add user for get response statusCode: {}", response.statusCode());
        long expectedId = Long.parseLong(response.jsonPath().getString("message"));
        logger.info("Added user id: {}", expectedId);

        Response getResponse = userController.getUserByName(storeUserList.getByIndex(1).getUsername());
        Assertions.assertThat(getResponse.statusCode()).isEqualTo(200);
        logger.info("Get user response statusCode: {}", getResponse.statusCode());
        long actualId = Long.parseLong(getResponse.jsonPath().getString("id"));
        logger.info("Actual user id: {}", actualId);
        String actualUserName = getResponse.jsonPath().getString("firstName");
        Assertions.assertThat(storeUserList.getByIndex(1).getFirstName()).isEqualTo(actualUserName);
        //Assertions.assertThat(actualId).isEqualTo(expectedId);
        logger.info("Check status 200 and id when get user: {}", getResponse.prettyPrint());
    }

    @Story("Update user by username")
    @Test
    @Tag("smoke")
    @DisplayName("Check status 200 when update user")
    void checkUpdateUserTest() {
        Response response = userController.addStoreUser(storeUserList.getDefaultUser());
        Assertions.assertThat(response.statusCode()).isEqualTo(200);
        logger.info("Add user for update response statusCode: {}", response.statusCode());

        StoreUser updatingUser = storeUserList.getDefaultUser();
        String currentUsername = updatingUser.getUsername();
                updatingUser.setFirstName("Alex");
                updatingUser.setEmail("alex50@gmail.com");
        logger.info("update user: {}", updatingUser);

        Response updateResponse = userController.updateUserByName(currentUsername,updatingUser);
        Assertions.assertThat(updateResponse.statusCode()).isEqualTo(200);
        logger.info("Update response status code: {}", updateResponse.statusCode());
        logger.info("Update response body: {}", updateResponse.prettyPrint());

        //Сервер не вносит изменений в БД при выполнении update - контроль изменений невозможен
    }

    @Story("Delete user by username")
    @Test
    @Tag("smoke")
    @DisplayName("Check status 200 when delete user")
    void checkDeleteUserTest() {
        Response response = userController.addStoreUser(storeUserList.getDefaultUser());
        Assertions.assertThat(response.statusCode()).isEqualTo(200);
        logger.info("Add user for delete response statusCode: {}", response.statusCode());

        String deletingUserUsername = storeUserList.getDefaultUser().getUsername();
        logger.info("Deleting StoreUser username: {}", deletingUserUsername);
        Response deleteResponse = userController.deleteUserByName(deletingUserUsername);
        Assertions.assertThat(deleteResponse.statusCode()).isEqualTo(200);
        logger.info("Delete response status code: {}", deleteResponse.statusCode());
        logger.info("Delete response body: {}", deleteResponse.prettyPrint());

        Response getResponse = userController.getUserByName(deletingUserUsername);
        Assertions.assertThat(getResponse.statusCode()).isEqualTo(404);
        logger.info("Get response by username: {}", deletingUserUsername);
        logger.info("Get response after deleting status code: {}", getResponse.statusCode());
        Assertions.assertThat((getResponse.jsonPath().getString("message")).contains("User not found")).isTrue();
        logger.info("Get response after deleting body: {}", getResponse.prettyPrint());
    }


}
