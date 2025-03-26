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
import petstoreproject.controllers.PetController;
import petstoreproject.models.StorePet;
import petstoreproject.utils.StorePetsList;

import static java.lang.Thread.sleep;

@Feature("PetControllerTests")
@Epic("Pet API Tests")
@Tag("api")
public class PetApiTests {
    Logger logger = LoggerFactory.getLogger(PetApiTests.class);
    PetController petController = new PetController();
    StorePetsList storePetsList = new StorePetsList();
    RandomGenerator randomGenerator = new RandomGenerator();

    @BeforeEach
    @AfterEach
    void cleanTestPets() {
        for (long petId : storePetsList.petsId()) {
            petController.deletePetById(petId);
        }
    }

    @Story("Add new pet")
    @Test
    @Tag("smoke")
    @DisplayName("Check status 200 when add pet")
    void checkAddPetTest() {
        //Arrange
        StorePet currentPet = storePetsList.getDefaultPet();
        long expectedId = currentPet.getId();

        //Act
        Response response = petController.addStorePet(currentPet);
        logger.info("checkAddPetTest Add pet response statusCode: {}", response.statusCode());
        logger.info("checkAddPetTest Add pet response body: {}", response.asString());

        //Assert
        Assertions.assertThat(response.statusCode()).isEqualTo(200);
        logger.info("checkAddPetTest Expected id: {}", expectedId);
        long actualId = Long.parseLong(response.jsonPath().getString("id"));
        logger.info("checkAddPetTest Actual id: {}", actualId);
        Assertions.assertThat(actualId).isEqualTo(expectedId);
    }

    @Story("Get pet by id")
    @Test
    @Tag("smoke")
    @DisplayName("Check status 200 and id when get pet")
    void checkGetPetTest() {
        //Arrange
        int index = randomGenerator.getIndex(storePetsList.getNumberOfPets());
        long expectedId = storePetsList.getPetByIndex(index).getId();

        Response response = petController.addStorePet(storePetsList.getPetByIndex(index));
        logger.info("checkGetPetTest Add pet response statusCode: {}", response.statusCode());
        logger.info("checkGetPetTest Add response body: {}", response.asString());
        Assertions.assertThat(response.statusCode()).isEqualTo(200);

        //Act
        Response getResponse = petController.getStorePetById(expectedId);
        logger.info("checkGetPetTest Get pet response statusCode: {}", getResponse.statusCode());
        logger.info("checkGetPetTest Get pet response body: {}", getResponse.asString());

        //Assert
        Assertions.assertThat(getResponse.statusCode()).isEqualTo(200);
        logger.info("checkGetPetTest Expected id: {}", expectedId);
        long actualId = Long.parseLong(getResponse.jsonPath().getString("id"));
        logger.info("checkGetPetTest Actual id: {}", actualId);
        Assertions.assertThat(actualId).isEqualTo(expectedId);
    }

    @Story("Get pet by id")
    @Test
    @Tag("extended")
    @DisplayName("Check status 200 and object when get pet")
    void checkGetAndDataPetTest() {
        //Arrange
        int index = randomGenerator.getIndex(storePetsList.getNumberOfPets());
        StorePet currentPet = storePetsList.getPetByIndex(index);
        long currentPetId = currentPet.getId();

        Response response = petController.addStorePet(currentPet);
        logger.info("checkGetAndDataPetTest Add pet response statusCode: {}", response.statusCode());
        logger.info("checkGetAndDataPetTest Add response body: {}", response.asString());
        Assertions.assertThat(response.statusCode()).isEqualTo(200);
        logger.info("checkGetAndDataPetTest currentPetId id: {}", currentPetId);

        //Act
        Response getResponse = petController.getStorePetById(currentPetId);
        logger.info("checkGetAndDataPetTest Get pet response statusCode: {}", getResponse.statusCode());
        logger.info("checkGetAndDataPetTest Get response body: {}", getResponse.asString());

        //Assert
        Assertions.assertThat(getResponse.statusCode()).isEqualTo(200);
        logger.info("checkGetAndDataPetTest currentPet: {}", currentPet);
        StorePet actualGetPet = response.as(StorePet.class);
        logger.info("checkGetAndDataPetTest actualPet: {}", actualGetPet);
        Assertions.assertThat(actualGetPet).usingRecursiveComparison().isEqualTo(currentPet);
    }

    @Story("Update pet by id")
    @Test
    @Tag("smoke")
    @DisplayName("Check status 200 when update pet")
    void checkUpdateExistingPetTest() {
        //Arrange
        int index = randomGenerator.getIndex(storePetsList.getNumberOfPets());

        Response response = petController.addStorePet(storePetsList.getPetByIndex(index));
        logger.info("checkUpdateExistingPetTest Add pet response statusCode: {}", response.statusCode());
        logger.info("checkUpdateExistingPetTest Add response body: {}", response.asString());
        Assertions.assertThat(response.statusCode()).isEqualTo(200);

        StorePet changedPet = storePetsList.getPetByIndex(index);
        changedPet.setName("Albatros");
        changedPet.setStatus("sold");
        logger.info("checkUpdateExistingPetTest Changed pet: {}", changedPet);

        //Act
        Response putResponse = petController.updateStorePet(changedPet);
        logger.info("checkUpdateExistingPetTest Put pet response statusCode: {}", putResponse.statusCode());
        logger.info("checkUpdateExistingPetTest Put response body: {}", putResponse.asString());

        //Assert
        Assertions.assertThat(response.statusCode()).isEqualTo(200);
    }

    @Story("Update pet by id")
    @Test
    @Tag("extended")
    @DisplayName("Check status 200 and changed values when update pet")
    void checkUpdateValuesExistingPetTest() {
        //Arrange
        int index = randomGenerator.getIndex(storePetsList.getNumberOfPets());
        StorePet currentPet = storePetsList.getPetByIndex(index);
        logger.info("checkUpdateValuesExistingPetTest Current pet: {}", currentPet);

        Response response = petController.addStorePet(currentPet);
        logger.info("checkUpdateValuesExistingPetTest Add pet response statusCode: {}", response.statusCode());
        logger.info("checkUpdateValuesExistingPetTest Add response body: {}", response.asString());
        Assertions.assertThat(response.statusCode()).isEqualTo(200);

        StorePet changedPet = storePetsList.getPetByIndex(index);
        changedPet.setName("Albatros");
        changedPet.setStatus("sold");
        logger.info("checkUpdateValuesExistingPetTest Changed pet: {}", changedPet);

        //Act
        Response putResponse = petController.updateStorePet(changedPet);
        logger.info("checkUpdateValuesExistingPetTest Put pet response statusCode: {}", putResponse.statusCode());
        logger.info("checkUpdateValuesExistingPetTest Put response body: {}", putResponse.asString());

        //Assert
        Assertions.assertThat(response.statusCode()).isEqualTo(200);

        Response getResponse = petController.getStorePetById(changedPet.getId());
        logger.info("checkUpdateValuesExistingPetTest Get pet response statusCode: {}", getResponse.statusCode());
        logger.info("checkUpdateValuesExistingPetTest Get response body: {}", getResponse.asString());
        Assertions.assertThat(getResponse.statusCode()).isEqualTo(200);

        StorePet gotPet = getResponse.as(StorePet.class);
        logger.info("checkUpdateValuesExistingPetTest Got pet: {}", gotPet);
        Assertions.assertThat(gotPet).usingRecursiveComparison().isEqualTo(changedPet);
    }

    @Story("Delete pet by id")
    @Test
    @Tag("smoke")
    @DisplayName("Check status 200 when delete and 404 when get deleted pet")
    void checkDeletePetTest() {
        //Arrange
        int index = randomGenerator.getIndex(storePetsList.getNumberOfPets());
        long currentId = storePetsList.getPetByIndex(index).getId();

        Response response = petController.addStorePet(storePetsList.getPetByIndex(index));
        logger.info("checkDeletePetTes Add pet response statusCode: {}", response.statusCode());
        logger.info("checkDeletePetTes Add pet response body: {}", response.asString());
        Assertions.assertThat(response.statusCode()).isEqualTo(200);

        //Act
        Response deleteResponse = petController.deletePetById(currentId);
        logger.info("checkDeletePetTes Delete pet response statusCode: {}", deleteResponse.statusCode());
        logger.info("checkDeletePetTes Delete pet response body: {}", deleteResponse.asString());

        //Assert
        Assertions.assertThat(deleteResponse.statusCode()).isEqualTo(200);

        Response getResponse = petController.getStorePetById(currentId);
        logger.info("checkDeletePetTes Get deleted pet response statusCode: {}", getResponse.statusCode());
        logger.info("checkDeletePetTes Get deleted pet response body: {}", getResponse.asString());
        Assertions.assertThat(getResponse.statusCode()).isEqualTo(404);
    }

}
