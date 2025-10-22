package com.ktb.community.repository;

import com.ktb.community.entity.Like;
import com.ktb.community.entity.LikePK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeRepository extends JpaRepository<Like, LikePK> {
    boolean existsByIdAndDeletedAtIsNull(LikePK pk);
}
