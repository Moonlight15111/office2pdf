package org.moonlight.office2pdf.connect;

import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.connection.SocketOpenOfficeConnection;
import org.moonlight.office2pdf.common.Const;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.ConnectException;
import java.util.LinkedList;

/**
 * open office 连接池
 *
 * @author Moonlight
 * @date 2021/4/10 11:50
 */
class OpenOfficeConnectionPool {

    private final LinkedList<OpenOfficeConnection> pool;

    OpenOfficeConnectionPool(String openOfficeHost, Integer openOfficePort, Integer connectionSize) throws ConnectException {
        this.pool = new LinkedList<>();
        this.init(openOfficeHost, openOfficePort, connectionSize);
    }

    synchronized OpenOfficeConnection getConnection() {
        if (pool.isEmpty()) {
            throw new RuntimeException("连接池已无更多连接");
        }
        return pool.removeFirst();
    }

    synchronized void disconnect(OpenOfficeConnection connection) {
        pool.addLast(connection);
    }

    /**
     * 功能描述: <br>
     * 〈〉
     * 初始化连接池
     * @since 1.0.0
     * @author Moonlight
     * @date 2021/4/12 17:08
     */
    private void init(String openOfficeHost, Integer openOfficePort, Integer connectionSize) throws ConnectException {
        for (int i = 0; i < connectionSize; i++) {
            OpenOfficeConnection connection = createConnection(openOfficeHost, openOfficePort);
            if (connection != null) {
                OpenOfficeConnection proxyConnection = (OpenOfficeConnection) Proxy.newProxyInstance(OpenOfficeConnectionPool.class.getClassLoader(),
                        new Class<?>[]{OpenOfficeConnection.class}, new ConnectionProxy(connection, this));
                pool.add(proxyConnection);
            }
        }
    }

    private OpenOfficeConnection createConnection(String openOfficeHost, Integer openOfficePort) throws ConnectException {
        OpenOfficeConnection connection = new SocketOpenOfficeConnection(openOfficeHost, openOfficePort);
        connection.connect();
        return connection.isConnected() ? connection : null;
    }

    private static class ConnectionProxy implements InvocationHandler {

        private final OpenOfficeConnection connection;
        private final OpenOfficeConnectionPool connectionPool;

        ConnectionProxy(OpenOfficeConnection connection, OpenOfficeConnectionPool connectionPool) {
            this.connection = connection;
            this.connectionPool = connectionPool;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (Const.METHOD_DISCONNECT.equals(method.getName())) {
                this.connectionPool.disconnect(connection);
                return null;
            }
            return method.invoke(connection, args);
        }
    }

}
