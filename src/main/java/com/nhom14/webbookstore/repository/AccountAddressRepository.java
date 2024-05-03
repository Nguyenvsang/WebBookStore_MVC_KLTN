package com.nhom14.webbookstore.repository;

import com.nhom14.webbookstore.entity.Account;
import com.nhom14.webbookstore.entity.AccountAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountAddressRepository extends JpaRepository<AccountAddress, Integer> {
    List<AccountAddress> findByAccountId(int accountId);

    @Query("SELECT aa FROM AccountAddress aa WHERE aa.account = :account AND aa.isDefault = 1")
    AccountAddress findDefaultAddressByAccount(Account account);

    List<AccountAddress> findAllByAccount(Account account);

    List<AccountAddress> findAllByAccountOrderByIsDefaultDescIdDesc(Account account);
}
