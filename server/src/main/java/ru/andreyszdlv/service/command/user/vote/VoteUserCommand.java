package ru.andreyszdlv.service.command.user.vote;

import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.andreyszdlv.handler.VoteAnswerHandler;
import ru.andreyszdlv.model.AnswerOption;
import ru.andreyszdlv.model.Topic;
import ru.andreyszdlv.model.Vote;
import ru.andreyszdlv.repo.TopicRepository;
import ru.andreyszdlv.service.command.user.UserCommandHandler;
import ru.andreyszdlv.util.MessageProviderUtil;
import ru.andreyszdlv.util.ParameterUtil;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class VoteUserCommand implements UserCommandHandler {

    private final TopicRepository topicRepository;

    @Override
    public void execute(ChannelHandlerContext ctx, String[] paramsCommand) {
        log.info("Executing vote command with params: {}", (Object) paramsCommand);

        if(paramsCommand.length != 2) {
            log.warn("Invalid number of parameters for vote command");
            ctx.writeAndFlush(MessageProviderUtil.getMessage("error.command.vote.invalid"));
            return;
        }

        String topicName = ParameterUtil.extractValueByPrefix(paramsCommand[0], "-t=");
        String voteName = ParameterUtil.extractValueByPrefix(paramsCommand[1], "-v=");

        if(topicName == null || voteName == null) {
            log.warn("Vote command options invalid. Expected: -t=TopicName -v=VoteName. Received: {}",
                    (Object) paramsCommand);
            ctx.writeAndFlush(MessageProviderUtil.getMessage("error.command.vote.invalid"));
            return;
        }

        if(topicName.isBlank() || voteName.isBlank()){
            log.warn("Topic name or vote name is empty");
            ctx.writeAndFlush(MessageProviderUtil.getMessage("error.topic.name.vote.name.empty"));
            return;
        }

        Optional<Topic> topic = topicRepository.findTopicByName(topicName);

        if(topic.isEmpty()) {
            log.warn("Topic \"{}\" not found", topicName);
            ctx.writeAndFlush(MessageProviderUtil.getMessage("error.topic.not_found", topicName));
            return;
        }

        Optional<Vote> vote = topic.get().getVoteByName(voteName);

        if(vote.isEmpty()) {
            log.warn("Vote \"{}\" not found in topic \"{}\"", voteName, topicName);
            ctx.writeAndFlush(MessageProviderUtil.getMessage("error.vote.not_found", voteName));
            return;
        }

        StringBuilder response = new StringBuilder("Варианты ответа:\n");
        List<AnswerOption> options = vote.get().getAnswerOptions();
        for (int i = 0; i < options.size(); i++) {
            response.append(String.format("Вариант #%d: %s\n", (i + 1), options.get(i).getAnswer()));
        }
        response.append(MessageProviderUtil.getMessage("command.vote.success"));
        ctx.writeAndFlush(response.toString());

        log.info("Sending vote options to user and switching to VoteAnswerHandler");
        ctx.pipeline().removeLast();
        ctx.pipeline().addLast(new VoteAnswerHandler(vote.get()));
    }
}