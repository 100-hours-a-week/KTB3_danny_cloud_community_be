package com.ktb.community.service;

import com.ktb.community.dto.request.CreatePostRequestDto;
import com.ktb.community.dto.response.*;
import com.ktb.community.entity.*;
import com.ktb.community.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class PostService {
    private final PostRepository postRepository;
    private final CountRepository countRepository;
    private final ImageRepository imageRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    @Autowired
    public PostService(PostRepository postRepository, CountRepository countRepository, ImageRepository imageRepository, CommentRepository commentRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.countRepository = countRepository;
        this.imageRepository = imageRepository;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public CreatePostResponseDto createPost(CreatePostRequestDto createPostRequestDto, String email) {
        Post post = new Post();
        post.setTitle(createPostRequestDto.getTitle());
        post.setContent(createPostRequestDto.getContent());
        User user = this.userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Users not found"));
        post.setUser(user);

        Post savedPost = this.postRepository.save(post);

        if (!createPostRequestDto.getImages().isEmpty()) {
            List<String> imageUrls = createPostRequestDto.getImages();
            List<Image> images = new ArrayList<>();
            for (int i = 0; i < createPostRequestDto.getImages().size(); i++) {
                Image img = new Image();
                img.setUrl(imageUrls.get(i));
                img.setPost(savedPost);
                img.setDisplayOrder(i);
                images.add(img);
            }
            this.imageRepository.saveAll(images);
        }

        return new CreatePostResponseDto(savedPost.getId());
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
                    Count count = this.countRepository.findByPostId(post.getId()).orElse(null);

                    return PostResponseDto.builder()
                            .id(post.getId())
                            .title(post.getTitle())
                            .content(post.getContent())
                            .author(post.getUser().getNickname())
                            .createAt(post.getCreatedAt())
                            .views(count != null ? count.getViewCount() : 0L)
                            .likes(count != null ? count.getLikeCount() : 0L)
                            .comments(count != null ? count.getCommentCount() : 0L)
                            .build();
                }).collect(Collectors.toList());
        Long nextCursor = !postContent.isEmpty() ? postContent.getLast().getId() : null;

        return new CursorPageResponseDto<>(postContent, nextCursor, hasNext);
    }

    public PostDetailResponseDto getPostContent(Long postId) {
        Post post = this.postRepository.findById(postId).orElse(null);

        if (post == null) {
            throw new IllegalArgumentException("The post does not exist.");
        }

        List<String> images = this.imageRepository.findByPostIdAndDeletedAtIsNullOrderByDisplayOrderAsc(post.getId())
                .stream()
                .map(Image::getUrl)
                .toList();

        Count count = this.countRepository.findByPostId(post.getId()).orElse(null);

        return PostDetailResponseDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .author(post.getUser().getNickname())
                .images(images)
                .views(count != null ? count.getViewCount() : 0L)
                .likes(count != null ? count.getLikeCount() : 0L)
                .comments(count != null ? count.getCommentCount() : 0L)
                .build();
    }


    public CursorCommentResponseDto<CommentResponseDto> getCommentList(Long postId, Long cursor, int size) {
        List<Comment> comments;
        Pageable pageable = PageRequest.of(0, size + 1);
        if (cursor == null) {
            comments = this.commentRepository.findByPostIdAndDeletedAtIsNullOrderByCreatedAtDesc(postId, pageable);
        } else {
            comments = this.commentRepository.findByPostIdAndIdLessThanAndDeletedAtIsNullOrderByCreatedAtDesc(postId, cursor, pageable);
        }

        boolean hasNext = comments.size() > size;
        if (hasNext) {
            comments = comments.subList(0, size);
        }

        List<CommentResponseDto> commentList = comments.stream()
                .map(comment -> CommentResponseDto.builder()
                        .id(comment.getId())
                        .author(comment.getUser().getNickname())
                        .content(comment.getContent())
                        .createdAt(comment.getCreatedAt())
                        .isMine(false) // 인증이 추가되면 로직 변경하기
                        .build())
                .toList();

        Long nextCursor = !commentList.isEmpty() ? commentList.getLast().getId() : null;
        return new CursorCommentResponseDto<>(commentList, nextCursor, hasNext);
    }

}

