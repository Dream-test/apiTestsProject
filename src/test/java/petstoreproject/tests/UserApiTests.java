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
        logger.info("checkAddUserTest Add user response statusCode: {}", response.statusCode());
        Assertions.assertThat(response.jsonPath().getString("message")).isNotEqualTo("0");
        logger.info("checkAddUserTest Check status 200 when add user response: {}", response.prettyPrint());
    }

    @Story("Get user by username")
    @Test
    @Tag("smoke")
    @DisplayName("Check status 200 and id when get user")
    void checkGetUserTest() throws InterruptedException {
        Response response = userController.addStoreUser(storeUserList.getDefaultUser());
        Assertions.assertThat(response.statusCode()).isEqualTo(200);
        logger.info("checkGetUserTest Add user for get response statusCode: {}", response.statusCode());
        long expectedId = Long.parseLong(response.jsonPath().getString("message"));
        logger.info("checkGetUserTest Added user id: {}", expectedId);

        sleep(2000);

        Response getResponse = userController.getUserByName(storeUserList.getDefaultUser().getUsername());
        Assertions.assertThat(getResponse.statusCode()).isEqualTo(200);
        logger.info("checkGetUserTest Get user response statusCode: {}", getResponse.statusCode());
        long actualId = Long.parseLong(getResponse.jsonPath().getString("id"));
        logger.info("checkGetUserTest Actual user id: {}", actualId);
        String actualUserFirstName = getResponse.jsonPath().getString("firstName");
        logger.info("checkGetUserTest ActualUserFirstName: {}", actualUserFirstName);
        logger.info("checkGetUserTest ExpectedUserFirstName: {}", storeUserList.getDefaultUser().getFirstName());
        Assertions.assertThat(actualUserFirstName).isEqualTo(storeUserList.getDefaultUser().getFirstName());
        Assertions.assertThat(actualId).isEqualTo(expectedId);
        logger.info("checkGetUserTest Check status 200 and id when get user response body: {}", getResponse.prettyPrint());
    }

    @Story("Update user by username")
    @Test
    @Tag("smoke")
    @DisplayName("Check status 200 when update user")
    void checkUpdateUserTest() {
        Response response = userController.addStoreUser(storeUserList.getDefaultUser());
        Assertions.assertThat(response.statusCode()).isEqualTo(200);
        logger.info("checkUpdateUserTest Add user for update response statusCode: {}", response.statusCode());

        StoreUser updatingUser = storeUserList.getDefaultUser();
        String currentUsername = updatingUser.getUsername();
                updatingUser.setFirstName("Alex");
                updatingUser.setEmail("alex50@gmail.com");
        logger.info("checkUpdateUserTest update user: {}", updatingUser);

        Response updateResponse = userController.updateUserByName(currentUsername,updatingUser);
        Assertions.assertThat(updateResponse.statusCode()).isEqualTo(200);
        logger.info("checkUpdateUserTest Update response status code: {}", updateResponse.statusCode());
        logger.info("checkUpdateUserTest Update response body: {}", updateResponse.prettyPrint());

        //Сервер не вносит изменений в БД при выполнении update - контроль изменений невозможен
    }

    @Story("Delete user by username")
    @Test
    @Tag("smoke")
    @DisplayName("Check status 200 when delete user")
    void checkDeleteUserTest() throws InterruptedException {
        Response response = userController.addStoreUser(storeUserList.getDefaultUser());
        Assertions.assertThat(response.statusCode()).isEqualTo(200);
        logger.info("checkDeleteUserTest Add user for delete response statusCode: {}", response.statusCode());

        sleep(2000);

        String deletingUserUsername = storeUserList.getDefaultUser().getUsername();
        logger.info("checkDeleteUserTest Deleting StoreUser username: {}", deletingUserUsername);
        Response deleteResponse = userController.deleteUserByName(deletingUserUsername);
        Assertions.assertThat(deleteResponse.statusCode()).isEqualTo(200);
        logger.info("checkDeleteUserTest Delete response status code: {}", deleteResponse.statusCode());
        logger.info("checkDeleteUserTest Delete response body: {}", deleteResponse.prettyPrint());

        sleep(2000);

        Response getResponse = userController.getUserByName(deletingUserUsername);
        Assertions.assertThat(getResponse.statusCode()).isEqualTo(404);
        logger.info("checkDeleteUserTest Get response by username: {}", deletingUserUsername);
        logger.info("checkDeleteUserTest Get response after deleting status code: {}", getResponse.statusCode());
        Assertions.assertThat((getResponse.jsonPath().getString("message")).contains("User not found")).isTrue();
        logger.info("checkDeleteUserTest Get response after deleting body: {}", getResponse.prettyPrint());
    }

    @Story("Add users by list")
    @Test
    @Tag("extended")
    @DisplayName("Check status 200 and get one when add users by list")
    void checkAddUsersByListTest() throws InterruptedException {

        userController.deleteAllUsersByList(storeUserList);

        Response response = userController.addStoreUsersByString(storeUserList.allUsersAsString());
        Assertions.assertThat(response.statusCode()).isEqualTo(200);
        logger.info("checkAddUsersByListTes Add users by list response statusCode: {}", response.statusCode());
        String addResponseMessage = response.jsonPath().getString("message");
        Assertions.assertThat((addResponseMessage).contains("ok")).isTrue();
        logger.info("checkAddUsersByListTes Add users by list response message: {}", addResponseMessage);

        int index = randomGenerator.getIndex(storeUserList.getNumberOfUsers());
        String currentUsername = storeUserList
                .getByIndex(index)
                .getUsername();
        logger.info("checkAddUsersByListTes CurrentUsername: {}", currentUsername);
        String currentUserFirstName = storeUserList
                .getByIndex(index)
                .getFirstName();
        logger.info("checkAddUsersByListTes CurrentUserFirstName: {}", currentUserFirstName);

        sleep(2000);

        Response getResponse = userController.getUserByName(currentUsername);
        Assertions.assertThat(getResponse.statusCode()).isEqualTo(200);
        logger.info("checkAddUsersByListTes Get user response after added users list statusCode: {}", getResponse.statusCode());

        String actualUserFirstName = getResponse.jsonPath().getString("firstName");
        logger.info("checkAddUsersByListTes ActualUserFirstName: {}", actualUserFirstName);
        logger.info("checkAddUsersByListTes Get user after added users list response body: {}", getResponse.prettyPrint());
        Assertions.assertThat(actualUserFirstName).isEqualTo(currentUserFirstName);


        userController.deleteAllUsersByList(storeUserList);
    }
}
