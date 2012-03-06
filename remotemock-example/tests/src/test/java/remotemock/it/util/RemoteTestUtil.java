package remotemock.it.util;

import play.remotemock.Remotable;

public class RemoteTestUtil {

    public static <T> T switchRemoteModeOn(T mock) {
        Remotable backend = RemotableMockFactory.createdMocks.get(mock);
        if (backend == null) {
            throw new IllegalArgumentException("Remotable mock expected");
        }
        backend.switchRemoteModeOn();
        return mock;
    }

    public static <T> T switchRemoteModeOff(T mock) {
        Remotable backend = RemotableMockFactory.createdMocks.get(mock);
        if (backend == null) {
            throw new IllegalArgumentException("Remotable mock expected");
        }
        backend.switchRemoteModeOff();
        return mock;
    }
}
