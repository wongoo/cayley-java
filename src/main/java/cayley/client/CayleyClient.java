package cayley.client;


import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cayley.exception.CayleyException;
import cayley.exception.DuplicatedException;
import cayley.model.CayleyListItem;
import cayley.model.CayleyListResult;
import cayley.model.CayleyResult;
import cayley.model.CayleyWriteResult;
import cayley.model.Quad;

/**
 * @author wangoo
 * @since 2017-08-30 10:44
 */
public class CayleyClient {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private String writeUrl;
    private String gremlinUrl;

    private String url;

    private String version;

    public CayleyClient() {
        this("http://127.0.0.1:64210", "v1");
    }

    public CayleyClient(String url) {
        this(url, "v1");
    }

    public CayleyClient(String url, String version) {

        this.url = url;
        this.version = version;
        this.gremlinUrl = url + "/api/" + version + "/query/gremlin";
        this.writeUrl = url + "/api/" + version + "/write";
    }

    public String query(String command) throws CayleyException {
        return send(gremlinUrl, command);
    }

    private String write(String data) throws CayleyException {
        return send(writeUrl, data);
    }

    public String write(Quad quad) throws CayleyException {
        return write(Arrays.asList(quad));
    }

    public String write(List<Quad> quads) throws CayleyException {
        return write(quads.toString());
    }

    public void addPredicate(String subject, String predicate, String object)
            throws CayleyException {
        parseResult(write(new Quad(subject, predicate, object)), CayleyWriteResult.class);
    }

    public List<String> queryPredicateFrom(String subject, String predicate)
            throws CayleyException {
        String query = query(String.format("g.V('%s').Out('%s').All()", subject, predicate));
        CayleyListResult result = parseResult(query, CayleyListResult.class);
        List<CayleyListItem> list = result.getResult();
        if (list == null || list.size() == 0) {
            return new ArrayList<>(0);
        } else {
            List<String> ids = new ArrayList<>(list.size());
            list.stream().forEach(e -> {
                ids.add(e.getId());
            });
            return ids;
        }
    }

    private <T extends CayleyResult> T parseResult(String value, Class<T> clazz)
            throws CayleyException {
        try {
            T result = objectMapper.readValue(value, clazz);
            if (result.getError() != null) {
                if (result.getError().contains("quad exists")) {
                    throw new DuplicatedException(result.getError());
                } else {
                    throw new CayleyException(result.getError());
                }
            }
            return result;
        } catch (Exception e) {
            throw new CayleyException(e.getMessage());
        }
    }

    private String send(String url, String command) throws CayleyException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            HttpPost post = new HttpPost(url);
            StringEntity stringEntity = new StringEntity(command);
            post.setEntity(stringEntity);

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
