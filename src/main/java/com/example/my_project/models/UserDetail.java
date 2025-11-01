package com.example.my_project.models;

import jakarta.persistence.*;

@Entity
@Table(name = "user_details")
public class UserDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long detailId;

    @Column(name = "user_id")
    private long userId;

    private String detailKey;

    private double detailValue;

    // קשר אופציונלי לטבלת users:
    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    // קונסטרקטור עם פרמטרים
    public UserDetail(long detailId, long userId, String detailKey, double detailValue) {
        this.detailId = detailId;
        this.userId = userId;
        this.detailKey = detailKey;
        this.detailValue = detailValue;
    }

    // קונסטרקטור ריק נדרש ל־JPA
    public UserDetail() {
    }

    public long getDetailId() {
        return detailId;
    }

    public void setDetailId(long detailId) {
        this.detailId = detailId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getDetailKey() {
        return detailKey;
    }

    public void setDetailKey(String detailKey) {
        this.detailKey = detailKey;
    }

    public double getDetailValue() {
        return detailValue;
    }

    public void setDetailValue(double detailValue) {
        this.detailValue = detailValue;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


    @Override
    public String toString() {
        return "UserDetail{" +
                "detailId=" + detailId +
                ", userId=" + userId +
                ", detailKey='" + detailKey + '\'' +
                ", detailValue=" + detailValue +
                '}';
    }
}