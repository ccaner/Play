package play.remotemock.mock;

public interface Remotable {

    void attachRemote(String rmiUrl);

    void switchRemoteModeOn();

    void switchRemoteModeOff();

}
