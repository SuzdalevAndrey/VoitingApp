package ru.andreyszdlv.service.command.user.createvote;

import io.netty.channel.ChannelHandlerContext;
import ru.andreyszdlv.util.MessageProviderUtil;

public class VoteDescriptionStep implements VoteStepStrategy{
    @Override
    public void execute(ChannelHandlerContext ctx, String message, VoteCreationService service) {
        service.setDescription(message);
        service.nextStep();
        ctx.writeAndFlush(MessageProviderUtil.getMessage("vote.options_count"));
    }
}