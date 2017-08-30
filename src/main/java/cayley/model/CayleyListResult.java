package cayley.model;

import java.util.List;

import lombok.Data;

/**
 * @author wangoo
 * @since 2017-08-30 13:37
 */
@Data
public class CayleyListResult extends CayleyResult {
    private List<CayleyListItem> result;
}
