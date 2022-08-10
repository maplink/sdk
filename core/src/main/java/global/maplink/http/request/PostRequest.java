package global.maplink.http.request;

import lombok.Getter;

import java.net.URL;

public class PostRequest extends Request {
    @Getter
    private final byte[] body;

    public PostRequest(URL url, byte[] body) {
        super(url);
        this.body = body;
    }

    @Override
    public PostRequest withQuery(String key, String value) {
        return (PostRequest) super.withQuery(key, value);
    }

    @Override
    public PostRequest withHeader(String key, String value) {
        return (PostRequest) super.withHeader(key, value);
    }


}