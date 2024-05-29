package com.nhom14.webbookstore.model.response_model;

import com.nhom14.webbookstore.model.lean_model.AccountLeanModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationResponseModel {
    @Serial
    private static final long serialVersionUID = 1L;

    private long id;

    private String content;

    private int status;

    private int type;

    private int referredId;

    private AccountLeanModel receiver;

    private AccountLeanModel triggerUser;

    private Timestamp sentTime;
}
