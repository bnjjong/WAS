package util;

import com.google.common.collect.Maps;

import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;


/**
 * Created by kanghonggu on 2016-12-16.
 */
public class HttpHeaderUtilTest {
    BufferedReader br;

    @Before
    public void setUp () throws IOException {
        br = new BufferedReader(new FileReader("./header.txt"));
    }

    @Test
    public void setHeader() throws IOException {
        Map<String, String> testHeader = new HashMap<>();

        testHeader.put("Host", "localhost:8080");
        testHeader.put("Connection", "keep-alive");
        testHeader.put("Cache-Control", "max-age=0");
        testHeader.put("Upgrade-Insecure-Requests", "1");
        testHeader.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36");
        testHeader.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        testHeader.put("Accept-Encoding", "gzip, deflate, sdch, br");
        testHeader.put("Accept-Language", "ko-KR,ko;q=0.8,en-US;q=0.6,en;q=0.4");
        testHeader.put("Cookie", "_ga=GA1.1.378865925.1480564448; _td=b959f524-9ba8-4fe1-82cc-d14b2831d96a");


        HttpHeaderUtil.setHeader(br);

        assertTrue(Maps.difference(HttpHeaderUtil.HEADER_DATA, testHeader).areEqual());
    }
}
