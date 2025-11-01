package com.example.my_project.Service;

import org.springframework.stereotype.Service;

import java.util.Optional;

import com.example.my_project.models.UserDetail;
import com.example.my_project.repository.UserDetailRepository;

@Service
public class UserDetailService {

    private final UserDetailRepository userDetailRepository;

    public UserDetailService(UserDetailRepository userDetailRepository) {
        this.userDetailRepository = userDetailRepository;
    }

    public void saveOrUpdateDetail(long userId, String key, double value) {
        Optional<UserDetail> existingDetail = userDetailRepository.findByUserIdAndDetailKey(userId, key);

        if (existingDetail.isPresent()) {
            UserDetail detail = existingDetail.get();
            detail.setDetailValue(value);
            userDetailRepository.save(detail);
        } else {
            UserDetail newDetail = new UserDetail();
            newDetail.setUserId(userId);
            newDetail.setDetailKey(key);
            newDetail.setDetailValue(value);
            userDetailRepository.save(newDetail);
        }
    }
}
