package com.nhom14.webbookstore.repository;

import com.nhom14.webbookstore.entity.Account;
import com.nhom14.webbookstore.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Page<Notification> findAllByReceiver(Account account, Pageable pageable);

    List<Notification> findAllByReceiverAndStatusOrderBySentTimeDesc(Account account, int i);
}
