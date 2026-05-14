package com.community.platform.domain.gallery.service;

import com.community.platform.domain.post.entity.Like;
import com.community.platform.domain.post.repository.LikeRepository;
import com.community.platform.domain.user.entity.User;
import com.community.platform.domain.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PhotoLikeService {

    private final LikeRepository likeRepository;
    private final UserRepository userRepository;

    @Transactional
    public boolean toggleLike(Long photoId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        return likeRepository
                .findByUserIdAndTargetTypeAndTargetId(
                        userId, "PHOTO", photoId)
                .map(like -> {
                    likeRepository.delete(like);
                    return false;
                })
                .orElseGet(() -> {
                    likeRepository.save(
                            Like.create(user, "PHOTO", photoId));
                    return true;
                });
    }
}