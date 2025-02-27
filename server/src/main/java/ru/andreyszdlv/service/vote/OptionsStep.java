package ru.andreyszdlv.service.vote;

import io.netty.channel.ChannelHandlerContext;
import ru.andreyszdlv.service.VoteCreationService;

public class OptionsStep implements VoteStepStrategy{
    @Override
    public void execute(ChannelHandlerContext ctx, String message, VoteCreationService service) {
        service.addOption(message);
        if (service.isMoreOptionsNeeded()) {
            ctx.writeAndFlush("Введите вариант ответа #" + (service.getOptionsCount() + 1) + ":\n");
        } else {
            service.completeVote(ctx);
        }
    }
}
