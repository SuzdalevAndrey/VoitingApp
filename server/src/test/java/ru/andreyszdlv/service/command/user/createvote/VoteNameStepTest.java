package ru.andreyszdlv.service.command.user.createvote;

import io.netty.channel.ChannelHandlerContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.andreyszdlv.repo.TopicRepository;
import ru.andreyszdlv.util.MessageProviderUtil;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VoteNameStepTest {

    @Mock
    TopicRepository topicRepository;

    @Mock
    VoteCreationService voteCreationService;

    @Mock
    ChannelHandlerContext ctx;

    final String topicName = "TopicName";

    VoteNameStep voteNameStep;

    @BeforeEach
    void setUp() {
        voteNameStep = new VoteNameStep(topicRepository, topicName);
    }

    @Test
    void execute_SendErrorMessageAndDoNotProceed_WhenVoteNameIsBlank() {
        String voteName = "    ";

        voteNameStep.execute(ctx, voteName, voteCreationService);

        verify(ctx, times(1))
                .writeAndFlush(MessageProviderUtil.getMessage("error.vote.name.empty"));
        verifyNoMoreInteractions(ctx);
        verifyNoInteractions(voteCreationService);
    }

    @Test
    void execute_SendErrorMessage_WhenVoteAlreadyExists() {
        String voteName = "VoteName";
        when(topicRepository.containsVoteByName(topicName, voteName)).thenReturn(true);

        voteNameStep.execute(ctx, voteName, voteCreationService);

        verify(ctx, times(1))
                .writeAndFlush(MessageProviderUtil.getMessage("error.vote.already_exist", voteName));
        verifyNoInteractions(voteCreationService);
        verifyNoMoreInteractions(ctx);
    }

    @Test
    void execute_SetVoteNameAndProceedToNextStep_WhenVoteNameValid() {
        String voteName = "newVote";
        String message = "   " + voteName;
        when(topicRepository.containsVoteByName(topicName, voteName)).thenReturn(false);

        voteNameStep.execute(ctx, message, voteCreationService);

        verify(voteCreationService, times(1)).setVoteName(voteName);
        verify(voteCreationService, times(1)).nextStep();
        verify(ctx, times(1))
                .writeAndFlush(MessageProviderUtil.getMessage("vote.description"));
        verifyNoMoreInteractions(ctx);
        verifyNoMoreInteractions(voteCreationService);
    }
}