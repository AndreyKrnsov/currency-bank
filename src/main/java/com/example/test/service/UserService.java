package com.example.test.service;

import com.example.test.dto.HistoryDto;
import com.example.test.dto.UserRegistrationDto;
import com.example.test.entity.User;

import java.util.List;

public interface UserService {
    User save(UserRegistrationDto registrationDto);
    List<UserRegistrationDto> getAllUsers();
    User findUserByUsername(String username);
    void deleteAccount(long accountId, String user);
    void createAccount(String currency, String user);
    List<HistoryDto> mapToHistoryDto(User user);

    void transferIn(Double amount, Long accountId);

    void transferOut(Double amount, Long accountId);

    void exchangeCurrency(Double amount, Long accountId1, Long accountId2, String operationName);
}
