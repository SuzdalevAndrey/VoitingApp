package ru.andreyszdlv.service.command.user.vote;

import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import ru.andreyszdlv.handler.VoteAnswerHandler;
import ru.andreyszdlv.model.AnswerOption;
import ru.andreyszdlv.model.Topic;
import ru.andreyszdlv.model.Vote;
import ru.andreyszdlv.repo.TopicRepository;
import ru.andreyszdlv.service.command.user.UserCommandHandler;
import ru.andreyszdlv.util.MessageProviderUtil;
import ru.andreyszdlv.util.ParameterUtils;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class VoteUserCommand implements UserCommandHandler {

    private final TopicRepository topicRepository;

    @Override
    public void execute(ChannelHandlerContext ctx, String[] paramsCommand) {
        if(paramsCommand.length != 2) {
            ctx.writeAndFlush(MessageProviderUtil.getMessage("error.command.vote.invalid"));
            return;
        }

        String topicName = ParameterUtils.extractValueByPrefix(paramsCommand[0], "-t=");
        String voteName = ParameterUtils.extractValueByPrefix(paramsCommand[1], "-v=");

        if(topicName == null || voteName == null) {
            ctx.writeAndFlush(MessageProviderUtil.getMessage("error.command.vote.invalid"));
            return;
        }

        if(topicName.isBlank() || voteName.isBlank()){
            ctx.writeAndFlush(MessageProviderUtil.getMessage("error.topic.name.vote.name.empty"));
            return;
        }

        Optional<Topic> topic = topicRepository.findTopicByName(topicName);

        if(topic.isEmpty()) {
            ctx.writeAndFlush(MessageProviderUtil.getMessage("error.topic.not_found", topicName));
            return;
        }

        Optional<Vote> vote = topic.get().getVoteByName(voteName);

        if(vote.isEmpty()) {
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

        ctx.pipeline().removeLast();
        ctx.pipeline().addLast(new VoteAnswerHandler(vote.get()));
    }
}