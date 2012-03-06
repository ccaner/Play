package remotemock.it.util;

import play.remotemock.Remotable;

/**
 * Created by IntelliJ IDEA.
 * User: caner
 * Date: 3/6/12
 * Time: 2:16 AM
 * To change this template use File | Settings | File Templates.
 */
public class RemoteTestUtil {

    public static <T> T switchRemoteModeOn(T mock) {
        if (!(mock instanceof Remotable)) {
            throw new IllegalArgumentException("Remotable mock expected");
        }
        ((Remotable)mock).switchRemoteModeOn();
        return mock;
    }

    public static <T> T switchRemoteModeOff(T mock) {
        if (!(mock instanceof Remotable)) {
            throw new IllegalArgumentException("Remotable mock expected");
        }
        ((Remotable)mock).switchRemoteModeOff();
        return mock;
    }
}
