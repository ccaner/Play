package play.remotemock.util;

import play.remotemock.Remotable;

import java.util.Map;
import java.util.WeakHashMap;

public class RemoteTestUtil {

    public static final Map<Object, Remotable> mockToRemotable;

    static {
        mockToRemotable = new WeakHashMap<Object, Remotable>();
    }

    public static <T> T switchRemoteModeOn(T mock) {
        Remotable backend = mockToRemotable.get(mock);
        if (backend == null) {
            throw new IllegalArgumentException("Mock does not have a corresponding remotable instance");
        }
        backend.switchRemoteModeOn();
        return mock;
    }

    public static <T> T switchRemoteModeOff(T mock) {
        Remotable backend = mockToRemotable.get(mock);
        if (backend == null) {
            throw new IllegalArgumentException("Mock does not have a corresponding remotable instance");
        }
        backend.switchRemoteModeOff();
        return mock;
    }
}
