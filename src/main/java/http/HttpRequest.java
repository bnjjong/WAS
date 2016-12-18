package http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 강홍구 on 2016-12-18.
 */
public class HttpRequest {

    private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);

    private static BufferedReader bufferedReader;

    private Map<String, String> requestHeader = new HashMap<>();
    private Map<String, String> requestParameter = new HashMap<>();

    private String requestUrl;
    private String requestPath;
    private String requestMethod;

    /**
     * 생성자
     * @param inputStream : 클라이언트의 요청을 담고 있다.
     * 클라이언트의 요청을
     *  1) HTTP request 메서드, 2) HTTP request url, 3) HTTP request 헤더, 4) request Body 로 분리한다.
     */
    public HttpRequest(InputStream inputStream) throws IOException {
        bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String lineOfMethodAndUrl = bufferedReader.readLine();
        log.debug("request line : {}", lineOfMethodAndUrl);
        this.setRequestUrl(lineOfMethodAndUrl);
        this.setRequestMethod(lineOfMethodAndUrl);
        this.setRequestPath();

        this.setRequestHeader();
        this.setRequestParameter();
    }

    private void setRequestUrl(String lineOfMethodAndUrl) {
        this.requestUrl = HttpRequestUtils.getUrl(lineOfMethodAndUrl, " ");
    }

    private void setRequestPath () {
        this.requestPath = this.requestUrl.split("\\?")[0];
    }

    private void setRequestMethod (String lineOfMethodAndUrl) {
        this.requestMethod = HttpRequestUtils.getMethod(lineOfMethodAndUrl, " ");
    }

    private void setRequestHeader () throws IOException {
        String line = null;
        while(!"".equals(line)) {
            line = bufferedReader.readLine();
            HttpRequestUtils.Pair pair = HttpRequestUtils.parseHeader(line);

            if (pair == null) {
                return;
            }

            requestHeader.put(pair.getKey(), pair.getValue());

            log.debug("request line : {}", line);
        }
    }

    private void setRequestParameter () throws IOException {
        if ("GET".equalsIgnoreCase(this.requestMethod)) {
            this.requestParameter = HttpRequestUtils.parseQueryString(this.requestUrl.split("\\?")[1]);
        }

        if ("POST".equalsIgnoreCase(this.requestMethod)) {
            int contentLength = Integer.parseInt(this.requestHeader.get("Content-Length"));

            String queryString = IOUtils.readData(this.bufferedReader, contentLength);
            this.requestParameter = HttpRequestUtils.parseQueryString(queryString);
        }
    }

    public String getRequestMethod () {
        return this.requestMethod;
    }

    public String getRequestUrl () {
        return this.requestUrl;
    }

    public String getRequestPath () {
        return this.requestPath;
    }

    public String getRequestHeader (String key) {
        return this.requestHeader.get(key);
    }

    public String getRequestParam (String key) {
        return this.requestParameter.get(key);
    }

}
