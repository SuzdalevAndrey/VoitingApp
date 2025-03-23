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
class VoteOptionsStepTest {

    @Mock
    ChannelHandlerContext ctx;

    @Mock
    MessageService messageService;

    @Mock
    VoteCreationService voteCreationService;

    @InjectMocks
    VoteOptionsStep voteOptionsStep;

    @Test
    void execute_AddOptionAndAskForNext_WhenMoreOptionsNeeded() {
        String message = "Вариант 1";

        when(voteCreationService.isMoreOptionsNeeded()).thenReturn(true);
        when(voteCreationService.getOptionsCount()).thenReturn(1);

        voteOptionsStep.execute(ctx, message, voteCreationService);

        verify(voteCreationService, times(1)).addOption(message);
        verify(messageService, times(1))
                .sendMessageByKey(ctx, "vote.option", 2);
        verifyNoMoreInteractions(messageService, voteCreationService);
        verifyNoInteractions(ctx);
    }

    @Test
    void execute_CompleteVote_WhenNoMoreOptionsNeeded() {
        String message = "Вариант 1";
        when(voteCreationService.isMoreOptionsNeeded()).thenReturn(false);

        voteOptionsStep.execute(ctx, message, voteCreationService);

        verify(voteCreationService, times(1)).addOption(message);
        verify(voteCreationService, times(1)).completeVote(ctx);
        verifyNoMoreInteractions(voteCreationService);
        verifyNoInteractions(ctx);
    }
}