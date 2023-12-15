package ru.sfedu.projectmanagement.core.utils.types;

import ru.sfedu.projectmanagement.core.utils.ResultCode;

import java.util.Objects;
import java.util.TreeMap;

class NoData {}

public class Result<T> {
    private ResultCode code;
    private T data;
    private String message;
    private TreeMap<String, String> errors = new TreeMap<>();

    public Result(T data, ResultCode code, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public Result(T data, ResultCode code, TreeMap<String, String> errors) {
        this.data = data;
        this.code = code;
        this.errors = errors;
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

    public Result() {

    }

    public void setErrors(TreeMap<String, String> errors) {
        this.errors = errors;
    }

    public TreeMap<String, String> getErrors() {
        return errors;
    }

    public void addError(TreeMap<String, String> errors) {
        this.errors.putAll(errors);
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
        return "Result{" +
                "code=" + code +
                ", data=" + data +
                ", message='" + message + '\'' +
                ", errors=" + errors +
                '}';
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
