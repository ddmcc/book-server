package com.soft.book;


import com.soft.book.service.CommentService;

import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import lombok.Data;


@SpringBootTest
class BookServerApplicationTests {

    //设置APPID/AK/SK
    public static final String APP_ID = "18633623";
    public static final String API_KEY = "alSfIRR5xlZ3BmeFWFjF44PX";
    public static final String SECRET_KEY = "mXvtYkm4n2T6ZUlX1qMSVI8y7BV7C0ZL";

    @Autowired
    private CommentService commentService;

    @Test
    void contextLoads() throws JSONException {

    }

    @Data
    static class Item {

        String item;

        String pos;

    }

}
