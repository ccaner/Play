package play.remotemock.util;

import org.apache.commons.lang3.ClassUtils;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.remotemock.Remotable;

import java.rmi.RemoteException;

public class RemotableMockFactory {

    private static final Logger logger = LoggerFactory.getLogger(RemotableMockFactory.class);

    private RmiRegistry localRegistry;
    private RmiRegistry remoteRegistry;

    public RemotableMockFactory(RmiRegistry localRegistry, RmiRegistry remoteRegistry) {
        this.localRegistry = localRegistry;
        this.remoteRegistry = remoteRegistry;
    }

    public <T> T mockAndAttach(Class<T> serviceInterface, String serviceName) throws RemoteException {
        Answer<Object> defaultAnswer = new RemotableMockAnswer<T>(serviceInterface);

        @SuppressWarnings("unchecked")
        Remotable<T> backend = remoteRegistry.obtainServiceClient(Remotable.class, serviceName + "Remote");

        T mock = Mockito.mock(serviceInterface, Mockito.withSettings().defaultAnswer(defaultAnswer));
        T localStub = localRegistry.registerService(mock, serviceInterface);
        backend.attachRemote(localStub);
        backend.switchRemoteModeOn();
        RemoteTestUtil.mockToRemotable.put(mock, backend);
        return mock;
    }

    public <T> T mockAndAttach(Class<T> serviceInterface, String serviceName,
                               Answer defaultAnswer) throws RemoteException {
        @SuppressWarnings("unchecked")
        Remotable<T> backend = remoteRegistry.obtainServiceClient(Remotable.class, serviceName + "Remote");

        T mock = Mockito.mock(serviceInterface, defaultAnswer);
        T localStub = localRegistry.registerService(mock, serviceInterface);
        backend.attachRemote(localStub);
        backend.switchRemoteModeOn();
        RemoteTestUtil.mockToRemotable.put(mock, backend);
        return mock;
    }

    static class RemotableMockAnswer<T> implements Answer<Object> {

        private Class<T> serviceInterface;

        RemotableMockAnswer(Class<T> serviceInterface) {
            this.serviceInterface = serviceInterface;
        }

        @Override
        public Object answer(InvocationOnMock invocation) throws Throwable {
            /* mockito passes toString calls to default answer */
//            if (ClassUtils.declaresMethod(serviceInterface, invocation.getMethod())) {
                throw new InvokeLocalMethodException();
//            }
//            return ClassUtil.invokeMethodOnProxy(invocation.getMock(),
//                    invocation.getMethod(), invocation.getArguments());
        }
    }

}
