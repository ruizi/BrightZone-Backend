package com.horsemenoftheocics.brightzone.service.impl;

import com.horsemenoftheocics.brightzone.entity.Account;
import com.horsemenoftheocics.brightzone.entity.Person;
import com.horsemenoftheocics.brightzone.entity.Request;
import com.horsemenoftheocics.brightzone.enums.AccountStatus;
import com.horsemenoftheocics.brightzone.enums.RequestStatus;
import com.horsemenoftheocics.brightzone.enums.RequestType;
import com.horsemenoftheocics.brightzone.repository.AccountRepository;
import com.horsemenoftheocics.brightzone.repository.PersonRepository;
import com.horsemenoftheocics.brightzone.repository.RequestRepository;
import com.horsemenoftheocics.brightzone.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.util.*;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private JavaMailSender javaMailSender;

    @Override
    @Transactional
    public Map<String, Object> registerAccount(String emailOrUserId) {
        emailOrUserId = emailOrUserId.trim();
        HashMap<String, Object> map = new HashMap<>();
        if (StringUtils.isEmpty(emailOrUserId)) {
            map.put("success", false);
            map.put("errMsg", "Username shouldn't be empty!");
            return map;
        }

        boolean registerWithEmail = emailOrUserId.contains("@");
        Optional<Person> optionalPerson;
        if (registerWithEmail) {
            optionalPerson = personRepository.findByEmail(emailOrUserId);
        } else {
            int personId = Integer.parseInt(emailOrUserId);
            optionalPerson = personRepository.findById(personId);
        }

        if (optionalPerson.isEmpty()) {  // user is not a member of CMS
            map.put("success", false);
            map.put("errMsg", "You are not allowed to register an account in CMS");
            return map;
        }

        Optional<Account> optionalAccount;
        if (registerWithEmail) {
            optionalAccount = accountRepository.findByEmail(optionalPerson.get().getEmail());
        } else {
            optionalAccount = accountRepository.findById(optionalPerson.get().getPersonId());
        }

        if (optionalAccount.isPresent()) {  // user had an account
            AccountStatus accountStatus = optionalAccount.get().getAccountStatus();
            map.put("success", false);
            if (AccountStatus.unauthorized.equals(accountStatus))  {
                map.put("errMsg", "Please wait for Admin's authorization");
            } else {
                map.put("errMsg", "You already have an account, and you are not allowed to register a new one");
            }
            return map;
        }

        Account account = new Account();
        Person person = optionalPerson.get();
        account.setUserId(person.getPersonId());
        account.setName(person.getName());
        account.setType(person.getType());
        account.setFacultyId(person.getFacultyId());
        account.setProgram(person.getProgram());
        account.setEmail(person.getEmail());
        // do not set password
        account.setAccountStatus(AccountStatus.unauthorized);
        // do not set lastLogin
        // do not set verification code
        Account save = accountRepository.save(account);
        map.put("success", true);
        map.put("account", save);
        return map;
    }

    @Override
    @Transactional
    public Map<String, Object> login(String emailOrUserId, String password) {
        emailOrUserId = emailOrUserId.trim();
        password = password.trim();
        HashMap<String, Object> map = new HashMap<>();
        if (StringUtils.isEmpty(emailOrUserId) || StringUtils.isEmpty(password)) {
            map.put("success", false);
            map.put("errMsg", "Username or password shouldn't be empty");
            return map;
        }

        boolean loginWithEmail = emailOrUserId.contains("@");
        Optional<Account> optionalAccount;
        if (loginWithEmail) {
            optionalAccount = accountRepository.findByEmail(emailOrUserId);
        } else {
            int userId = Integer.parseInt(emailOrUserId);
            optionalAccount = accountRepository.findById(userId);
        }

        if (optionalAccount.isEmpty()) {  // invalid account
            map.put("success", false);
            map.put("errMsg", "User doesn't exist, please register an account");
            return map;
        }

        Account account = optionalAccount.get();
        if (AccountStatus.unauthorized.equals(account.getAccountStatus())) {  // invalid account
            map.put("success", false);
            map.put("errMsg", "User is not authorized, please wait for admin’s authorization");
            return map;
        }

        if (!password.equals(account.getPassword())) {
            map.put("success", false);
            map.put("errMsg", "Wrong password");
        } else {
            account.setLastLogin(new Timestamp(System.currentTimeMillis()));
            Account save = accountRepository.save(account);
            map.put("success", true);
            map.put("account", save);
        }
        return map;
    }

    @Override
    @Transactional
    public Map<String, Object> createRequest(int accountId, String requestMessage, String requestType) {
        requestMessage = requestMessage.trim();
        requestType = requestType.trim();
        HashMap<String, Object> map = new HashMap<>();
        if (StringUtils.isEmpty(requestMessage) || StringUtils.isEmpty(requestType)) {
            map.put("success", false);
            map.put("errMsg", "Input message shouldn't be empty");
            return map;
        }

        Request request = new Request();
        request.setUserId(accountId);
        request.setMessage(requestMessage);
        request.setStatus(RequestStatus.open);
        request.setType(RequestType.valueOf(requestType));
        Request save = requestRepository.save(request);
        map.put("success", true);
        map.put("request", save);
        return map;
    }

    @Override
    @Transactional
    public Map<String, Object> passwordRecovery(String email, String verificationCode, String newPassword) {
        email = email.trim();
        verificationCode = verificationCode.trim();
        newPassword = newPassword.trim();
        HashMap<String, Object> map = new HashMap<>();
        if (StringUtils.isEmpty(email) || StringUtils.isEmpty(verificationCode) || StringUtils.isEmpty(newPassword)) {
            map.put("success", false);
            map.put("errMsg", "Input message shouldn't be empty");
            return map;
        }

        Optional<Account> optionalAccount = accountRepository.findByEmail(email);
        if (optionalAccount.isEmpty()) {
            map.put("success", false);
            map.put("errMsg", "User doesn't exist, please register an account first");
            return map;
        }

        Account account = optionalAccount.get();
        if (AccountStatus.unauthorized.equals(account.getAccountStatus())) {
            map.put("success", false);
            map.put("errMsg", "User is not authorized, please wait for admin’s authorization");
            return map;
        }

        if (!verificationCode.equals(account.getVerificationCode())) {
            map.put("success", false);
            map.put("errMsg", "Wrong verification code");
        } else {
            account.setPassword(newPassword);
            Account save = accountRepository.save(account);
            map.put("success", true);
            map.put("account", save);
        }
        return map;
    }

    @Override
    @Transactional
    public Map<String, Object> sendVerificationCode(String email) {
        email = email.trim();
        HashMap<String, Object> map = new HashMap<>();

        if (StringUtils.isEmpty(email)) {
            map.put("success", false);
            map.put("errMsg", "Email shouldn't be empty");
            return map;
        }

        Optional<Account> optionalAccount = accountRepository.findByEmail(email);
        if (optionalAccount.isEmpty()) {
            map.put("success", false);
            map.put("errMsg", "User doesn't exist, please register an account first");
            return map;
        }

        Account account = optionalAccount.get();
        if (AccountStatus.unauthorized.equals(account.getAccountStatus())) {
            map.put("success", false);
            map.put("errMsg", "User is not authorized, please wait for admin’s authorization");
            return map;
        }

        String verificationCode = this.getVerificationCode(6);
        account.setVerificationCode(verificationCode);
        Account save = accountRepository.save(account);
        Thread thread = new Thread(() -> sendEmail("cmsserver123@gmail.com", account.getEmail(),
                "Password Recovery", "Verification Code:" + verificationCode));
        thread.start();
        map.put("success", true);
        map.put("account", save);
        return map;
    }

    private String getVerificationCode(int length) {
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < length; ++i) {
            code.append(new Random().nextInt(10));
        }
        return code.toString();
    }

    private void sendEmail(String from, String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        javaMailSender.send(message);
    }

    @Override
    @Transactional
    public Map<String, Object> updateEmail(int userId, String email) {
        email = email.trim();
        HashMap<String, Object> map = new HashMap<>();

        if (StringUtils.isEmpty(email)) {
            map.put("success", false);
            map.put("errMsg", "New email shouldn't be empty");
            return map;
        }

        List<Account> accounts = accountRepository.findAllByEmailAndUserIdNot(email, userId);
        if (accounts.size() > 0) {
            map.put("success", false);
            map.put("errMsg", "New email has already been occupied by other users");
            return map;
        }

        Optional<Account> optionalAccount = accountRepository.findById(userId);
        if (optionalAccount.isEmpty()) {
            throw new RuntimeException("Can't find account with userId " + userId + " in database.");
        }

        Account account = optionalAccount.get();
        account.setEmail(email);
        Account save = accountRepository.save(account);
        map.put("success", true);
        map.put("account", save);
        return map;
    }

    @Override
    @Transactional
    public Map<String, Object> updatePassword(int userId, String password) {
        password = password.trim();
        HashMap<String, Object> map = new HashMap<>();

        if (StringUtils.isEmpty(password)) {
            map.put("success", false);
            map.put("errMsg", "New password shouldn't be empty");
            return map;
        }

        Optional<Account> optionalAccount = accountRepository.findById(userId);
        if (optionalAccount.isEmpty()) {
            throw new RuntimeException("Can't find account with userId " + userId + " in database.");
        }

        Account account = optionalAccount.get();
        account.setPassword(password);
        Account save = accountRepository.save(account);
        map.put("success", true);
        map.put("account", save);
        return map;
    }
}
