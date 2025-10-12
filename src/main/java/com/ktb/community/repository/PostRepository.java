package com.ktb.community.repository;

import com.ktb.community.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    // deletedAt이 null인 게시글만 조회 (삭제되지 않은 게시글)
    List<Post> findByDeletedAtIsNullOrderByCreatedAtDesc(Pageable pageable);

    List<Post> findByIdLessThanAndDeletedAtIsNullOrderByCreatedAtDesc(Long cursor, Pageable pageable);
}
