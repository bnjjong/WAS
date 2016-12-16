package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kanghonggu on 2016-12-16.
 */
public class HttpHeaderUtil {

    private static final Logger log = LoggerFactory.getLogger(HttpHeaderUtil.class);

    public static Map<String, String> HEADER_DATA = new HashMap<>();

    public static void setHeader(BufferedReader br) throws IOException {
        String line = null;
        while(!"".equals(line)) {
            line = br.readLine();
            HttpRequestUtils.Pair pair = HttpRequestUtils.parseHeader(line);

            if (pair == null) {
                return;
            }
            HEADER_DATA.put(pair.getKey(), pair.getValue());

            log.debug("request line : {}", line);
        }
    }

}
