package com.nhom14.webbookstore.service;

import com.nhom14.webbookstore.entity.Account;
import com.nhom14.webbookstore.entity.AccountAddress;

import java.util.List;

public interface AccountAddressService {
    List<AccountAddress> getAccountAddressesByAccountId(int accountId);
    void addAccountAddress(AccountAddress accountAddress);
    void updateAccountAddress(AccountAddress accountAddress);
    void deleteAccountAddress(int accountAddressId);

    AccountAddress getDefaultAddress(Account account);

    List<AccountAddress> getAddressesByAccount(Account account);

    AccountAddress getAccountAddressById(int id);
}
