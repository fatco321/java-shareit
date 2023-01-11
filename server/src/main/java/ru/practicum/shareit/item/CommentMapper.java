package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

public class CommentMapper {
    public static CommentDto commentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .authorName(comment.getAuthor().getName())
                .text(comment.getText())
                .created(comment.getCreated())
                .build();
    }

    public static Comment fromCommentDto(CommentDto commentDto, User user, Item item) {
        return Comment.builder()
                .id(commentDto.getId())
                .text(commentDto.getText())
                .author(user)
                .item(item)
                .build();
    }
}