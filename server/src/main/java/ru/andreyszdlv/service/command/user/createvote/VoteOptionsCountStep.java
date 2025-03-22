package ru.andreyszdlv.service.command.user.createvote;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import ru.andreyszdlv.util.MessageProviderUtil;

@Slf4j
@Service
@Order(2)
public class VoteOptionsCountStep implements VoteStepStrategy {
    @Override
    public void execute(ChannelHandlerContext ctx, String message, VoteCreationService service) {
        try {
            int count = Integer.parseInt(message);
            log.info("Received number of options: {}", count);

            if (count <= 0) {
                log.warn("Invalid count option: {}. Count option must be positive.", message);
                ctx.writeAndFlush(MessageProviderUtil.getMessage("error.vote.options_count.negative"));
                return;
            }
            service.setNumberOfOptions(count);
            service.nextStep();
            log.info("Count options set {}", count);
            ctx.writeAndFlush(MessageProviderUtil.getMessage("vote.option", 1));
        } catch (NumberFormatException e) {
            log.warn("Invalid count option format: {}. Must be number.", message);
            ctx.writeAndFlush(MessageProviderUtil.getMessage("error.vote.options_count.invalid"));
        }
    }
}