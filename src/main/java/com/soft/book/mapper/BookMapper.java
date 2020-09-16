package com.soft.book.mapper;

import com.soft.book.entity.Detail;
import com.soft.book.entity.bo.Book;

import java.util.List;

public interface BookMapper {


    /**
     * 根据人气排序
     *
     * @param param     搜索框参数
     * @return          List<Book>
     */
    List<Book> listBookByPop(String param);

    /**
     * 根据人气评分
     *
     * @param param     搜索框参数
     * @return          List<Book>
     */
    List<Book> listBookByScore(String param);

    /**
     * 综合排序
     *
     * @param param     搜索框参数
     * @return          List<Book>
     */
    List<Book> listBookByAll(String param);

    /**
     * 获取书本详情
     *
     * @param id    id
     * @return      Detail
     */
    Detail getBook(Integer id);
}
