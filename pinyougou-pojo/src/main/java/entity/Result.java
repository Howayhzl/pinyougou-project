package entity;

import java.io.Serializable;

/**
 * 返回结果封装
 */
public class Result implements Serializable {

    private String message;

    private boolean success;

    public Result(String message, boolean success) {
        super();
        this.message = message;
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
