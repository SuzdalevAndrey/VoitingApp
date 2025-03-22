package ru.andreyszdlv.service.command.user.vote;

import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.andreyszdlv.enums.UserCommandType;
import ru.andreyszdlv.factory.HandlerFactory;
import ru.andreyszdlv.model.AnswerOption;
import ru.andreyszdlv.model.Vote;
import ru.andreyszdlv.repo.TopicRepository;
import ru.andreyszdlv.service.command.user.UserCommandHandler;
import ru.andreyszdlv.util.MessageProviderUtil;
import ru.andreyszdlv.util.ParamUtil;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class VoteUserCommand implements UserCommandHandler {

    private final TopicRepository topicRepository;

    private final HandlerFactory handlerFactory;

    @Override
    public void execute(ChannelHandlerContext ctx, String[] paramsCommand) {
        log.info("Executing vote command with params: {}", (Object) paramsCommand);

        String topicName = ParamUtil.extractValueByPrefix(paramsCommand[0], "-t=");
        String voteName = ParamUtil.extractValueByPrefix(paramsCommand[1], "-v=");

        if(!topicRepository.containsVoteByTopicNameAndVoteName(topicName, voteName)) {
            log.warn("Topic \"{}\" and vote \"{}\" not found", topicName, voteName);
            ctx.writeAndFlush(MessageProviderUtil.getMessage("error.topic_vote.not_found", topicName, voteName));
            return;
        }

        Vote vote = topicRepository.findTopicByName(topicName).get().getVoteByName(voteName).get();

        StringBuilder response = new StringBuilder("Варианты ответа:\n");
        List<AnswerOption> options = vote.getAnswerOptions();
        for (int i = 0; i < options.size(); i++) {
            response.append(String.format("Вариант #%d: %s\n", (i + 1), options.get(i).getAnswer()));
        }
        response.append(MessageProviderUtil.getMessage("command.vote.success"));
        ctx.writeAndFlush(response.toString());

        log.info("Sending vote options to user and switching to VoteAnswerHandler");
        ctx.pipeline().removeLast();
        ctx.pipeline().addLast(handlerFactory.createVoteAnswerHandler(vote));
    }

    @Override
    public UserCommandType getType() {
        return UserCommandType.VOTE;
    }
}