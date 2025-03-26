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
import petstoreproject.controllers.UserController;
import petstoreproject.models.StoreUser;
import petstoreproject.utils.StoreUserList;

import static java.lang.Thread.sleep;

@Feature("UserControllerTests")
@Epic("User API Tests")
@Tag("api")
public class UserApiTests {
    Logger logger = LoggerFactory.getLogger(UserApiTests.class);
    UserController userController = new UserController();
    StoreUserList storeUserList = new StoreUserList();
    RandomGenerator randomGenerator = new RandomGenerator();

    @BeforeEach
    @AfterEach
    void cleanTestUsers() {
        userController.deleteAllUsersByList(storeUserList);
    }

    @Story("Adding user")
    @Test
    @Tag("smoke")
    @DisplayName("Check status 200 when add user")
    void checkAddUserTest() {
        //Arrange
        StoreUser currentUser = storeUserList.getDefaultUser();
        logger.info("checkAddUserTest currentUser: {}", currentUser);

        //Act
        Response response = userController.addStoreUser(currentUser);
        logger.info("checkAddUserTest Add user response statusCode: {}", response.statusCode());
        logger.info("checkAddUserTest Check status 200 when add user response: {}", response.asString());

        //Assert
        Assertions.assertThat(response.statusCode()).isEqualTo(200);
        Assertions.assertThat(response.jsonPath().getString("message")).isNotEqualTo("0");
    }

    @Story("Get user by username")
    @Test
    @Tag("smoke")
    @DisplayName("Check status 200 and id when get user")
    void checkGetUserTest() {
        //Arrange
        int index = randomGenerator.getIndex(storeUserList.getNumberOfUsers());
        StoreUser currentUser = storeUserList.getByIndex(index);

        Response response = userController.addStoreUser(currentUser);
        logger.info("checkGetUserTest Add user for get response statusCode: {}", response.statusCode());
        long expectedId = Long.parseLong(response.jsonPath().getString("message"));
        Assertions.assertThat(response.statusCode()).isEqualTo(200);

        //Act
        Response getResponse = userController.getUserByName(currentUser.getUsername());
        logger.info("checkGetUserTest Get user response statusCode: {}", getResponse.statusCode());
        logger.info("checkGetUserTest Get user response body: {}", getResponse.asString());

        //Assert
        Assertions.assertThat(getResponse.statusCode()).isEqualTo(200);
        StoreUser actualUser = getResponse.as(StoreUser.class);
        long actualId = Long.parseLong(getResponse.jsonPath().getString("id"));

        logger.info("checkGetUserTest Added user id: {}", expectedId);
        logger.info("checkGetUserTest Actual user id: {}", actualId);
        Assertions.assertThat(actualId).isEqualTo(expectedId);

        logger.info("checkGetUserTest currentUser: {}", currentUser);
        logger.info("checkGetUserTest actualUser: {}", actualUser);
        Assertions.assertThat(actualUser).usingRecursiveComparison().ignoringFields("id").isEqualTo(currentUser);
    }

    @Story("Update user by username")
    @Test
    @Tag("smoke")
    @DisplayName("Check status 200 when update user")
    void checkUpdateUserTest() {
        //Arrange
        Response response = userController.addStoreUser(storeUserList.getDefaultUser());
        logger.info("checkUpdateUserTest Add user for update response statusCode: {}", response.statusCode());
        Assertions.assertThat(response.statusCode()).isEqualTo(200);

        StoreUser updatingUser = storeUserList.getDefaultUser();
        String currentUsername = updatingUser.getUsername();
                updatingUser.setFirstName("Alex");
                updatingUser.setEmail("alex50@gmail.com");
        logger.info("checkUpdateUserTest update user: {}", updatingUser);

        //Act
        Response updateResponse = userController.updateUserByName(currentUsername,updatingUser);
        logger.info("checkUpdateUserTest Update response status code: {}", updateResponse.statusCode());
        logger.info("checkUpdateUserTest Update response body: {}", updateResponse.prettyPrint());

        //Assert
        Assertions.assertThat(updateResponse.statusCode()).isEqualTo(200);

        //Сервер не вносит изменений в БД при выполнении update - контроль изменений невозможен
    }

    @Story("Delete user by username")
    @Test
    @Tag("smoke")
    @DisplayName("Check status 200 when delete user")
    void checkDeleteUserTest() {
        //Arrange
        int index = randomGenerator.getIndex(storeUserList.getNumberOfUsers());
        StoreUser currentUser = storeUserList.getByIndex(index);

        Response response = userController.addStoreUser(currentUser);
        logger.info("checkDeleteUserTest Add user for delete response statusCode: {}", response.statusCode());
        Assertions.assertThat(response.statusCode()).isEqualTo(200);

        String deletingUserUsername = currentUser.getUsername();
        logger.info("checkDeleteUserTest Deleting StoreUser username: {}", deletingUserUsername);

        //Act
        Response deleteResponse = userController.deleteUserByName(deletingUserUsername);
        logger.info("checkDeleteUserTest Delete response status code: {}", deleteResponse.statusCode());
        logger.info("checkDeleteUserTest Delete response body: {}", deleteResponse.asString());
        String deletedUsername = deleteResponse.jsonPath().getString("message");
        logger.info("checkDeleteUserTest deletedUsername: {}", deletedUsername);

       //Assert
        Assertions.assertThat(deleteResponse.statusCode()).isEqualTo(200);
        Assertions.assertThat(deletedUsername).isEqualTo(deletingUserUsername);

        Response getResponse = userController.getUserByName(deletingUserUsername);
        logger.info("checkDeleteUserTest Get response after deleting by username: {}", deletingUserUsername);
        logger.info("checkDeleteUserTest Get response after deleting status code: {}", getResponse.statusCode());
        logger.info("checkDeleteUserTest Get response after deleting body: {}", getResponse.asString());
        Assertions.assertThat(getResponse.statusCode()).isEqualTo(404);
        Assertions.assertThat((getResponse.jsonPath().getString("message")).contains("User not found")).isTrue();
    }

    @Story("Add users by list")
    @Test
    @Tag("extended")
    @DisplayName("Check status 200 and get one when add users by list")
    void checkAddUsersByListTest() throws InterruptedException {
        //Act
        Response response = userController.addStoreUsersByString(storeUserList.allUsersAsString());
        logger.info("checkAddUsersByListTes Add users by list response statusCode: {}", response.statusCode());
        String addResponseMessage = response.jsonPath().getString("message");
        logger.info("checkAddUsersByListTes Add users by list response message: {}", addResponseMessage);

        //Assert
        Assertions.assertThat(response.statusCode()).isEqualTo(200);
        Assertions.assertThat((addResponseMessage).contains("ok")).isTrue();

        int index = randomGenerator.getIndex(storeUserList.getNumberOfUsers());
        StoreUser currentUser = storeUserList.getByIndex(index);
        String currentUsername = currentUser.getUsername();

        Response getResponse = userController.getUserByName(currentUsername);
        logger.info("checkAddUsersByListTes Get user response after added users list statusCode: {}", getResponse.statusCode());
        logger.info("checkAddUsersByListTes Get user after added users list response body: {}", getResponse.asString());
        Assertions.assertThat(getResponse.statusCode()).isEqualTo(200);
        StoreUser actualUser = getResponse.as(StoreUser.class);

        logger.info("checkAddUsersByListTes CurrentUsername: {}", currentUsername);
        logger.info("checkAddUsersByListTes ActualUser: {}", actualUser);
        Assertions.assertThat(actualUser).usingRecursiveComparison().ignoringFields("id").isEqualTo(currentUser);
    }
}
