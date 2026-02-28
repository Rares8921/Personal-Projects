package com.example.ihas.services;

import com.example.ihas.dao.SmartAssistantDAO;
import com.example.ihas.dao.SmartHubDAO;
import com.example.ihas.dao.UserDAO;
import com.example.ihas.devices.SmartAssistant;
import com.example.ihas.devices.SmartHub;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SmartAssistantService {

    private final SmartAssistantDAO dao;
    private final SmartHubDAO hubDao;
    private final UserDAO userDao;

    public SmartAssistantService(SmartAssistantDAO _dao, SmartHubDAO _hubDao, UserDAO _userDao) {
        dao = _dao;
        hubDao = _hubDao;
        userDao = _userDao;
    }

    public SmartAssistant get(String id, String userId) {
        return dao.findById(id, userId);
    }

    public List<SmartAssistant> getAll(String userId) {
        return dao.findAll(userId);
    }

    public void add(SmartAssistant assistant, String userEmail) {
        String userId = userDao.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail))
                .getId()
                .toString();

        dao.save(assistant, userId);
    }

    public void delete(String id, String userId) {
        dao.delete(id, userId);
    }

    public void update(SmartAssistant assistant, String userId) {
        dao.update(assistant, userId);
    }

    public void processVoiceCommand(String assistantId, String command, String userId) {
        SmartAssistant assistant = dao.findById(assistantId, userId);
        SmartHub hub = hubDao.findById(assistant.getHubId(), userId);
        assistant.processVoiceCommand(command, hub);
        dao.update(assistant, userId);
    }
}
