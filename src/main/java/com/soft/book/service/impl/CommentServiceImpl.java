package com.soft.book.service.impl;

import com.github.pagehelper.PageHelper;
import com.soft.book.entity.bo.Comment;
import com.soft.book.mapper.CommentMapper;
import com.soft.book.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    /**
     * 获取评论列表
     *
     * @param bookId    书籍id
     * @param page      页数
     * @param size      条数
     * @return          列表
     */
    @Override
    public List<Comment> listComment(Integer bookId, Integer page, Integer size) {
        // 开始分页
        PageHelper.startPage(page, size);
        return commentMapper.listComment(bookId);
    }

    /**
     * 获取用户这本书的评论
     *
     * @param bookId        书籍id
     * @param userId        用户id
     * @return              评论
     */
    @Override
    public Comment getByBookIdAndUserId(Integer bookId, Integer userId) {
        return commentMapper.getByUserAndBookId(bookId, userId);
    }

    /**
     * 获取这本书的评分比例
     *
     * @param bookId        书籍id
     * @return              Map
     */
    @Override
    public Map<String, BigDecimal> getScores(Integer bookId) {
        return commentMapper.getScores(bookId);
    }

    /**
     * 评论书籍
     *
     * @param comment 评论
     * @return true/false
     */
    @Override
    public boolean summitComment(Comment comment) {
        // 设置评论时间
        comment.setCreateTime(new Date());
        // insert方法会返回数据库影响的条数，插入1条即会返回1，大于0插入成功
        return commentMapper.save(comment) > 0;
    }


    /**
     * 查询所有评论内容
     *
     * @param bookId 书籍id
     * @return String
     */
    @Override
    public String listCommentContent(Integer bookId) {
        return commentMapper.listCommentContent(bookId);
    }
}
