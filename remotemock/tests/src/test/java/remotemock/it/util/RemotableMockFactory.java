package remotemock.it.util;

import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import play.remotemock.Remotable;
import play.remotemock.util.CallLocalException;
import play.remotemock.util.RmiRegistry;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

public class RemotableMockFactory {

    RmiRegistry remoteRegistry;
    RmiRegistry localRegistry;

    static Map<Object, Remotable> createdMocks = new HashMap<>();

    public RemotableMockFactory(RmiRegistry remoteRegistry, RmiRegistry localRegistry) {
        this.remoteRegistry = remoteRegistry;
        this.localRegistry = localRegistry;
    }

    public <T> T mockAndAttach(Class<T> serviceInterface, String serviceName) throws RemoteException {
        final T mock = Mockito.mock(serviceInterface);
        final Remotable backend = remoteRegistry.getClientProxy(serviceName + "Remote", Remotable.class);
        localRegistry.exportService(mock, serviceName, serviceInterface);
        backend.attachRemote(localRegistry.getServiceUrl(serviceName));
        createdMocks.put(mock, backend);
        return mock;
    }

    public <T> T spyAndAttach(final Class<T> serviceInterface, String serviceName) throws RemoteException {
        final T mock = Mockito.mock(serviceInterface, new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                throw new CallLocalException();
            }
        });
        final Remotable backend = remoteRegistry.getClientProxy(serviceName + "Remote", Remotable.class);
        localRegistry.exportService(mock, serviceName, serviceInterface);
        backend.attachRemote(localRegistry.getServiceUrl(serviceName));
        createdMocks.put(mock, backend);
        return mock;
    }
}

