package org.bankai.model;

import lombok.Data;

import java.util.Date;

@Data
public class UserActive {
    private Integer userId;
    private Date lastActive;
    private Date expireTime;
}
