package petstoreproject.utils;

import apiproject.objects.PetStoreUser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import petstoreproject.models.StoreUser;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class StoreUserList {
    private final Logger logger = LoggerFactory.getLogger(StoreUserList.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String userFilePath = "src/test/resources/users.json";
    private List<StoreUser> usersList;
    private String jsonBody;

    public StoreUserList() {
        try {
            this.usersList = objectMapper.readValue(new File(userFilePath), new TypeReference<>() {});
            this.jsonBody = objectMapper.writeValueAsString(
                    objectMapper.readTree(new File(userFilePath)) // Читает JSON как дерево
            );
        } catch (IOException e) {
            logger.error("Error reading file user.json", e);
            this.usersList = List.of(); // Пустой список
            this.jsonBody = "[]";
        }
    }

    public int getNumberOfUsers() {
        return usersList.size();
    }

    public StoreUser getDefaultUser() {
        return usersList.isEmpty() ? null : usersList.getFirst();//Проверка и return null если список пуст
    }

    public StoreUser getByIndex(int index) {
        return usersList.isEmpty() ? null : usersList.get(index);
    }

    public String allUsersAsString() {
        return jsonBody;
    }
}
