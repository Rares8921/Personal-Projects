package com.example.ihas.services;

import com.example.ihas.dao.SmartRefrigeratorDAO;
import com.example.ihas.dao.SmartRefrigeratorItemDAO;
import com.example.ihas.devices.SmartRefrigerator;
import com.example.ihas.devices.SmartRefrigeratorItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class SmartRefrigeratorService {

    private final SmartRefrigeratorDAO dao;

    @Autowired
    private SmartRefrigeratorItemDAO itemDAO;

    public SmartRefrigeratorService(SmartRefrigeratorDAO _dao) {
        dao = _dao;
    }

    public SmartRefrigerator getRefrigerator(String id, String user_id) {
        return dao.findById(id, user_id);
    }

    public List<SmartRefrigerator> getAllRefrigerators(String user_id) {
        return dao.findAllByUser(user_id);
    }

    public void addRefrigerator(SmartRefrigerator refrigerator, String user_id) {
        dao.save(refrigerator, user_id);
    }

    public void deleteRefrigerator(String id, String user_id) {
        dao.delete(id, user_id);
    }

    public void setTemperature(String id, double temp, String user_id) {
        SmartRefrigerator fr = dao.findById(id, user_id);
        fr.setTemperature(temp);
        dao.update(fr, user_id);
    }

    public void updateTemperature(String id, double temp, String user_id) {
        SmartRefrigerator ref = dao.findById(id, user_id);
        ref.setTemperature(temp);
        dao.update(ref, user_id);
    }

    public void toggleDoor(String id, String user_id) {
        SmartRefrigerator fr = dao.findById(id, user_id);
        fr.togglePower();
        dao.update(fr, user_id);
    }

    public void addItem(String id, String item, String expiry, String user_id) {
        SmartRefrigerator ref = dao.findById(id, user_id);
        ref.addItem(item, LocalDate.parse(expiry));
        dao.update(ref, user_id);
    }

    public void removeItem(String id, String item, String user_id) {
        SmartRefrigerator ref = dao.findById(id, user_id);
        ref.removeItem(item);
        dao.update(ref, user_id);
    }

    public String checkExpiredItems(String refrigeratorId, String userId) {
        List<SmartRefrigeratorItem> items = itemDAO.findByRefrigeratorId(refrigeratorId);

        List<String> expired = items.stream()
                .filter(i -> i.getExpiryDate().isBefore(LocalDate.now()))
                .map(SmartRefrigeratorItem::getName)
                .toList();

        if (expired.isEmpty()) {
            return "No expired items.";
        } else {
            return "Expired items: " + String.join(", ", expired);
        }
    }

    public List<SmartRefrigeratorItem> getItems(String refrigeratorId) {
        return itemDAO.findByRefrigeratorId(refrigeratorId);
    }

    public void addItem(String refrigeratorId, String name, LocalDate expiryDate) {
        System.out.println(">> Saving item: " + name + ", " + expiryDate + ", fridge=" + refrigeratorId);
        SmartRefrigeratorItem item = new SmartRefrigeratorItem();
        item.setName(name);
        item.setExpiryDate(expiryDate);
        item.setRefrigeratorId(refrigeratorId);
        itemDAO.save(item);
    }

    public void deleteItem(Long itemId) {
        itemDAO.deleteById(itemId);
    }

    public void deleteExpiredItems(String refrigeratorId) {
        itemDAO.deleteExpired(refrigeratorId);
    }

}
