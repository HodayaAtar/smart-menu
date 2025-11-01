package com.example.my_project.dal;

import com.example.my_project.models.UserDetail;
import com.example.my_project.repository.UserDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class UserDetailsDal {

    private final UserDetailRepository userDetailRepository;

    @Autowired
    public UserDetailsDal(UserDetailRepository userDetailRepository) {
        this.userDetailRepository = userDetailRepository;
    }

    public List<UserDetail> getAllDetails() {
        return userDetailRepository.findAll();
    }

    public List<UserDetail> getDetailsForUser(long userId) {
        return userDetailRepository.findByUserId(userId);
    }

    public boolean updateDetail(long userId, String key, double value) {
        Optional<UserDetail> existing = userDetailRepository.findByUserIdAndDetailKey(userId, key);

        if (existing.isPresent()) {
            UserDetail detail = existing.get();
            detail.setDetailValue(value);
            userDetailRepository.save(detail);
        } else {
            UserDetail newDetail = new UserDetail();
            newDetail.setUserId(userId);
            newDetail.setDetailKey(key);
            newDetail.setDetailValue(value);
            userDetailRepository.save(newDetail);
        }
        return true;
    }

    public void saveDetail(UserDetail detail) {
        userDetailRepository.save(detail);
    }

    public void deleteDetailsForUser(long userId) {
        userDetailRepository.deleteByUserId(userId);
    }

    public void deleteDetailByUserIdAndKey(long userId, String key) {
        userDetailRepository.deleteByUserIdAndDetailKey(userId, key);
    }
}
