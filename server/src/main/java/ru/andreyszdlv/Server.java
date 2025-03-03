package ru.andreyszdlv;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;
import ru.andreyszdlv.config.ServerConfiguration;
import ru.andreyszdlv.enums.ServerCommandType;
import ru.andreyszdlv.factory.RepositoryFactory;
import ru.andreyszdlv.handler.CommandHandler;
import ru.andreyszdlv.service.command.server.ServerCommandService;
import ru.andreyszdlv.service.command.user.UserCommandService;
import ru.andreyszdlv.validator.AuthenticationValidator;

import java.util.Scanner;

@Slf4j
public class Server {

    private final int port;

    private final ServerCommandService serverCommandService = new ServerCommandService();

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

            log.info("The server is running on port: {}", port);
            commandListener();

            future.channel().close().sync();
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
                                new CommandHandler(new UserCommandService(new AuthenticationValidator(RepositoryFactory.getUserRepository())))
                        );
                    }
                });
    }

    private void commandListener() {
        try(Scanner scanner = new Scanner(System.in)) {
            while(true){
                String command = scanner.nextLine();
                if(command.equals(ServerCommandType.EXIT.getName())){
                    log.info("Shutting down the server...");
                    break;
                }
                serverCommandService.dispatch(command);
            }
        }
    }
}