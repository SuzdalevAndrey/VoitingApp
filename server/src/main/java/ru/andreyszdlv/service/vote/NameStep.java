package ru.andreyszdlv.service.vote;

import io.netty.channel.ChannelHandlerContext;
import ru.andreyszdlv.service.VoteCreationService;

public class NameStep implements VoteStepStrategy {
    @Override
    public void execute(ChannelHandlerContext ctx, String message, VoteCreationService service) {
        service.setNameVote(message);
        service.nextStep();
        ctx.writeAndFlush("Введите тему голосования:\n");
    }
}
