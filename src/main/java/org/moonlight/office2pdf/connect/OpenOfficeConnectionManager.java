package org.moonlight.office2pdf.connect;

import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;

import java.io.IOException;

/**
 * 〈功能简述〉<br>
 * 〈〉
 *
 * @author Moonlight
 * @date 2021/4/12 17:44
 */
public class OpenOfficeConnectionManager {

    private static OpenOfficeConnectionPool POOL;
//    private static ThreadLocal<OpenOfficeConnection> threadLocal = new ThreadLocal<>();

    public static void init(OpenOfficeConnectionConfig config) throws IOException {

        startOpenOffice(config);

        POOL = new OpenOfficeConnectionPool(config.getOpenOfficeHost(), config.getOpenOfficePort(), config.getConnectionSize());
    }

    public static OpenOfficeConnection getOpenOfficeConnection() throws InterruptedException {
//        OpenOfficeConnection connection = threadLocal.get();
//        if (connection == null) {
//            connection = POOL.getConnection();
//            threadLocal.set(connection);
//        }
//        return connection;
        return POOL.getConnection();
    }

//    public static void closeConnection() {
//        threadLocal.remove();
//    }

    /**
     * 功能描述: <br>
     * 〈〉
     * 启动openOffice服务
     *
     * @author Moonlight
     * @date 2021/4/12 17:06
     * @since 1.0.0
     */
    private static void startOpenOffice(OpenOfficeConnectionConfig config) throws IOException {
        String command = config.getOpenOfficePath() + " -headless -accept=\"socket,host="
                + config.getOpenOfficeHost() + ",port=" + config.getOpenOfficePort() + ";urp;\" -nofirststartwizard";
        Runtime.getRuntime().exec(command);
    }

}
