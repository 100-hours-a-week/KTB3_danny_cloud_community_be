package com.ktb.community.service;

import com.ktb.community.dto.response.CursorPageResponseDto;
import com.ktb.community.dto.response.PostDetailResponseDto;
import com.ktb.community.dto.response.PostResponseDto;
import com.ktb.community.entity.Count;
import com.ktb.community.entity.Image;
import com.ktb.community.entity.Post;
import com.ktb.community.repository.CountRepository;
import com.ktb.community.repository.ImageRepository;
import com.ktb.community.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final CountRepository countRepository;
    private final ImageRepository imageRepository;

    @Autowired
    public PostService(PostRepository postRepository, CountRepository countRepository, ImageRepository imageRepository) {
        this.postRepository = postRepository;
        this.countRepository = countRepository;
        this.imageRepository = imageRepository;
    }

    public CursorPageResponseDto<PostResponseDto> getPostList(Long cursor, int size) {
        Pageable pageable = PageRequest.of(0, size + 1);

        List<Post> posts;
        if (cursor == null) {
            // null이면 첫페이지
            posts = this.postRepository.findByDeletedAtIsNullOrderByCreatedAtDesc(pageable);
        } else {
            // 다음 페이지
            posts = this.postRepository.findByIdLessThanAndDeletedAtIsNullOrderByCreatedAtDesc(cursor, pageable);
        }

        boolean hasNext = posts.size() > size;
        if (hasNext) {
            // 다음 페이지가 존재한다면
            posts = posts.subList(0, size);
        }

        List<PostResponseDto> postContent = posts.stream()
                .map(post -> {
                    PostResponseDto postResponseDto = new PostResponseDto();
                    postResponseDto.setId(post.getId());
                    postResponseDto.setTitle(post.getTitle());
                    postResponseDto.setContent(post.getContent());
                    postResponseDto.setAuthor(post.getUser().getNickname());
                    postResponseDto.setCreateAt(post.getCreatedAt());

                    Count count = this.countRepository.findByPostId(post.getId()).orElse(null);
                    if (count != null) {
                        postResponseDto.setViews(count.getViewCount());
                        postResponseDto.setLikes(count.getLikeCount());
                        postResponseDto.setComments(count.getCommentCount());
                    } else {
                        postResponseDto.setViews(0L);
                        postResponseDto.setLikes(0L);
                        postResponseDto.setComments(0L);
                    }
                    return postResponseDto;
                }).collect(Collectors.toList());
        Long nextCursor = !postContent.isEmpty() ? postContent.getLast().getId() : null;

        return new CursorPageResponseDto<>(postContent, nextCursor, hasNext);
    }

    public PostDetailResponseDto getPostContent(Long postId) {
        Post post = this.postRepository.findById(postId).orElse(null);

        if (post == null) {
            throw new IllegalArgumentException("The post does not exist.");
        }

        PostDetailResponseDto postDetailResponseDto = new PostDetailResponseDto();
        postDetailResponseDto.setId(post.getId());
        postDetailResponseDto.setTitle(post.getTitle());
        postDetailResponseDto.setContent(post.getContent());
        postDetailResponseDto.setAuthor(post.getUser().getNickname());

        List<String> images = this.imageRepository.findByPostIdAndDeletedAtIsNull(post.getId())
                .stream()
                .map(image -> {
                    return image.getUrl();
                }).toList();
        postDetailResponseDto.setImages(images);

        Count count = this.countRepository.findByPostId(post.getId()).orElse(null);
        if (count != null) {
            postDetailResponseDto.setViews(count.getViewCount());
            postDetailResponseDto.setLikes(count.getLikeCount());
            postDetailResponseDto.setComments(count.getCommentCount());
        } else {
            postDetailResponseDto.setViews(0L);
            postDetailResponseDto.setLikes(0L);
            postDetailResponseDto.setComments(0L);
        }

        return postDetailResponseDto;
    }

}

