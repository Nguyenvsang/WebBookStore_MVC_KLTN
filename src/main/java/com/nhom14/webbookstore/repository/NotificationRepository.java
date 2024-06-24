package com.nhom14.webbookstore.repository;

import com.nhom14.webbookstore.entity.Account;
import com.nhom14.webbookstore.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Page<Notification> findAllByReceiver(Account account, Pageable pageable);

    List<Notification> findAllByReceiverAndStatusOrderBySentTimeDesc(Account account, int i);

    List<Notification> findAllByReceiverAndStatus(Account receiver, int status);

    @Query("SELECT n FROM Notification n WHERE n.receiver.accountType = 0")
    Page<Notification> findAllByAdminReceivers(Pageable pageable);

    // Thêm phương thức để lấy tất cả thông báo của admin chưa đọc
    @Query("SELECT n FROM Notification n WHERE n.receiver.accountType = 0 AND n.status = 0")
    List<Notification> findAllUnreadByAdminReceivers();
}
