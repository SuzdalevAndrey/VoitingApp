package ru.andreyszdlv.service.command.user.createvote;

import io.netty.channel.ChannelHandlerContext;
import ru.andreyszdlv.util.MessageProviderUtil;

public class VoteOptionsStep implements VoteStepStrategy{
    @Override
    public void execute(ChannelHandlerContext ctx, String message, VoteCreationService service) {
        service.addOption(message);
        if (service.isMoreOptionsNeeded()) {
            ctx.writeAndFlush(MessageProviderUtil.getMessage("vote.option" , (service.getOptionsCount() + 1)));
        } else {
            service.completeVote(ctx);
        }
    }
}