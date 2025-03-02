package ru.andreyszdlv.service.command.user.createvote;

import io.netty.channel.ChannelHandlerContext;
import ru.andreyszdlv.util.MessageProviderUtil;

public class VoteOptionsCountStep implements VoteStepStrategy{
    @Override
    public void execute(ChannelHandlerContext ctx, String message, VoteCreationService service) {
        try {
            int count = Integer.parseInt(message);
            if (count <= 0) {
                ctx.writeAndFlush(MessageProviderUtil.getMessage("error.vote.options_count.negative"));
                return;
            }
            service.setNumberOfOptions(count);
            service.nextStep();
            ctx.writeAndFlush(MessageProviderUtil.getMessage("vote.option",1));
        } catch (NumberFormatException e) {
            ctx.writeAndFlush(MessageProviderUtil.getMessage("error.vote.options_count.invalid"));
        }
    }
}