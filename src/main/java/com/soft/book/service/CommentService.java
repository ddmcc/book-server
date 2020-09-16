package com.soft.book.service;

import com.soft.book.entity.bo.Comment;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface CommentService {


    /**
     * 获取评论列表
     *
     * @param bookId    书籍id
     * @param page      页数
     * @param size      条数
     * @return          列表
     */
    List<Comment> listComment(Integer bookId, Integer page, Integer size);

    /**
     * 获取用户这本书的评论
     *
     * @param bookId        书籍id
     * @param userId        用户id
     * @return              评论
     */
    Comment getByBookIdAndUserId(Integer bookId, Integer userId);

    /**
     * 获取这本书的评分比例
     *
     * @param bookId        书籍id
     * @return              Map
     */
    Map<String, BigDecimal> getScores(Integer bookId);


    /**
     * 评论书籍
     *
     * @param comment   评论
     * @return          true/false
     */
    boolean summitComment(Comment comment);



    /**
     * 查询所有评论内容
     *
     * @param bookId        书籍id
     * @return              String
     */
    String listCommentContent(Integer bookId);

}
