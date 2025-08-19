package com.xiaoRed.mapper;


import com.xiaoRed.entity.postings.Comment;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CommentMapper {
    // 插入评论（返回自增主键）
    @Insert("INSERT INTO db_post_comment(content, post_id, user_id, create_time, update_time) " +
            "VALUES(#{content}, #{postId}, #{userId}, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)")
    void insert(Comment comment);

    @Select("SELECT " +
            "c.id, " +
            "c.post_id AS postId, " +
            "c.user_id AS userId, " +
            "c.content, " +
            "u.username AS authorName, " +
            "ui.avatar AS avatar, " +
            "c.create_time AS createTime, " +
            "c.update_time AS updateTime " +
            "FROM db_post_comment c " +
            "LEFT JOIN db_account u ON c.user_id = u.id " +
            "LEFT JOIN db_account_info ui ON ui.uid = u.id " +
            "WHERE c.post_id = #{postId} " +
            "ORDER BY c.create_time DESC")
    List<Comment> selectByPostId(Integer postId);

    @Select("SELECT * FROM db_post_comment WHERE id = #{id}")
    Comment selectById(Integer id);

    // 删除评论
    @Delete("DELETE FROM db_post_comment WHERE id = #{commentId} AND user_id = #{userId}")
    int delete(@Param("commentId") Integer commentId, @Param("userId") Integer userId);
}
