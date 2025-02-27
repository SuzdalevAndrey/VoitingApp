package ru.andreyszdlv.service.vote;

import io.netty.channel.ChannelHandlerContext;

public class VoteOptionsCountStep implements VoteStepStrategy{
    @Override
    public void execute(ChannelHandlerContext ctx, String message, VoteCreationService service) {
        try {
            int count = Integer.parseInt(message);
            if (count <= 0) {
                ctx.writeAndFlush("Количество вариантов должно быть положительным числом.");
                return;
            }
            service.setNumberOfOptions(count);
            service.nextStep();
            ctx.writeAndFlush("Введите вариант ответа #1:");
        } catch (NumberFormatException e) {
            ctx.writeAndFlush("Пожалуйста, введите правильное число вариантов.");
        }
    }
}