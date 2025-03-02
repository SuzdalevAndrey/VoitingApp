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
class VoteOptionsStepTest {

    @Mock
    ChannelHandlerContext ctx;

    @Mock
    VoteCreationService voteCreationService;

    VoteOptionsStep voteOptionsStep;

    @BeforeEach
    void setUp() {
        voteOptionsStep = new VoteOptionsStep();
    }

    @Test
    void execute_AddOptionAndAskForNext_WhenMoreOptionsNeeded() {
        String message = "Вариант 1";
        when(voteCreationService.isMoreOptionsNeeded()).thenReturn(true);
        when(voteCreationService.getOptionsCount()).thenReturn(1);

        voteOptionsStep.execute(ctx, message, voteCreationService);

        verify(voteCreationService, times(1)).addOption(message);
        verify(ctx, times(1))
                .writeAndFlush(MessageProviderUtil.getMessage("vote.option", 2));
        verifyNoMoreInteractions(ctx, voteCreationService);
    }

    @Test
    void execute_CompleteVote_WhenNoMoreOptionsNeeded() {
        String message = "Вариант 1";
        when(voteCreationService.isMoreOptionsNeeded()).thenReturn(false);

        voteOptionsStep.execute(ctx, message, voteCreationService);

        verify(voteCreationService, times(1)).addOption(message);
        verify(voteCreationService, times(1)).completeVote(ctx);
        verifyNoMoreInteractions(ctx, voteCreationService);
    }
}