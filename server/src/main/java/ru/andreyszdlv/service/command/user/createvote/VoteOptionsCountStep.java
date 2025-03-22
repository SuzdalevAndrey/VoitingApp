package ru.andreyszdlv.service.command.user.createvote;

import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import ru.andreyszdlv.service.MessageService;

@Slf4j
@Service
@Order(2)
@RequiredArgsConstructor
public class VoteOptionsCountStep implements VoteStepStrategy {

    private final MessageService messageService;

    @Override
    public void execute(ChannelHandlerContext ctx, String message, VoteCreationService service) {
        int count;

        try {
            count = parseCountOption(message);
        } catch (NumberFormatException e) {
            log.warn("Invalid count option format: {}. Must be number.", message);
            messageService.sendMessageByKey(ctx, "error.vote.options_count.invalid");
            return;
        }

        if (count <= 0) {
            log.warn("Invalid count option: {}. Count option must be positive.", message);
            messageService.sendMessageByKey(ctx, "error.vote.options_count.negative");
            return;
        }

        service.setNumberOfOptions(count);
        service.nextStep();

        log.info("Count options set {}", count);
        messageService.sendMessageByKey(ctx, "vote.option", 1);
    }

    private int parseCountOption(String message) {
        return Integer.parseInt(message);
    }
}