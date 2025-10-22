package com.ktb.community.repository;

import com.ktb.community.entity.Comment;
import com.ktb.community.entity.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostIdAndDeletedAtIsNullOrderByCreatedAtDesc(Long post_id, Pageable pageable);

    List<Comment> findByPostIdAndIdLessThanAndDeletedAtIsNullOrderByCreatedAtDesc(
            Long postId, Long cursor, Pageable pageable);

    List<Comment> findByPostId(Long postId);
}
