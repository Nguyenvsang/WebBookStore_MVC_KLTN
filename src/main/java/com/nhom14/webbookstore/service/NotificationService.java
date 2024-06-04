package com.nhom14.webbookstore.service;

import com.nhom14.webbookstore.entity.Account;
import com.nhom14.webbookstore.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationService {

    Notification save(Notification notification);



    Page<Notification> findAllByAccountReceived(Account account, Pageable pageable);

    Notification getNewNotification(Account account);

    Notification getNotificationById(long notificationId);

    void markAllAsReadForAccount(Account account);
}
