package com.example.my_project.repository;

import com.example.my_project.models.UserFoodRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserFoodRatingRepository extends JpaRepository<UserFoodRating, Integer> {
    List<UserFoodRating> findByUserId(Integer userId);
    List<UserFoodRating> findByFoodId(Integer foodId);
    List<UserFoodRating> findByUserIdAndLiked(Integer userId, Boolean liked);
}