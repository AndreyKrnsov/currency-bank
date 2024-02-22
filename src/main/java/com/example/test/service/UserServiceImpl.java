package com.example.test.service;

import com.example.test.dto.HistoryDto;
import com.example.test.dto.UserRegistrationDto;
import com.example.test.entity.Account;
import com.example.test.entity.Operation;
import com.example.test.entity.Role;
import com.example.test.entity.User;
import com.example.test.repository.AccountRepository;
import com.example.test.repository.OperationsRepository;
import com.example.test.repository.RoleRepository;
import com.example.test.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.util.StringUtils.capitalize;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AccountRepository accountRepository;
    private final OperationsRepository operationsRepository;
    private final PasswordEncoder passwordEncoder;


    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder,
                           AccountRepository accountRepository,
                           OperationsRepository operationsRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.accountRepository = accountRepository;
        this.operationsRepository = operationsRepository;
    }

    @Override
    public User save (UserRegistrationDto registrationDto) {
        User user = new User();
        user.setName(capitalize(registrationDto.getFirstName().trim()) + " " + capitalize(registrationDto.getLastName().trim()));
        user.setUsername(registrationDto.getUsername());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));

        Account account = new Account();
        account.setBalance(0);
        account.setCurrency("RUB");
        accountRepository.save(account);
        user.setAccounts(List.of(account));

        Role role = roleRepository.findByName("ROLE_USER");
        if(role == null){
            role = checkRoleExist();
        }
        user.setRoles(List.of(role));
        userRepository.save(user);
        account.setUser(user);
        accountRepository.save(account);
        return user;

    }

    @Override
    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    @Override
    public List<UserRegistrationDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .filter(User::isUser)
                .map(this::mapToUserDto)
                .collect(Collectors.toList());
    }

    private UserRegistrationDto mapToUserDto(User user){
        UserRegistrationDto userDto = new UserRegistrationDto();
        String[] name = user.getName().split(" ");
        userDto.setFirstName(name[0]);
        userDto.setLastName(name[1]);
        userDto.setUsername(user.getUsername());
        return userDto;
    }
    private Role checkRoleExist(){
        Role role = new Role();
        role.setName("ROLE_USER");
        return roleRepository.save(role);
    }

    @Override
    public void deleteAccount(long accountId, String user) {
        User usr = findUserByUsername(user);
        Account rubAccount = usr.getAccounts().get(0);
        Account account = accountRepository.findById(accountId);
        CurrencyConverter currencyConverter = new CurrencyConverter();
        double rate = currencyConverter.getCurrencyRate(account.getCurrency());
        rubAccount.setBalance(rubAccount.getBalance() + account.getBalance() * rate);
        usr.getAccounts().removeIf(acc -> acc.getId() == accountId);

        if (account.getBalance() != 0) {
            Operation operation  = new Operation();
            List<Long> accounts = new ArrayList<>();
            accounts.add(accountId);
            accounts.add(rubAccount.getId());
            operation.setAccounts(accounts);
            operation.setUser(usr);
            operation.setType("Продажа " + account.getCurrency());
            operation.setDate(Instant.now());
            operation.setAmount(account.getBalance());
            operation.setRate(currencyConverter.getCurrencyRate(account.getCurrency()));
            usr.addOperation(operation);
        }

        userRepository.save(usr);
    }

    @Override
    public void createAccount(String currency, String user) {
        Account account = new Account();
        account.setBalance(0);
        account.setCurrency(currency);
        account.setUser(userRepository.findByUsername(user));
        accountRepository.save(account);
    }

    @Override
    public List<HistoryDto> mapToHistoryDto(User user) {
        List<HistoryDto> historyDtos = new ArrayList<>();
        DecimalFormatSymbols formatSymbols = new DecimalFormatSymbols(Locale.getDefault());
        formatSymbols.setDecimalSeparator(',');
        formatSymbols.setGroupingSeparator(' ');
        DecimalFormat numberFormat = new DecimalFormat("###,###.##", formatSymbols);
        DecimalFormat rateFormat = new DecimalFormat("#.#### ₽", formatSymbols);
        DateTimeFormatter formatter = DateTimeFormatter
                .ofPattern("dd MMMM/yyyy/HH:mm")
                .withLocale(new Locale("ru"));
        for (Operation operation : user.getOperations()) {
            HistoryDto dto = new HistoryDto();
            List<Long> accounts = operation.getAccounts();
            if (accounts.size() == 2) {
                dto.setRate(rateFormat.format(operation.getRate()));
                String cur = operation.getType().split(" ")[1];
                String type = operation.getType().split(" ")[0];
                if (type.equals("Покупка")) {
                    dto.setAmount(numberFormat.format(operation.getAmount())
                            + " " + "₽ → "
                            + numberFormat.format(operation.getAmount() * operation.getRate())+ " "
                            + cur);
                } else {
                    dto.setAmount(numberFormat.format(operation.getAmount())
                            + " " + cur + " → "
                            + numberFormat.format(operation.getAmount() * operation.getRate())+ " "
                            + "₽");
                }

                dto.setAccounts(String.format("•• %04d  ", accounts.get(0))
                        + String.format("→  •• %04d", accounts.get(1)));
            } else {
                if (operation.getType().equals("Вывод")) {
                    dto.setAmount("− " + numberFormat.format(operation.getAmount()) + " ₽");
                } else {
                    dto.setAmount("+ " + numberFormat.format(operation.getAmount()) + " ₽");
                }


                dto.setAccounts(String.format("•• %04d", accounts.get(0)));
            }
            ZonedDateTime zdt = ZonedDateTime.ofInstant(operation.getDate(), ZoneId.systemDefault());
            String date = zdt.format(formatter);
            dto.setDate(date.split("/")[0]);
            dto.setYear(date.split("/")[1]);
            dto.setTime(date.split("/")[2]);
            dto.setType(operation.getType());
            historyDtos.add(dto);
        }
        Collections.reverse(historyDtos);
        return historyDtos;
    }

    @Override
    @Transactional
    public void transferIn(Double amount, Long accountId) {
        Optional<Account> optionalAccount = accountRepository.findById(accountId);
        if (optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            account.setBalance(account.getBalance() + amount);
            accountRepository.save(account);

            Operation operation  = new Operation();
            List<Long> accounts = new ArrayList<>();
            accounts.add(accountId);
            operation.setAccounts(accounts);
            operation.setUser(account.getUser());
            operation.setType("Пополнение");
            operation.setDate(Instant.now());
            operation.setAmount(amount);
            operation.setRate(0);
            account.getUser().addOperation(operation);
            userRepository.save(account.getUser());
        }

    }

    @Override
    @Transactional
    public void transferOut(Double amount, Long accountId) {
        Optional<Account> optionalAccount = accountRepository.findById(accountId);
        if (optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            account.setBalance(account.getBalance() - amount);
            accountRepository.save(account);

            Operation operation  = new Operation();
            List<Long> accounts = new ArrayList<>();
            accounts.add(accountId);
            operation.setAccounts(accounts);
            operation.setUser(account.getUser());
            operation.setType("Вывод");
            operation.setDate(Instant.now());
            operation.setAmount(amount);
            operation.setRate(0);
            account.getUser().addOperation(operation);
            userRepository.save(account.getUser());
        }
    }

    @Override
    @Transactional
    public void exchangeCurrency(Double amount, Long accountId1, Long accountId2, String operationName) {
        Optional<Account> optAccount1 = accountRepository.findById(accountId1);
        Optional<Account> optAccount2 = accountRepository.findById(accountId2);
        CurrencyConverter currencyConverter = new CurrencyConverter();
        if (optAccount1.isPresent() && optAccount2.isPresent()) {
            Account account1 = optAccount1.get();
            Account account2 = optAccount2.get();
            double rate1;
            if (!account1.getCurrency().equals("RUB")) {
                rate1 = currencyConverter.getCurrencyRate(account1.getCurrency());
            } else {
                rate1 = 1.0;
            }
            double rate2;
            if (!account2.getCurrency().equals("RUB")) {
                rate2 = currencyConverter.getCurrencyRate(account2.getCurrency());
            } else {
                rate2 = 1.0;
            }

            double rate = rate1 / rate2;

            account1.setBalance(account1.getBalance() - amount);
            account2.setBalance(account2.getBalance() + amount * rate);
            accountRepository.save(account1);
            accountRepository.save(account2);

            Operation operation  = new Operation();
            List<Long> accounts = new ArrayList<>();
            User user = account1.getUser();
            accounts.add(accountId1);
            accounts.add(accountId2);
            operation.setAccounts(accounts);
            operation.setUser(user);
            operation.setType(operationName);
            operation.setDate(Instant.now());
            operation.setAmount(amount);
            operation.setRate(rate);
            user.addOperation(operation);
            userRepository.save(user);
        }
    }
}
