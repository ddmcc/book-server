package com.soft.book.entity;

import lombok.Data;
import java.io.Serializable;

/**
 * @author ddmcc
 */
@Data
public class Result<T> implements Serializable {

    public static final String DO_SUCCESS = "操作成功";

    public static final String DO_FAIL = "操作失败";

    public static final String CREATE_FAIL = "保存失败";

    public static final String CREATE_SUCCESS = "保存成功";

    public static final String QUERY_SUCCESS = "获取成功";

    public static final String QUERY_FAIL = "获取失败";

    public static final int SUCCESS = 200;

    public static final int FAIL = -1;

    private T data;

    private int status;

    private String msg;

    private Result(T data, int status) {
        this.data = data;
        this.status = status;
    }

    private Result(int status, String msg, T data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    public interface ResultBuilder {

        <T> Result<T> data(T data);

        <T> Result<T> msg(String msg);

        <T> Result<T> build(String msg, T data);
    }

    public static class DefaultBuilder implements ResultBuilder{

        private int status;

        private String msg;

        private DefaultBuilder(int status) {
            this.status = status;
        }

        @Override
        public <T> Result<T> data(T data) {
            return new Result<>(this.status, this.msg, data);
        }

        @Override
        public <T> Result<T> msg(String msg) {
            return new Result<>(this.status, msg, null);
        }

        @Override
        public <T> Result<T> build(String msg, T data) {
            return new Result<>(this.status, msg, data);
        }
    }


    public static Result.ResultBuilder status(int status) {
        return new Result.DefaultBuilder(status);
    }


    public static Result.ResultBuilder ok() {
        return status(SUCCESS);
    }

    public static Result.ResultBuilder fail() {
        return status(FAIL);
    }

    public static <T> Result<T> ok(T data) {
        Result.ResultBuilder builder = ok();
        return builder.data(data);
    }

    public static <T> Result<T> ok(String msg) {
        Result.ResultBuilder builder = ok();
        return builder.msg(msg);
    }

    public static <T> Result<T> fail(String msg) {
        Result.ResultBuilder builder = fail();
        return builder.msg(msg);
    }

    public static <T> Result<T> fail(T data) {
        Result.ResultBuilder builder = fail();
        return builder.data(data);
    }

    public static <T> Result<T> ok(String msg, T data) {
        Result.ResultBuilder builder = ok();
        return builder.build(msg, data);
    }

    public static <T> Result<T> fail(String msg, T data) {
        Result.ResultBuilder builder = fail();
        return builder.build(msg, data);
    }

    public static <T> Result<T> doSuccess() {
        Result.ResultBuilder builder = ok();
        return builder.msg(DO_SUCCESS);
    }

    public static <T> Result<T> doFail() {
        Result.ResultBuilder builder = fail();
        return builder.msg(DO_FAIL);
    }
}
