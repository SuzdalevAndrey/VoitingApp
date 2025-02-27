package ru.andreyszdlv;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import ru.andreyszdlv.config.ServerConfiguration;
import ru.andreyszdlv.handler.CommandHandler;

public class Server {

    private final int port;

    public Server(int port) {
        this.port = port;
    }

    public static void main(String[] args) throws InterruptedException {
        new Server(new ServerConfiguration("application.properties").getPort()).run();
    }

    public void run() throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = createBootstrap(bossGroup, workerGroup);

            ChannelFuture future = bootstrap.bind(port).sync();
            System.out.println("Сервер запущен на порту " + port);
            future.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    private ServerBootstrap createBootstrap(EventLoopGroup bossGroup, EventLoopGroup workerGroup) {
        return new ServerBootstrap().group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) {
                        socketChannel.pipeline().addLast(
                                new StringDecoder(),
                                new StringEncoder(),
                                new CommandHandler()
                        );
                    }
                });
    }
}