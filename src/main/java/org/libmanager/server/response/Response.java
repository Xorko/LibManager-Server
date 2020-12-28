package org.libmanager.server.response;

public class Response<T> {

    public enum Code {
        OK,
        ERROR,
        INVALID_TOKEN,
        INSUFFICIENT_PERMISSIONS,
        NOT_FOUND,
        NOT_AVAILABLE,
        INVALID_TOTAL_COPIES,
        MAX_ITEMS_REACHED,
        MAX_USERS_REACHED,
        MAX_RESERVATIONS_REACHED
    }

    private Code code;
    private T content;

    public Response(Code code, T content) {
        this.code = code;
        this.content = content;
    }

    public Code getCode() {
        return code;
    }

    public void setCode(Code code) {
        this.code = code;
    }

    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
    }

}