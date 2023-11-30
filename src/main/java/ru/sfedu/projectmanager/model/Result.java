package ru.sfedu.projectmanager.model;

import ru.sfedu.projectmanager.utils.ResultCode;

import java.util.Objects;

public class Result<T> {
    private ResultCode code;
    private T data;
    private String message;

    public Result(T data, ResultCode code, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public Result(T data, ResultCode code) {
        this.data = data;
        this.code = code;
    }

    public Result(ResultCode code, String message) {
        this.code = code;
        this.message = message;
    }

    public Result(ResultCode code) {
        this.code = code;
    }

    public ResultCode getCode() {
        return code;
    }

    public void setCode(ResultCode code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return String.format("""
            data: %s,
            code: %s,
            message: %s
        """, data != null ? data.toString() : null, code.toString(), message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message, code, data);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Result<?> result = (Result<?>) object;
        return code == result.code && Objects.equals(data, result.data) && Objects.equals(message, result.message);
    }
}
