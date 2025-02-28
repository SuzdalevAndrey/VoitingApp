package ru.andreyszdlv;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class Client {
    private final String host;

    private final int port;

    private final UserInputHandler userInputHandler;

    public Client(String host, int port, UserInputHandler userInputHandler) {
        this.host = host;
        this.port = port;
        this.userInputHandler = userInputHandler;
    }

    public static void main(String[] args) throws InterruptedException {
        Config config = new Config("application.properties");
        new Client(config.getHost(), config.getPort(), new ConsoleUserInputHandler()).run();
    }

    public void run() throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = createBootstrap(group);
            ChannelFuture future = connect(bootstrap, host, port).sync();

            userInputHandler.handle(future, group);

            future.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }

    private Bootstrap createBootstrap(EventLoopGroup group) {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(
                                new StringDecoder(),
                                new StringEncoder(),
                                new ClientHandler()
                        );
                    }
                });
        return bootstrap;
    }

    private ChannelFuture connect(Bootstrap bootstrap, String host, int port) {
        try{
            ChannelFuture future = bootstrap.connect(host, port).sync();
            System.out.println("Подключение установлено");
            return future;
        }
        catch (Exception e) {
            System.err.println("Не удалось подключиться");
            throw new RuntimeException(e);
        }
    }
}