package mc21.tgi.com.googlesociallogindemo.bean;

public class TgiResponse {
    int code;
    String message;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "TgiResponse{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }
}
