package com.soft.book.service.impl;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Sets;
import com.qianxinyao.analysis.jieba.keyword.Keyword;
import com.qianxinyao.analysis.jieba.keyword.TFIDFAnalyzer;
import com.soft.book.entity.Detail;
import com.soft.book.entity.bo.Book;
import com.soft.book.mapper.BookMapper;
import com.soft.book.service.BookService;
import com.soft.book.service.CommentService;
import com.soft.book.utils.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * 书本服务实现类
 */
@Service
public class BookServiceImpl implements BookService {


    @Autowired
    private BookMapper bookMapper;

    @Autowired
    private CommentService commentService;

    @Autowired
    private RedisUtil<String, String> redisUtil;

    /**
     * 查询书本列表
     *
     * @param page  页数
     * @param size  大小
     * @param param 参数
     * @param type  类型
     * @return List<Book>
     */
    @Override
    public PageInfo<Book> listBook(Integer page, Integer size, String param, String type) {
        // 开始分页，会对下一次列表查询进行分页
        PageHelper.startPage(page, size);
        switch (type) {
            case "pop":
                return new PageInfo<>(bookMapper.listBookByPop(param));
            case "score":
                return new PageInfo<>(bookMapper.listBookByScore(param));
            default:
                return new PageInfo<>(bookMapper.listBookByAll(param));
        }
    }


    /**
     * book详情
     *
     * @param bookId id
     * @return Detail
     */
    @Override
    public Detail getBook(Integer bookId) {
        Detail detail = bookMapper.getBook(bookId);
        if (detail == null) {
            return null;
        }

        // 获取各星评价占比
        detail.setScores(commentService.getScores(bookId));
        return detail;
    }

    /**
     * 获取标签
     *
     * @param bookId 书籍id
     * @return 列表
     */
    @Override
    public Set<String> listTags(Integer bookId) {
        // 先redis取,用set可以防止重复的分词
        Set<String> tags = redisUtil.getSet(String.valueOf(bookId));
        if (tags != null && !tags.isEmpty()) {
            return tags;
        }

        // 将所有评论拼成一个字符串返回
        String commentContent = commentService.listCommentContent(bookId);
        // 为空 就返回空列表
        if (StringUtils.isBlank(commentContent)) {
            return Sets.newHashSet();
        }

        // TFIDF算法提取关键词，分词 tonN 表示 返回前5个分词
        TFIDFAnalyzer tfidfAnalyzer = new TFIDFAnalyzer();
        List<Keyword> list = tfidfAnalyzer.analyze(commentContent, 7);
        // 存到redis
        redisUtil.setSet(String.valueOf(bookId), handle(list));
        return redisUtil.getSet(String.valueOf(bookId));
    }


    /**
     * 过滤一些评论， 权重大于一定大小，字数大于2，并且不是全是标点符号和数字
     * @param keywords  分词列表
     * @return          过滤后的列表
     */
    private List<Keyword> handle(List<Keyword> keywords) {
        return keywords.stream().filter(e -> StringUtils.isNotBlank(e.getName().replaceAll("\\d", ""))
                        && e.getName().length() >= 2
                        && StringUtils.isNotBlank(e.getName().replaceAll("\\p{P}", "")))
                .collect(Collectors.toList());
    }
}
