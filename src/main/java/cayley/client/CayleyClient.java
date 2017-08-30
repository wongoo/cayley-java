package cayley.client;


import com.fasterxml.jackson.databind.ObjectMapper;

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
import cayley.util.CayleyHttpUtil;
import cayley.util.CayleyReflectionUtil;

/**
 * @author wangoo
 * @since 2017-08-30 10:44
 */
public class CayleyClient {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private String writeUrl;
    private String deleteUrl;
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
        this.deleteUrl = url + "/api/" + version + "/delete";
    }

    /** ---------------------- write ---------------------------------------- */

    private String write(String data) throws CayleyException {
        return CayleyHttpUtil.send(writeUrl, data);
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

    /** ---------------------- delete ---------------------------------------- */

    private String delete(String data) throws CayleyException {
        return CayleyHttpUtil.send(deleteUrl, data);
    }

    public String delete(Quad quad) throws CayleyException {
        return delete(Arrays.asList(quad));
    }

    public String delete(List<Quad> quads) throws CayleyException {
        return delete(quads.toString());
    }

    public void deletePredicate(String subject, String predicate, String object)
            throws CayleyException {
        delete(new Quad(subject, predicate, object));
    }

    /** ---------------------- query ---------------------------------------- */

    public String query(String command) throws CayleyException {
        return CayleyHttpUtil.send(gremlinUrl, command);
    }

    public List<String> queryPredicate(String subject, String predicate) throws CayleyException {
        return queryList(String.format("g.V('%s').Out('%s').All()", subject, predicate));
    }

    public <T> T queryObject(String subject, Class<T> clazz) throws CayleyException {
        CayleyListResult cayleyListResult =
                queryCayleyList(String.format("g.V('%s').Out(null,'predicate').All()", subject));
        return collectPredicateAsObject(cayleyListResult, clazz);
    }

    public List<String> queryList(String command) throws CayleyException {
        return collectObjectIds(queryCayleyList(command));
    }

    public CayleyListResult queryCayleyList(String command) throws CayleyException {
        String query = query(command);
        return parseResult(query, CayleyListResult.class);
    }

    /** ---------------------- exists ---------------------------------------- */

    public boolean exists(String object) throws CayleyException {
        return queryList(String.format("g.V('%s').All()", object)) != null;
    }

    public boolean existPredicate(String subject, String predicate, String object)
            throws CayleyException {
        String command =
                String.format("g.V('%s').Out('%s').Is('%s').All()", subject, predicate, object);
        return queryList(command) != null;
    }

    /** ---------------------- other ---------------------------------------- */
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

    private List<String> collectObjectIds(CayleyListResult result) {
        if (result == null) {
            return null;
        }
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

    private <T> T collectPredicateAsObject(CayleyListResult result, Class<T> clazz)
            throws CayleyException {
        try {
            T t = clazz.newInstance();
            if (result != null) {
                result.getResult().stream().forEach(e -> {
                    CayleyReflectionUtil.set(t, e.getPredicate(), e.getId());
                });
            }
            return t;
        } catch (Exception e) {
            throw new CayleyException(e);
        }
    }

}
