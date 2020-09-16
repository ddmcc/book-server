package com.soft.book.api;

import com.google.common.collect.Lists;
import com.soft.book.entity.Result;
import com.soft.book.entity.bo.Comment;
import com.soft.book.service.CommentService;
import com.soft.book.utils.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
public class CommentController {

    @Autowired
    private RedisUtil<String, String> redisUtil;

    @Autowired
    private CommentService commentService;

    @GetMapping("/comments")
    public ResponseEntity listComment(@RequestParam("bookId") Integer bookId,
                                      @RequestParam(value = "page", defaultValue = "1") Integer page,
                                      @RequestParam(value = "size", defaultValue = "10") Integer size) {
        if (bookId == null) {
            return ResponseEntity.ok(Result.ok(Lists.newArrayList()));
        }
        return ResponseEntity.ok(Result.ok(commentService.listComment(bookId, page, size))
        );
    }


    @GetMapping("/comments/{id}")
    public ResponseEntity getComment(@PathVariable("id") Integer bookId, HttpServletRequest request) {
        String token = request.getHeader("token");
        String userId = "";
        if (StringUtils.isNotBlank(token)) {
            userId = redisUtil.get(token);
        }

        if (StringUtils.isBlank(userId)) {
            return ResponseEntity.ok(Result.ok(""));
        }

        return ResponseEntity.ok(Result.ok(commentService.getByBookIdAndUserId(bookId, Integer.valueOf(userId))));
    }

    @PostMapping("/comment")
    public ResponseEntity summitComment(@RequestBody Comment comment, HttpServletRequest request) {
        String token = request.getHeader("token");
        String userId = "";
        if (StringUtils.isNotBlank(token)) {
            userId = redisUtil.get(token);
        }

        if (StringUtils.isBlank(userId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Result.fail("您还未登录或登录已过期，请重新登录！"));
        }
        comment.setUserId(Integer.valueOf(userId));
        if (commentService.summitComment(comment)) {
            redisUtil.removeSet(String.valueOf(comment.getBookId()));
            return ResponseEntity.ok(Result.ok("评论成功！"));
        }

        return ResponseEntity.badRequest().body(Result.fail("评论失败，请刷新重试！"));
    }

}
