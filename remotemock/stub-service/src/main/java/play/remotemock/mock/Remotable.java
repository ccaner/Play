package play.remotemock.mock;

public interface Remotable<T> {

    void attachRemote(String rmiUrl);

    void switchRemoteModeOn();

    void switchRemoteModeOff();

}
