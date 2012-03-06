package play.remotemock;

public interface Remotable {

    void attachRemote(String rmiUrl);

    void switchRemoteModeOn();

    void switchRemoteModeOff();

}
