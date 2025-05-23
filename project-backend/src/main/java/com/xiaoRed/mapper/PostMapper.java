package com.xiaoRed.mapper;

import com.xiaoRed.entity.postings.Post;
import com.xiaoRed.entity.postings.PostVO;
import org.apache.ibatis.annotations.*;

import java.util.List;


// PostMapper.java
@Mapper
public interface PostMapper {
//    查询帖子列表
    @Select("SELECT p.id, p.title, p.content, p.user_id AS userId," +
            " u.username AS authorName," +
            " a.tag_name AS tagName, " +  // 替换tagid为tagName
            " a.tag_id AS tagId, " +  // 新增字段
            " p.image_urls AS images, p.create_time AS createTime," +
            " p.update_time AS updateTime" +
            " FROM db_post p" +
            " LEFT JOIN db_account_user u ON p.user_id = u.id" +
            " LEFT JOIN db_post_article a ON p.tag_id = a.tag_id " + // 新增关联
            " ORDER BY p.create_time DESC")
    List<PostVO> selectPostList();

//    通过帖子id查询帖子详情
    @Select("SELECT " +
            " p.id, p.title, p.content, p.user_id AS userId," +
            " u.username AS authorName," +
            " a.tag_name AS tagName, " +  // 替换tagid为tagName
            " p.image_urls AS images, p.create_time AS createTime," +
            " p.update_time AS updateTime " +
            " FROM db_post p " +
            " LEFT JOIN db_account_user u ON p.user_id = u.id " +
            " LEFT JOIN db_post_article a ON p.tag_id = a.tag_id " + // 新增关联
            " WHERE p.id = #{postId}")
    PostVO selectPostById(Integer postId);

//    创建新帖子
    @Insert("INSERT INTO db_post (title, content, user_id, tag_id, image_urls, create_time) " +
            "VALUES (#{title}, #{content}, #{userId}, #{tagId}, #{images}, CURRENT_TIMESTAMP)")
    void insertPost(PostVO post);

//    更新编辑帖子
    @Update("UPDATE db_post SET " +
            "title = #{title}, " +
            "content = #{content}, " +
            "tag_id = #{tagId}, " +
            "image_urls = #{images}, " +
            "update_time = CURRENT_TIMESTAMP " +
            "WHERE id = #{id} AND user_id = #{userId}")
    int updatePost(PostVO postVO);

//    查询所有标签
    @Select("SELECT p.tag_id AS tagId, p.tag_name AS tagName FROM db_post_article p ORDER BY tag_id")
    List<PostVO> selectAllTags();
}

