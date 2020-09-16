package com.soft.book.service;

import com.github.pagehelper.PageInfo;
import com.soft.book.entity.Detail;
import com.soft.book.entity.bo.Book;
import java.util.Set;


public interface BookService {


    /**
     * 查询书本列表
     *
     * @param page      页数
     * @param size      大小
     * @param param     参数
     * @param type      类型
     * @return          List<Book>
     */
    PageInfo<Book> listBook(Integer page, Integer size, String param, String type);


    /**
     * book详情
     *
     * @param bookId    id
     * @return          Detail
     */
    Detail getBook(Integer bookId);


    /**
     * 获取标签
     *
     * @param bookId        书籍id
     * @return              列表
     */
    Set<String> listTags(Integer bookId);

}
