package ru.andreyszdlv.service.vote;

import io.netty.channel.ChannelHandlerContext;

public class VoteDescriptionStep implements VoteStepStrategy{
    @Override
    public void execute(ChannelHandlerContext ctx, String message, VoteCreationService service) {
        service.setDescription(message);
        service.nextStep();
        ctx.writeAndFlush("Введите количество вариантов ответа:");
    }
}
