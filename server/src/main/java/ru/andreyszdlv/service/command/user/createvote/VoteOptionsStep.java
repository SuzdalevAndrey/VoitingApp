package ru.andreyszdlv.service.command.user.createvote;

import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import ru.andreyszdlv.service.MessageService;

@Slf4j
@Service
@Order(3)
@RequiredArgsConstructor
public class VoteOptionsStep implements VoteStepStrategy {

    private final MessageService messageService;

    @Override
    public void execute(ChannelHandlerContext ctx, String message, VoteCreationService service) {
        log.info("Received vote option: '{}'", message);

        service.addOption(message);

        if (service.isMoreOptionsNeeded()) {
            int nextOption = service.getOptionsCount() + 1;
            log.info("More options needed. Requesting option #{}", nextOption);
            messageService.sendMessageByKey(ctx, "vote.option", nextOption);
        } else {
            log.info("All options received. Completing vote creation");
            service.completeVote(ctx);
        }
    }
}