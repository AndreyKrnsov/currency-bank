package com.example.currencybank.controller;

import com.example.currencybank.dto.AccountDto;
import com.example.currencybank.entity.Account;
import com.example.currencybank.entity.User;
import com.example.currencybank.repository.AccountRepository;
import com.example.currencybank.service.CurrencyConverter;
import com.example.currencybank.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class MainController {

    private final UserService userService;
    private final AccountRepository accountRepository;

    public MainController(UserService userService, AccountRepository accountRepository) {
        this.userService = userService;
        this.accountRepository = accountRepository;
    }

    @GetMapping("/index")
    public String home() {
        return "index";
    }

    @GetMapping("/admin")
    public String adminHome(Model model, Principal principal) {
        model.addAttribute("user", userService.findUserByUsername(principal.getName()));
        return "admin";
    }

    @GetMapping("/")
    public String main(Model model, Principal principal) {
        User user = userService.findUserByUsername(principal.getName());
        double balance = 0;
        CurrencyConverter converter = new CurrencyConverter();
        for (Account account : user.getAccounts()) {
            balance += account.getBalance() * converter.getCurrencyRate(account.getCurrency());
        }
        String pens = String.format("%.2f", balance).split("\\.")[1];
        model.addAttribute("balance", (int) balance);
        model.addAttribute("pens", pens);
        model.addAttribute("user", user);
        return "home";
    }

    @GetMapping("/accounts")
    public String viewAccounts(Model model, Principal principal) {
        User user = userService.findUserByUsername(principal.getName());
        CurrencyConverter currencyConverter = new CurrencyConverter();
        model.addAttribute("user", user);
        List<String> userCurrencies = user.getAccounts()
                .stream()
                .map(Account::getCurrency)
                .toList();
        model.addAttribute("userCurrencies", userCurrencies);
        model.addAttribute("currencies", currencyConverter.getCurrenciesCharCodes());
        return "accounts";
    }

    @PostMapping("/accounts")
    public String manageAccounts(@RequestParam(required = true, defaultValue = "" ) String action,
                                 @RequestParam(required = false) String accountId,
                                 @RequestParam(required = false) String currency,
                                 Principal principal) {
        if (action.equals("delete")) {
            userService.deleteAccount(Long.parseLong(accountId), principal.getName());
        }
        if (action.equals("create")) {
            userService.createAccount(currency, principal.getName());
        }
        return "redirect:/accounts";
    }

    @GetMapping("/transfers")
    public String viewTransfers(Model model, Principal principal) {
        User user = userService.findUserByUsername(principal.getName());
        Account mainAccount = user.getAccounts().get(0);
        AccountDto mainAccountDto = new AccountDto();
        mainAccountDto.setId(mainAccount.getId());
        mainAccountDto.setBalance(mainAccount.getBalance());
        model.addAttribute("mainAccount", mainAccountDto);
        model.addAttribute("user", user);
        return "transfers";
    }

    @PostMapping("/transfers")
    public String manageTransfer(@RequestParam(required = true, defaultValue = "") String action,
                                 @RequestParam(required = false) Double amount,
                                 Principal principal) {
        User user = userService.findUserByUsername(principal.getName());
        Account account = user.getAccounts().get(0);
        if (action.equals("put-in")) {
            userService.transferIn(amount, account.getId());
        }
        if (action.equals("take-out")) {
            userService.transferOut(amount, account.getId());
            return "redirect:/transfers?out";
        }
        return "redirect:/transfers";
    }

    @GetMapping("/exchange")
    public String viewExchange(Model model, Principal principal) {
        User user = userService.findUserByUsername(principal.getName());
        Account mainAccount = user.getAccounts().get(0);
        AccountDto mainAccountDto = new AccountDto();
        mainAccountDto.setId(mainAccount.getId());
        mainAccountDto.setBalance(mainAccount.getBalance());

        CurrencyConverter currencyConverter = new CurrencyConverter();
        List<Account> accounts = user.getAccounts();
        List<AccountDto> accountDtos = new ArrayList<>();

        for (int i = 1; i < accounts.size(); i++) {
            Account account = accounts.get(i);
            AccountDto accountDto = new AccountDto();
            accountDto.setId(account.getId());
            accountDto.setCurrency(account.getCurrency());
            accountDto.setRate(currencyConverter.getCurrencyRate(account.getCurrency()));
            accountDto.setBalance(account.getBalance());
            accountDtos.add(accountDto);
        }
        model.addAttribute("mainAccount", mainAccountDto);
        model.addAttribute("accounts", accountDtos);
        model.addAttribute("user", user);
        return "exchange";
    }

    @PostMapping("/exchange")
    public String manageExchange(@RequestParam(required = true, defaultValue = "") String action,
                                 @RequestParam(required = true, defaultValue = "0") Double amount,
                                 @RequestParam(required = true, defaultValue = "0") Long accountId,
                                 Principal principal) {
        User user = userService.findUserByUsername(principal.getName());
        Account mainAccount = user.getAccounts().get(0);
        Account exchangeAccount;
        Optional<Account> optExchangeAccount = accountRepository.findById(accountId);
        if (optExchangeAccount.isPresent()) {
            exchangeAccount = optExchangeAccount.get();
        } else {
            return "redirect:/exchange";
        }


        if (action.equals("buy")) {
            userService.exchangeCurrency(amount, mainAccount.getId(), accountId,
                    "Покупка " + exchangeAccount.getCurrency());
        }
        if (action.equals("sell")) {
            userService.exchangeCurrency(amount, accountId, mainAccount.getId(),
                    "Продажа " + exchangeAccount.getCurrency());
            return "redirect:/exchange?sell";
        }
        return "redirect:/exchange";
    }

    @GetMapping("/history")
    public String viewHistory(Model model, Principal principal) {
        User user = userService.findUserByUsername(principal.getName());
        model.addAttribute("user", userService.findUserByUsername(principal.getName()));
        model.addAttribute("operations", userService.mapToHistoryDto(user));
        return "history";
    }
}
