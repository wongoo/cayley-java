package cayley.model;

import lombok.Data;

/**
 * @author wangoo
 * @since 2017-08-30 11:13
 */
@Data
public class Quad {
    private static final String DEFAULT_LABEL = ".";

    private String subject;
    private String predicate;
    private String object;
    private String label = DEFAULT_LABEL;

    public Quad() {
    }

    public Quad(String subject, String predicate, String object) {
        this(subject, predicate, object, DEFAULT_LABEL);
    }

    public Quad(String subject, String predicate, String object, String label) {
        this.subject = subject;
        this.predicate = predicate;
        this.object = object;
        this.label = label;
    }

    @Override
    public String toString() {
        return "{\"subject\":\"" + subject + "\", \"predicate\":\"" + predicate + "\""
                + ", \"object\":\"" + object + "\",\"label\":\"" + label + "\"}";
    }
}
