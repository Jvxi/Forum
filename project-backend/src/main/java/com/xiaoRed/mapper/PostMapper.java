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
            " acc.username AS authorName, ui.avatar AS avatar, p.status AS status," +
            " art.tag_name AS tagName, " +
            " art.tag_id AS tagId, " +
            " p.image_urls AS images, p.create_time AS createTime," +
            " p.update_time AS updateTime" +
            " FROM db_post p" +
            " LEFT JOIN db_account acc ON p.user_id = acc.id" +
            " LEFT JOIN db_account_info ui ON ui.uid = acc.id" +
            " LEFT JOIN db_post_article art ON p.tag_id = art.tag_id " +
            " ORDER BY p.create_time DESC")
    List<PostVO> selectPostList();

//    通过帖子id查询帖子详情
    @Select("SELECT " +
            " p.id, p.title, p.content, p.user_id AS userId," +
            " acc.username AS authorName, ui.avatar AS avatar, p.status AS status," +
            " art.tag_name AS tagName, " +
            " p.image_urls AS images, p.create_time AS createTime," +
            " p.update_time AS updateTime " +
            " FROM db_post p " +
            " LEFT JOIN db_account acc ON p.user_id = acc.id " +
            " LEFT JOIN db_account_info ui ON ui.uid = acc.id " +
            " LEFT JOIN db_post_article art ON p.tag_id = art.tag_id " +
            " WHERE p.id = #{postId}")
    PostVO selectPostById(Integer postId);

//    创建新帖子
    @Insert("INSERT INTO db_post (title, content, user_id, tag_id, image_urls, create_time, update_time) " +
            "VALUES (#{title}, #{content}, #{userId}, #{tagId}, #{images}, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)")
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

//  新增删除方法
    @Delete("DELETE FROM db_post WHERE id = #{postId} AND user_id = #{userId}")
    int deletePost(@Param("postId") int postId, @Param("userId") int userId);

//    查询所有标签
    @Select("SELECT p.tag_id AS tagId, p.tag_name AS tagName FROM db_post_article p ORDER BY tag_id")
    List<PostVO> selectAllTags();

//    管理帖子
    @Select("SELECT p.id, p.title, p.content, p.user_id AS userId," +
            " u.username AS authorName, + p.status AS status," +
            " p.create_time AS createTime," +
            " p.update_time AS updateTime " +
            " FROM db_post p " +
            " LEFT JOIN db_account_user u ON p.user_id = u.id ")
    List<Post> selectAllPosts();
    
//    更新帖子状态
    @Update("UPDATE db_post SET status = #{status} WHERE id = #{postId}")
    int updatePostStatus(@Param("postId") int postId, @Param("status") String status);

//    删除帖子
    @Delete("DELETE FROM db_post WHERE id = #{postId}")
    int deletePostByAdmin(int postId);
}

