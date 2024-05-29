package com.nhom14.webbookstore.service;

import com.nhom14.webbookstore.entity.Account;
import com.nhom14.webbookstore.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationService {
    void sendPrivateNotification(int accountId);

    Notification save(Notification notification);

    void notifyAccount(int accountId, Notification notification);

    Page<Notification> findAllByAccountReceived(Account account, Pageable pageable);

    Notification getNewNotification(Account account);

    Notification getNotificationById(long notificationId);
}
