package com.example.currencybank.service;

import com.example.currencybank.dto.HistoryDto;
import com.example.currencybank.dto.UserRegistrationDto;
import com.example.currencybank.entity.User;

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
