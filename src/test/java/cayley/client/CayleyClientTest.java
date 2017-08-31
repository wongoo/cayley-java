package cayley.client;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import cayley.exception.CayleyException;
import cayley.model.Quad;

/**
 * @author wangoo
 * @since 2017-08-30 11:25
 */
public class CayleyClientTest {

    CayleyClient client = new CayleyClient("http://192.168.5.237:64210");

    @Test
    public void testWrite() {
        List<Quad> quads = new ArrayList<>();
        quads.add(new Quad("u1", "follows", "u2"));
        quads.add(new Quad("u2", "follows", "u3"));
        quads.add(new Quad("u1", "like", "u3"));
        try {
            String result = client.write(quads);
            System.out.println(result);
        } catch (CayleyException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testQuery() {
        try {
            String result = client.query("g.V('u1').Out().All()");
            System.out.println(result);
        } catch (CayleyException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testPredicate() {
        try {
            client.addPredicate("x", "f", "y");
            client.addPredicate("x", "f", "z");
        } catch (CayleyException e) {
            System.out.println(e.getMessage());
        }
        try {
            List<String> ids = client.queryPredicate("x", "f");
            System.out.println(ids);
        } catch (CayleyException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testExist() {
        try {
            client.addPredicate("g1", "group", "u1");
        } catch (CayleyException e) {
            System.out.println(e.getMessage());
        }
        try {
            boolean exist = client.existPredicate("g1", "group", "u1");
            Assert.assertTrue(exist);

            exist = client.existPredicate("g1", "group", "u" + System.currentTimeMillis());
            Assert.assertFalse(exist);
        } catch (CayleyException e)

        {
            Assert.fail(e.getMessage());
        }
    }
}
