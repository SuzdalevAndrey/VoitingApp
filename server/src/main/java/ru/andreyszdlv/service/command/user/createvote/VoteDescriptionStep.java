package ru.andreyszdlv.service.command.user.createvote;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import ru.andreyszdlv.util.MessageProviderUtil;

@Slf4j
public class VoteDescriptionStep implements VoteStepStrategy{
    @Override
    public void execute(ChannelHandlerContext ctx, String message, VoteCreationService service) {
        log.info("Setting vote description: \"{}\"", message);

        service.setDescription(message);
        service.nextStep();

        ctx.writeAndFlush(MessageProviderUtil.getMessage("vote.options_count"));
    }
}