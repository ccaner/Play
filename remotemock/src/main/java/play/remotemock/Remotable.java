package play.remotemock;

/**
 * Implementations can proxy method invocations to an attached remote object.
 * <p/>
 * If you are using this interface instead of Remotable annotation, your class should handle
 * all proxying logic.
 */
public interface Remotable<T> {

    void attachRemote(T remoteObject);

    void switchRemoteModeOn();

    void switchRemoteModeOff();

}
