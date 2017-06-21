package handling.cashshop;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IoAcceptor;
import org.apache.mina.common.SimpleByteBufferAllocator;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.SocketAcceptor;
import org.apache.mina.transport.socket.nio.SocketAcceptorConfig;

import constants.ServerConfig;
import handling.MapleServerHandler;
import handling.channel.PlayerStorage;
import net.mina.MapleCodecFactory;

public class CashShopServer {

    private static String ip;
    private static InetSocketAddress InetSocketadd;
    private final static int PORT = 8610;
    private static IoAcceptor acceptor;
    private static PlayerStorage players;
    private static boolean finishedShutdown = false;

    public static void run_startup_configurations() {
        ip = ServerConfig.IP_ADDRESS + ":" + PORT;

        ByteBuffer.setUseDirectBuffers(false);
        ByteBuffer.setAllocator(new SimpleByteBufferAllocator());

        acceptor = new SocketAcceptor();
        final SocketAcceptorConfig cfg = new SocketAcceptorConfig();
        cfg.getSessionConfig().setTcpNoDelay(true);
        cfg.setDisconnectOnUnbind(true);
        cfg.getFilterChain().addLast("codec", new ProtocolCodecFilter(new MapleCodecFactory()));
        players = new PlayerStorage(-10);

        try {
            InetSocketadd = new InetSocketAddress(PORT);
            acceptor.bind(InetSocketadd, new MapleServerHandler(), cfg);
            System.out.println("Cash Shop Server is listening on port " + PORT + ".");
        } catch (final IOException e) {
            System.out.println(" Failed!");
            System.err.println("Could not bind to port " + PORT + ".");
            throw new RuntimeException("Binding failed.", e);
        }
    }

    public static String getIP() {
        return ip;
    }

    public static PlayerStorage getPlayerStorage() {
        return players;
    }

    public static void shutdown() {
        if (finishedShutdown) {
            return;
        }
        System.out.println("Saving all connected clients (CS)...");
        players.disconnectAll();
        System.out.println("Shutting down CS...");
        //acceptor.unbindAll();
        finishedShutdown = true;
    }

    public static boolean isShutdown() {
        return finishedShutdown;
    }
}
