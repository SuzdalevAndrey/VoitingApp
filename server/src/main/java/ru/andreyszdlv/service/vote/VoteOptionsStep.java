package ru.andreyszdlv.service.vote;

import io.netty.channel.ChannelHandlerContext;

public class VoteOptionsStep implements VoteStepStrategy{
    @Override
    public void execute(ChannelHandlerContext ctx, String message, VoteCreationService service) {
        service.addOption(message);
        if (service.isMoreOptionsNeeded()) {
            ctx.writeAndFlush("Введите вариант ответа #" + (service.getOptionsCount() + 1) + ":");
        } else {
            service.completeVote(ctx);
        }
    }
}