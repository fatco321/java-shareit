package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class CommentMapperTest {
    private Comment comment;
    private CommentDto commentDto;
    private User user;
    private Item item;

    @BeforeEach
    void setup() {
        user = User.builder().id(1L).name("user").email("user@user.ru").build();
        item = Item.builder().id(2L).name("item").description("test").available(true).owner(user).build();
        comment = Comment.builder()
                .text("test")
                .author(user)
                .item(item)
                .created(LocalDate.now())
                .id(3L)
                .build();
        commentDto = CommentDto.builder()
                .authorName(user.getName())
                .id(comment.getId())
                .created(comment.getCreated())
                .text(comment.getText())
                .build();
    }

    @Test
    void test01_toCommentDto() {
        commentDto = CommentMapper.commentDto(comment);
        assertThat(commentDto).isNotNull();
        assertThat(commentDto.getText()).isEqualTo(comment.getText());
        assertThat(commentDto.getAuthorName()).isEqualTo(comment.getAuthor().getName());
    }

    @Test
    void test02_fromCommentDto() {
        comment = CommentMapper.fromCommentDto(commentDto, user, item);
        assertThat(comment).isNotNull();
        assertThat(comment.getText()).isEqualTo(commentDto.getText());
        assertThat(comment.getAuthor().getName()).isEqualTo(commentDto.getAuthorName());
    }
}
