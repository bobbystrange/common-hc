package org.dreamcat.common.hc.okhttp;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.FileOutputStream;
import java.io.InputStream;

@Slf4j
public class DownloadUtil {

    private static final String USER_AGENT
            = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.1"
            + " (KHTML, like Gecko) Chrome/21.0.1180.79 Safari/537.1";

    private static final String ACCEPT =
            "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8";

    private static final OkHttpClient client = new OkHttpClient();

    private static boolean download(String url, String path) {
        try {
            Request request = new Request.Builder()
                    .addHeader("User-Agent", USER_AGENT)
                    .addHeader("Accept", ACCEPT)
                    .addHeader("Accept-Encoding", "*")
                    .url(url)
                    .build();


            Response response = client.newCall(request).execute();
            ResponseBody body = response.body();
            if (body == null) return false;

            try (InputStream istream = body.byteStream();
                 FileOutputStream ostream = new FileOutputStream(path)) {

                byte[] buffer = new byte[1024];
                int temp;
                while ((temp = istream.read(buffer)) != -1) {
                    ostream.write(buffer, 0, temp);
                    ostream.flush();
                }
            }

            return true;
        } catch (Throwable t) {
            log.info(t.getMessage(), t);
            return false;
        }
    }


}
