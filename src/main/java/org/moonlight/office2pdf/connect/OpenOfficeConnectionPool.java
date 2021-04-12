package org.moonlight.office2pdf.connect;

import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.connection.SocketOpenOfficeConnection;
import org.moonlight.office2pdf.common.Const;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.util.LinkedList;

/**
 * open office 连接池
 *
 * @author Moonlight
 * @date 2021/4/10 11:50
 */
public class OpenOfficeConnectionPool {

    private final LinkedList<OpenOfficeConnection> pool;
    private final OpenOfficeConnectionConfig connectionConfig;

    public OpenOfficeConnectionPool(OpenOfficeConnectionConfig connectionConfig) throws Exception {
        this.connectionConfig = connectionConfig;
        this.pool = new LinkedList<>();
        this.init();
    }

    public OpenOfficeConnection getConnection() {
        if (pool.isEmpty()) {
            throw new RuntimeException("连接池已无更多连接");
        }
        return pool.remove(0);
    }

    private void init() throws Exception {
        for (int i = 0; i < connectionConfig.getConnectionSize(); i++) {
            OpenOfficeConnection connection = createConnection();
            if (connection != null) {
                OpenOfficeConnection proxyConnection = (OpenOfficeConnection) Proxy.newProxyInstance(OpenOfficeConnectionPool.class.getClassLoader(),
                        new Class<?>[]{Connection.class}, new ConnectionProxy(connection, pool));
                pool.add(proxyConnection);
            }
        }
    }

    private OpenOfficeConnection createConnection() throws Exception {
        OpenOfficeConnection connection = new SocketOpenOfficeConnection(connectionConfig.getOpenOfficeHost(), connectionConfig.getOpenOfficePort());
        connection.connect();
        return connection.isConnected() ? connection : null;
    }

    private static class ConnectionProxy implements InvocationHandler {

        private final OpenOfficeConnection connection;
        private final LinkedList<OpenOfficeConnection> pool;

        ConnectionProxy(OpenOfficeConnection connection, LinkedList<OpenOfficeConnection> pool) {
            this.connection = connection;
            this.pool = pool;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (Const.METHOD_DISCONNECT.equals(method.getName())) {
                pool.addLast(connection);
                return null;
            }
            return method.invoke(connection, args);
        }
    }

}
