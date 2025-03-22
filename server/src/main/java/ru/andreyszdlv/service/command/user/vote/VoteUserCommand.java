package ru.andreyszdlv.service.command.user.vote;

import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.andreyszdlv.enums.UserCommandType;
import ru.andreyszdlv.factory.HandlerFactory;
import ru.andreyszdlv.model.AnswerOption;
import ru.andreyszdlv.model.Topic;
import ru.andreyszdlv.model.Vote;
import ru.andreyszdlv.repo.TopicRepository;
import ru.andreyszdlv.service.HandlerService;
import ru.andreyszdlv.service.MessageService;
import ru.andreyszdlv.service.command.user.UserCommandHandler;
import ru.andreyszdlv.util.MessageProviderUtil;
import ru.andreyszdlv.util.ParamUtil;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class VoteUserCommand implements UserCommandHandler {

    private final TopicRepository topicRepository;
    private final HandlerFactory handlerFactory;
    private final HandlerService handlerService;
    private final MessageService messageService;

    @Override
    public void execute(ChannelHandlerContext ctx, String[] paramsCommand) {
        log.info("Executing vote command with params: {}", (Object) paramsCommand);

        String topicName = ParamUtil.extractValueByPrefix(paramsCommand[0], "-t=");
        String voteName = ParamUtil.extractValueByPrefix(paramsCommand[1], "-v=");

        Optional<Topic> topicOpt = topicRepository.findTopicByName(topicName);

        if(topicOpt.isEmpty() || !topicOpt.get().containsVoteByName(voteName)) {
            log.warn("Topic \"{}\" and vote \"{}\" not found", topicName, voteName);
            messageService.sendMessageByKey(ctx, "error.topic_vote.not_found", topicName, voteName);
            return;
        }

        Vote vote = topicOpt.get().getVoteByName(voteName).get();

        sendVoteOptions(ctx, vote);

        log.info("Sending vote options to user and switching to VoteAnswerHandler");
        handlerService.switchHandler(ctx, handlerFactory.createVoteAnswerHandler(vote));
    }

    @Override
    public UserCommandType getType() {
        return UserCommandType.VOTE;
    }

    private void sendVoteOptions(ChannelHandlerContext ctx, Vote vote){
        StringBuilder response = new StringBuilder("Варианты ответа:\n");
        List<AnswerOption> options = vote.getAnswerOptions();

        for (int i = 0; i < options.size(); i++) {
            response.append(String.format("Вариант #%d: %s\n", (i + 1), options.get(i).getAnswer()));
        }
        response.append(MessageProviderUtil.getMessage("command.vote.success"));

        ctx.writeAndFlush(response.toString());
    }
}