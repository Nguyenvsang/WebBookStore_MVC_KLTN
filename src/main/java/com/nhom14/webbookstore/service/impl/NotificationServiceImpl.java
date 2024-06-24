package com.nhom14.webbookstore.service.impl;

import com.nhom14.webbookstore.entity.Account;
import com.nhom14.webbookstore.entity.Notification;
import com.nhom14.webbookstore.model.response_model.NotificationResponseModel;
import com.nhom14.webbookstore.repository.NotificationRepository;
import com.nhom14.webbookstore.service.NotificationService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {
    private NotificationRepository notificationRepository;
    private ModelMapper modelMapper;

    @Autowired
    public NotificationServiceImpl(NotificationRepository notificationRepository, ModelMapper modelMapper) {
        super();
        this.notificationRepository = notificationRepository;
        this.modelMapper = modelMapper;
    }

//    @Override
//    public void sendPrivateNotification(final int accountId) {
//        Notification notification = new Notification();
//        simpMessagingTemplate.convertAndSendToUser(String.valueOf(accountId), "/topic/private-notifications", notification);
//    }

    @Override
    public Notification save(Notification notification) {
        return notificationRepository.save(notification);
    }

//    @Override
//    public void notifyAccount(int accountId, Notification notification) {
//        NotificationResponseModel notificationResponseModel = modelMapper.map(notification, NotificationResponseModel.class);
//        simpMessagingTemplate.convertAndSendToUser(String.valueOf(accountId), "/notify/send-back-user", notificationResponseModel);
//    }

    @Override
    public Page<Notification> findAllByAccountReceived(Account account, Pageable pageable) {
        return notificationRepository.findAllByReceiver(account, pageable);
    }

    @Override
    public Notification getNewNotification(Account account) {
        // Sử dụng NotificationRepository để lấy thông báo chưa đọc mới nhất
        List<Notification> notifications = notificationRepository.findAllByReceiverAndStatusOrderBySentTimeDesc(account, 0);
        if (notifications.isEmpty()) {
            return null;
        }

        // Trả về thông báo chưa đọc mới nhất
        return notifications.get(0);
    }

    @Override
    public Notification getNotificationById(long notificationId) {
        return notificationRepository.findById(notificationId).orElse(null);
    }

    @Override
    public void markAllAsReadForAccount(Account account) {
        List<Notification> notifications = notificationRepository.findAllByReceiverAndStatus(account, 0); // 0: chưa đọc
        for (Notification notification : notifications) {
            notification.setStatus(1); // 1: đã đọc
            notificationRepository.save(notification);
        }
    }

    @Override
    public Page<Notification> findAllByAdminReceivers(Pageable pageable) {
        return notificationRepository.findAllByAdminReceivers(pageable);
    }

    @Override
    public void markAllAsReadForAdmins() {
        List<Notification> notifications = notificationRepository.findAllUnreadByAdminReceivers();
        for (Notification notification : notifications) {
            notification.setStatus(1);
            notificationRepository.save(notification);
        }
    }

}
