package play.resultsetmock.jdbc.data;

import java.util.List;

public interface RowAccessor {

    Object getRow(Object rs, int index);

    int size(Object rs);

    public static RowAccessor LIST = new RowAccessor() {
        @Override
        public Object getRow(Object rs, int index) {
            return ((List)rs).get(index);
        }

        @Override
        public int size(Object rs) {
            return ((List)rs).size();
        }
    };

}

