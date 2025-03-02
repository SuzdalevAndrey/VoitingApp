package ru.andreyszdlv.service.command.user.createvote;


import io.netty.channel.ChannelHandlerContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.andreyszdlv.util.MessageProviderUtil;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VoteDescriptionStepTest {

    @Mock
    ChannelHandlerContext ctx;

    @Mock
    VoteCreationService voteCreationService;

    @InjectMocks
    VoteDescriptionStep voteDescriptionStep;

    @Test
    void execute_SetDescriptionAndMovesNextStep() {
        String description = "Description";

        voteDescriptionStep.execute(ctx, description, voteCreationService);

        verify(voteCreationService, times(1)).setDescription(description);
        verify(voteCreationService, times(1)).nextStep();
        verify(ctx, times(1))
                .writeAndFlush(MessageProviderUtil.getMessage("vote.options_count"));
        verifyNoMoreInteractions(voteCreationService, ctx);
    }
}