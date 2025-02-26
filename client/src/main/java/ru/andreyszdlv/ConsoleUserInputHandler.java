package ru.andreyszdlv;

import io.netty.channel.ChannelFuture;

import java.util.Scanner;

public class ConsoleUserInputHandler implements UserInputHandler {
    @Override
    public void handle(ChannelFuture future) {
        try(Scanner scanner = new Scanner(System.in)) {
            while (true) {
                String command = scanner.nextLine();
                if ("exit".equals(command)) break;
                future.channel().writeAndFlush(command);
            }
        }
    }
}
