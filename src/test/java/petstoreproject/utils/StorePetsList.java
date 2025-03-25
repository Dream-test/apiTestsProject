package petstoreproject.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import petstoreproject.models.StorePet;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class StorePetsList {
    private final Logger logger = LoggerFactory.getLogger(StorePetsList.class);
    private final String petsFilePath = "src/test/resources/pets.json";
    private List<StorePet> petsList;

    public StorePetsList() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            this.petsList = objectMapper.readValue(new File(petsFilePath), new TypeReference<>() {});
        } catch (IOException e) {
            logger.error("Error reading file pets.json", e);
            this.petsList = List.of();
        }
    }

    public int getNumberOfPets() {
        return petsList.size();
    }

    public StorePet getDefaultPet() {
        return petsList.getFirst();
    }

    public StorePet getPetByIndex(int index) {
        return petsList.get(index);
    }

    public long[] petsId() {
        long[] petsId = new long[petsList.size()];
        for (int i = 0; i < petsList.size(); i++) {
            petsId[i] = petsList.get(i).getId();
        }
        return petsId;
    }
}
