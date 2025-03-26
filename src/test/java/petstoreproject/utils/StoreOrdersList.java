package petstoreproject.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import petstoreproject.models.StoreOrder;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class StoreOrdersList {
    private final Logger logger = LoggerFactory.getLogger(StoreOrdersList.class);
    private final String ordersFilePath = "src/test/resources/orders.json";
    private List<StoreOrder> ordersList;

    public StoreOrdersList() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            this.ordersList = objectMapper.readValue(new File(ordersFilePath), new TypeReference<>() {});
        } catch (IOException e) {
            logger.error("Error reading file orders.json");
        }
    }

    public int getNumberOfOrders() {
        return ordersList.size();
    }

    public StoreOrder getDefaultOrder() {
        return ordersList.getFirst();
    }

    public StoreOrder getOrderByIndex(int index) {
        return ordersList.get(index);
    }

    public long[] ordersId() {
        long[] ordersId = new long[ordersList.size()];
        for (int i = 0; i < ordersList.size(); i++) {
            ordersId[i] = ordersList.get(i).getId();
        }
        return ordersId;
    }
}
