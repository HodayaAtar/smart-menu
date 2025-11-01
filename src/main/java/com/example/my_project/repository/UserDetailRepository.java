package com.example.my_project.repository;

import com.example.my_project.models.UserDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

import java.util.List;

@Repository
public interface UserDetailRepository extends JpaRepository<UserDetail, Integer> {

    // מחפש פרטי משתמש לפי userId ו-key מסוים
    Optional<UserDetail> findByUserIdAndDetailKey(long userId, String detailKey);

    // מוחק פרטי משתמש לפי userId ו-key מסוים
    void deleteByUserIdAndDetailKey(long userId, String detailKey);

     void deleteByUserId(long userId);

    // אופציונלי: מחזיר את כל הפרטים של משתמש מסוים
    List<UserDetail> findByUserId(long userId);
}
