package ru.andreyszdlv;

import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;

import java.util.Scanner;

public class ConsoleUserInputHandler implements UserInputHandler {

    @Override
    public void handle(ChannelFuture future, EventLoopGroup group) {
        try(Scanner scanner = new Scanner(System.in)) {
            while (true) {
                String command = scanner.nextLine();
                if ("exit".equals(command)){
                    shutdown(future, group);
                    break;
                }
                future.channel().writeAndFlush(command);
            }
        }
    }

    private void shutdown(ChannelFuture future, EventLoopGroup group) {
        future.channel().close();
        group.shutdownGracefully();
        System.exit(0);
    }
}
