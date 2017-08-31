package cayley.util;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

import cayley.exception.CayleyException;

/**
 * @author wangoo
 * @since 2017-08-30 16:34
 */
public class CayleyHttpUtil {

    public static String send(String url, String command) throws CayleyException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            HttpPost post = new HttpPost(url);
            StringEntity stringEntity = new StringEntity(command);
            post.setEntity(stringEntity);
            post.addHeader("Content-Type","application/json;charset=utf-8");

            CloseableHttpResponse response = httpclient.execute(post);
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity);

            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                throw new CayleyException("unexpected http status " + statusCode + "," + result);
            } else {
                return result;
            }
        } catch (IOException e) {
            throw new CayleyException(e.getMessage(), e);
        } finally {
            try {
                httpclient.close();
            } catch (IOException ex) {
            }
        }
    }

}
