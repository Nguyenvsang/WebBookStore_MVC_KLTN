package com.nhom14.webbookstore.service.impl;

import com.nhom14.webbookstore.entity.Account;
import com.nhom14.webbookstore.entity.AccountAddress;
import com.nhom14.webbookstore.repository.AccountAddressRepository;
import com.nhom14.webbookstore.service.AccountAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountAddressServiceImpl implements AccountAddressService {

    private AccountAddressRepository accountAddressRepository;

    @Autowired
    public AccountAddressServiceImpl(AccountAddressRepository accountAddressRepository) {
        super();
        this.accountAddressRepository = accountAddressRepository;
    }

    @Override
    public List<AccountAddress> getAccountAddressesByAccountId(int accountId) {
        return accountAddressRepository.findByAccountId(accountId);
    }

    @Override
    public void addAccountAddress(AccountAddress accountAddress) {
        accountAddressRepository.save(accountAddress);
    }

    @Override
    public void updateAccountAddress(AccountAddress accountAddress) {
        accountAddressRepository.save(accountAddress);
    }

    @Override
    public void deleteAccountAddress(int accountAddressId) {
        accountAddressRepository.deleteById(accountAddressId);
    }

    @Override
    public AccountAddress getDefaultAddress(Account account) {
        return accountAddressRepository.findDefaultAddressByAccount(account); // không có sẽ trả về null
    }

    @Override
    public List<AccountAddress> getAddressesByAccount(Account account) {
        return accountAddressRepository.findAllByAccountOrderByIsDefaultDescIdDesc(account);
    }

    @Override
    public AccountAddress getAccountAddressById(int id) {
        return accountAddressRepository.findById(id).orElse(null);
    }
}
