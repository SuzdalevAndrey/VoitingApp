package ru.andreyszdlv.service.command.user.createvote;

import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import ru.andreyszdlv.service.MessageService;

@Slf4j
@Service
@Order(1)
@RequiredArgsConstructor
public class VoteDescriptionStep implements VoteStepStrategy {

    private final MessageService messageService;

    @Override
    public void execute(ChannelHandlerContext ctx, String message, VoteCreationService service) {
        log.info("Setting vote description: \"{}\"", message);

        service.setDescription(message);
        service.nextStep();

        messageService.sendMessageByKey(ctx, "vote.options_count");
    }
}