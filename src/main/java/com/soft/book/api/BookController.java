package com.soft.book.api;

import com.google.common.collect.Lists;
import com.soft.book.entity.Detail;
import com.soft.book.entity.Result;
import com.soft.book.service.BookService;
import com.soft.book.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
public class BookController {

    @Autowired
    private RedisUtil<String, String> redisUtil;

    @Autowired
    private BookService bookService;

    @GetMapping("/books")
    public ResponseEntity books(@RequestParam(defaultValue = "1") Integer page,
                                @RequestParam(defaultValue = "5") Integer size,
                                @RequestParam(defaultValue = "all") String type,
                                String param) {

        return ResponseEntity.ok(Result.ok("", bookService.listBook(page, size, param, type)));
    }


    @GetMapping("/books/{id}")
    public ResponseEntity detail(@PathVariable("id") Integer id) {
        if (id == null || id == 0) {
            return ResponseEntity.badRequest().body(Result.ok("查询的书籍不存在", new Detail()));
        }

        Detail detail = bookService.getBook(id);
        if (detail == null) {
            return ResponseEntity.badRequest().body(Result.ok("查询的书籍不存在", new Detail()));
        }

        return ResponseEntity.ok().body(Result.ok("", detail));
    }


    @GetMapping("/tags")
    public ResponseEntity tags(@RequestParam("bookId") Integer bookId) {
        if (bookId == null || bookId == 0) {
            return ResponseEntity.ok(Result.fail("", Lists.newArrayList()));
        }

        return ResponseEntity.ok(Result.ok(bookService.listTags(bookId)));
    }

}
