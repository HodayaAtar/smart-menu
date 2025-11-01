package com.example.my_project.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.example.my_project.models.Food;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SmartMenuService {

    @Autowired
    private JdbcTemplate jdbcTemplate;//מחלקה שמפשטת שאילתות SQL למסד הנתונים

    public Map<Long, Map<Long, Integer>> buildRatingMatrix() {
        String sql = "SELECT user_id, food_id, liked FROM user_food_rating";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);

        Map<Long, Map<Long, Integer>> userFoodMatrix = new HashMap<>();

        for (Map<String, Object> row : rows) {
            Long userId = ((Number) row.get("user_id")).longValue();
            Long foodId = ((Number) row.get("food_id")).longValue();
            Integer liked = ((Number) row.get("liked")).intValue();

            userFoodMatrix//מוסיפים למשתמש את הדירוג של המאכל למאפ 
                .computeIfAbsent(userId, k -> new HashMap<>())
                .put(foodId, liked);
        }

        return userFoodMatrix;
    }
    public void printMatrix(Map<Long, Map<Long, Integer>> matrix) {
    for (Map.Entry<Long, Map<Long, Integer>> entry : matrix.entrySet()) {
   System.out.println(" משתמש " + entry.getKey() + ": " + entry.getValue());    }
}

//דמיון קוסינוסים
public double cosineSimilarity(double[] vectorA, double[] vectorB) {
    double dotProduct = 0.0;
    double normA = 0.0;
    double normB = 0.0;

    for (int i = 0; i < vectorA.length; i++) {
        dotProduct += vectorA[i] * vectorB[i];//מכפלה סקלרית
        normA += Math.pow(vectorA[i], 2);
        normB += Math.pow(vectorB[i], 2);
    }

    if (normA == 0 || normB == 0) return 0.0; // למנוע חלוקה באפס

    return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
}

public List<Food> recommendFoodsForUser(Long userId, List<Food> candidateFoods) {//מקבלת את המאכלים האפשריים להמלצה
    Map<Long, Map<Long, Integer>> ratingMatrix = buildRatingMatrix();//בונה תמט'

    //חישוב דמיון לפי דמיון קוסינוסים
    List<Long> similarUsers = findTopKSimilarUsers(userId, ratingMatrix, 5);

    //הסתברות שהמשתמש יאהב 
    Map<Long, Double> predictedScores = predictRatings(userId, similarUsers, ratingMatrix);

    // 4. החזרת רשימת מאכלים ממוינת בציונים מהמאכלים האפשריים בלבד
    return candidateFoods.stream()
            .filter(f -> predictedScores.containsKey(f.getId().longValue()))
            .sorted((f1, f2) -> Double.compare(
                predictedScores.getOrDefault(f2.getId(), 0.0),
                predictedScores.getOrDefault(f1.getId(), 0.0)
            ))
            .collect(Collectors.toList());
}

//  חיפוש המשתמשים הדומים ביותר לפי cosine similarity
public List<Long> findTopKSimilarUsers(Long targetUserId, Map<Long, Map<Long, Integer>> ratingMatrix, int k) {
    Map<Long, Integer> targetRatings = ratingMatrix.get(targetUserId);
    if (targetRatings == null) return List.of();

    List<Long> similarUsers = new ArrayList<>();

    for (Map.Entry<Long, Map<Long, Integer>> entry : ratingMatrix.entrySet()) {
        Long otherUserId = entry.getKey();
        if (otherUserId.equals(targetUserId)) continue;//לא משווים את המשתמש לעצמו

        Map<Long, Integer> otherRatings = entry.getValue();
        Set<Long> allFoodIds = new HashSet<>();
        allFoodIds.addAll(targetRatings.keySet());
        allFoodIds.addAll(otherRatings.keySet());

        double[] targetVector = new double[allFoodIds.size()];
        double[] otherVector = new double[allFoodIds.size()];

        int index = 0;
        for (Long foodId : allFoodIds) {
            targetVector[index] = targetRatings.getOrDefault(foodId, 0);
            otherVector[index] = otherRatings.getOrDefault(foodId, 0);
            index++;
        }

        double similarity = cosineSimilarity(targetVector, otherVector);
        if (similarity > 0) {
            similarUsers.add(otherUserId);
        }
    }
    return similarUsers.stream()
            .limit(k)
            .collect(Collectors.toList());
}

//  חיזוי דירוגים על בסיס ממוצע משוקלל של השכנים
public Map<Long, Double> predictRatings(Long userId, List<Long> similarUsers, Map<Long, Map<Long, Integer>> ratingMatrix) {
    Map<Long, Double> predictedScores = new HashMap<>();
    Map<Long, Integer> targetUserRatings = ratingMatrix.getOrDefault(userId, new HashMap<>());

    for (Long otherUserId : similarUsers) {
        Map<Long, Integer> otherRatings = ratingMatrix.getOrDefault(otherUserId, Map.of());

        for (Map.Entry<Long, Integer> entry : otherRatings.entrySet()) {
            Long foodId = entry.getKey();
            Integer score = entry.getValue();

            // לא לנבא מאכלים שכבר דורגו ע"י המשתמש
            if (targetUserRatings.containsKey(foodId)) continue;

            predictedScores.put(foodId, predictedScores.getOrDefault(foodId, 0.0) + score);
        }
    }

    return predictedScores;
}


}
