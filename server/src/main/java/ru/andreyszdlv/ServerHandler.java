package ru.andreyszdlv;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.HashMap;
import java.util.Map;

public class ServerHandler extends SimpleChannelInboundHandler<String> {

    private static final Map<String, String> users = new HashMap<>();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) {
        System.out.println("Получена команда: " + msg);

        String[] parts = msg.split(" ");
        String command = parts[0];

        switch (command) {
            case "login":
                handleLogin(ctx, parts);
                break;
            case "create":
                handleCreate(ctx, parts);
                break;
            case "view":
                handleView(ctx, parts);
                break;
            case "vote":
                handleVote(ctx, parts);
                break;
            case "exit":
                ctx.close();
                break;
            default:
                ctx.writeAndFlush("Неизвестная команда: " + msg + "\n");
        }
    }

    private void handleLogin(ChannelHandlerContext ctx, String[] parts) {
        if (parts.length < 2) {
            ctx.writeAndFlush("Ошибка: укажите имя пользователя. Пример: login -u=User123\n");
            return;
        }
        String username = parts[1].split("=")[1];
        users.put(ctx.channel().remoteAddress().toString(), username);
        ctx.writeAndFlush("Пользователь " + username + " вошел в систему!\n");
    }

    private void handleCreate(ChannelHandlerContext ctx, String[] parts) {
        if (parts.length < 3) {
            ctx.writeAndFlush("Ошибка: используйте create topic -n=<topic>\n");
            return;
        }
        String topic = parts[2].split("=")[1];
//        VotingStorage.createTopic(topic);
        ctx.writeAndFlush("Раздел '" + topic + "' создан!\n");
    }

    private void handleView(ChannelHandlerContext ctx, String[] parts) {
//        ctx.writeAndFlush(VotingStorage.viewTopics());
        ctx.writeAndFlush("Просмотр топиков");
    }

    private void handleVote(ChannelHandlerContext ctx, String[] parts) {
        ctx.writeAndFlush("Функция голосования в разработке...\n");
    }
}
