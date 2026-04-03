package com.xc.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;

@Data
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 成功标志
     */
    private boolean success;

    /**
     * 返回处理消息
     */
    private String message;

    /**
     * 返回代码
     */
    private Integer code = 0;

    /**
     * 返回数据对象 data
     */
    private T result;

    /**
     * 时间戳
     */
    private long timestamp = System.currentTimeMillis();

    public Result() {

    }

    public static <T> Result<T> ok() {
        Result<T> r = new Result<>();
        r.setSuccess(true);
        r.setCode(200);
        return r;
    }

    public static <T> Result<T> isOk(boolean b) {
        Result<T> r = new Result<>();
        if (b){
            r.setSuccess(true);
            r.setMessage("SUCCESS");
            r.setCode(200);
        }else {
            r.setSuccess(false);
            r.setCode(500);
        }
        return r;
    }

    public static <T> Result<T> ok(T data) {
        Result<T> r = new Result<>();
        r.setSuccess(true);
        r.setMessage("SUCCESS");
        r.setCode(200);
        r.setResult(data);
        return r;
    }

    public static <T> Result<T> ok(String msg, T data) {
        Result<T> r = new Result<>();
        r.setSuccess(true);
        r.setCode(200);
        r.setMessage(msg);
        r.setResult(data);
        return r;
    }

    public static <T> Result<T> error(String msg, T data) {
        Result<T> r = new Result<>();
        r.setSuccess(false);
        r.setCode(500);
        r.setMessage(msg);
        r.setResult(data);
        return r;
    }

    public static <T> Result<T> ok(String msg) {
        return ok(msg, null);
    }

    public static <T> Result<T> error(String msg) {
        return error(500, msg);
    }

    public static <T> Result<T> error(int code, String msg) {
        Result<T> r = new Result<>();
        r.setCode(code);
        r.setMessage(msg);
        r.setSuccess(false);
        return r;
    }

    public Result<T> success(String message) {
        this.message = message;
        this.code = 200;
        this.success = true;
        return this;
    }

    public Result<T> error500(String message) {
        this.message = message;
        this.code = 500;
        this.success = false;
        return this;
    }

    public static Result<Object> noauth(String msg) {
        return error(401, msg);
    }

    public static Result<Object> tokenInvalid(String msg) {
        return error(401, msg);
    }

    @JsonIgnore
    private String onlTable;

    public static <T> Result<T> OK(T data) {
        Result<T> r = new Result<>();
        r.setSuccess(true);
        r.setMessage("SUCCESS");
        r.setCode(200);
        r.setResult(data);
        return r;
    }

}
