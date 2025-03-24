package ru.andreyszdlv;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;
import ru.andreyszdlv.config.ClientConfiguration;
import ru.andreyszdlv.handler.ClientHandler;
import ru.andreyszdlv.props.ClientProperties;
import ru.andreyszdlv.service.UserInputHandler;

@Component
@RequiredArgsConstructor
public class Client {

    private final UserInputHandler userInputHandler;
    private final ClientProperties clientProperties;

    public static void main(String[] args) throws InterruptedException {
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(ClientConfiguration.class);

        Client client = applicationContext.getBean(Client.class);
        client.run();
    }

    public void run() throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = createBootstrap(group);
            ChannelFuture future = connect(bootstrap,
                    clientProperties.getHost(),
                    clientProperties.getPort())
                    .sync();

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
        try {
            ChannelFuture future = bootstrap.connect(host, port).sync();
            System.out.println("Подключение установлено");
            return future;
        } catch (Exception e) {
            System.err.println("Не удалось подключиться");
            throw new RuntimeException(e);
        }
    }
}