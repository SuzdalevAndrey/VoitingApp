package ru.andreyszdlv.service.command.user.createvote;

import io.netty.channel.ChannelHandlerContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.andreyszdlv.service.MessageService;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VoteOptionsCountStepTest {

    @Mock
    ChannelHandlerContext ctx;

    @Mock
    MessageService messageService;

    @Mock
    VoteCreationService voteCreationService;

    @InjectMocks
    VoteOptionsCountStep voteOptionsCountStep;


    @Test
    void execute_SendErrorMessage_WhenOptionsCountNotNumber() {
        String message = "one";

        voteOptionsCountStep.execute(ctx, message, voteCreationService);

        verify(messageService, times(1))
                .sendMessageByKey(ctx, "error.vote.options_count.invalid");
        verifyNoInteractions(voteCreationService, ctx);
        verifyNoMoreInteractions(messageService);
    }

    @Test
    void execute_SendErrorMessage_WhenOptionsCountNegative() {
        String message = "-1";

        voteOptionsCountStep.execute(ctx, message, voteCreationService);

        verify(messageService, times(1))
                .sendMessageByKey(ctx, "error.vote.options_count.negative");
        verifyNoInteractions(voteCreationService, ctx);
        verifyNoMoreInteractions(messageService);
    }

    @Test
    void execute_SendErrorMessage_WhenOptionsCountZero() {
        String message = "0";

        voteOptionsCountStep.execute(ctx, message, voteCreationService);

        verify(messageService, times(1))
                .sendMessageByKey(ctx, "error.vote.options_count.negative");
        verifyNoInteractions(voteCreationService, ctx);
        verifyNoMoreInteractions(messageService);
    }

    @Test
    void execute_SetNumberOfOptionsAndProceed_WhenOptionsCountValid() {
        String message = "5";

        voteOptionsCountStep.execute(ctx, message, voteCreationService);

        verify(voteCreationService, times(1)).setNumberOfOptions(5);
        verify(voteCreationService, times(1)).nextStep();
        verify(messageService, times(1))
                .sendMessageByKey(ctx, "vote.option", 1);
        verifyNoMoreInteractions(messageService, voteCreationService);
        verifyNoInteractions(ctx);
    }
}