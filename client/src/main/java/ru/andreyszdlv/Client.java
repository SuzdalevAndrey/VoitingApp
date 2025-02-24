package ru.andreyszdlv;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Scanner;


public class Client {

    //todo вынести в конфиг файл
    private final String host;
    private final int port;

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void run() throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel ch) {
                            ch.pipeline().addLast(new ClientHandler());
                        }
                    });

            ChannelFuture future = bootstrap.connect(host, port).sync();
            System.out.println("Подключено к серверу " + host + ":" + port);

            Scanner scanner = new Scanner(System.in);
            //todo вынести в отдельный файл обработку
            while (true) {
                System.out.print("> ");
                String command = scanner.nextLine();
                if ("exit".equalsIgnoreCase(command)) break;

                future.channel().writeAndFlush(command + "\n");
            }

            future.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new Client("localhost", 8080).run();
    }
}
