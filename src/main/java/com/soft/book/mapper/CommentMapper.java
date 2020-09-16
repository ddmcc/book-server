package com.soft.book.mapper;

import com.soft.book.entity.bo.Comment;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface CommentMapper {


    /**
     * 查询此数据评论  倒序
     *
     * @param bookId    书籍id
     * @return          List<Comment>
     */
    List<Comment> listComment(@Param("bookId") Integer bookId);

    /**
     * 根据用户id和书籍id 查询此用户的评论
     *
     * @param bookId    书籍id
     * @param userId    用户id
     * @return          Comment
     */
    Comment getByUserAndBookId(@Param("bookId") Integer bookId, @Param("userId") Integer userId);


    /**
     * 获取这本书的评分比例
     *
     * @param bookId        书籍id
     * @return              Map
     */
    Map<String, BigDecimal> getScores(Integer bookId);


    /**
     * 保存评论
     *
     * @param comment   评论
     * @return          int
     */
    int save(Comment comment);

    /**
     * 查询所有评论内容
     *
     * @param bookId        书籍id
     * @return              String
     */
    String listCommentContent(Integer bookId);

}
