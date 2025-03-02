package ru.andreyszdlv.service.command.user.createvote;

import io.netty.channel.ChannelHandlerContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.andreyszdlv.util.MessageProviderUtil;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VoteOptionsCountStepTest {

    @Mock
    ChannelHandlerContext ctx;

    @Mock
    VoteCreationService voteCreationService;

    VoteOptionsCountStep voteOptionsCountStep;

    @BeforeEach
    void setUp() {
        voteOptionsCountStep = new VoteOptionsCountStep();
    }

    @Test
    void execute_SendErrorMessage_WhenOptionsCountNotNumber() {
        String message = "one";

        voteOptionsCountStep.execute(ctx, message, voteCreationService);

        verify(ctx, times(1))
                .writeAndFlush(MessageProviderUtil.getMessage("error.vote.options_count.invalid"));
        verifyNoInteractions(voteCreationService);
        verifyNoMoreInteractions(ctx);
    }

    @Test
    void execute_SendErrorMessage_WhenOptionsCountNegative() {
        String message = "-1";

        voteOptionsCountStep.execute(ctx, message, voteCreationService);

        verify(ctx, times(1))
                .writeAndFlush(MessageProviderUtil.getMessage("error.vote.options_count.negative"));
        verifyNoInteractions(voteCreationService);
        verifyNoMoreInteractions(ctx);
    }

    @Test
    void execute_SendErrorMessage_WhenOptionsCountZero() {
        String message = "0";

        voteOptionsCountStep.execute(ctx, message, voteCreationService);

        verify(ctx, times(1))
                .writeAndFlush(MessageProviderUtil.getMessage("error.vote.options_count.negative"));
        verifyNoInteractions(voteCreationService);
        verifyNoMoreInteractions(ctx);
    }

    @Test
    void execute_SetNumberOfOptionsAndProceed_WhenOptionsCountValid() {
        String message = "5";

        voteOptionsCountStep.execute(ctx, message, voteCreationService);

        verify(voteCreationService, times(1)).setNumberOfOptions(5);
        verify(voteCreationService, times(1)).nextStep();
        verify(ctx, times(1))
                .writeAndFlush(MessageProviderUtil.getMessage("vote.option", 1));
        verifyNoMoreInteractions(ctx, voteCreationService);
    }
}