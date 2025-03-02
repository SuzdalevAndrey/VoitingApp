package ru.andreyszdlv.service.command.user;

import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import ru.andreyszdlv.model.Topic;
import ru.andreyszdlv.model.Vote;
import ru.andreyszdlv.repo.TopicRepository;
import ru.andreyszdlv.repo.UserRepository;
import ru.andreyszdlv.util.MessageProviderUtil;
import ru.andreyszdlv.util.ParameterUtils;

import java.util.Optional;

@RequiredArgsConstructor
public class DeleteUserCommand implements UserCommandHandler {

    private final TopicRepository topicRepository;

    private final UserRepository userRepository;

    @Override
    public void execute(ChannelHandlerContext ctx, String[] paramsCommand) {

        if(paramsCommand.length != 2) {
            ctx.writeAndFlush(MessageProviderUtil.getMessage("error.command.delete.invalid"));
            return;
        }

        String topicName = ParameterUtils.extractValueByPrefix(paramsCommand[0], "-t=");
        String voteName = ParameterUtils.extractValueByPrefix(paramsCommand[1], "-v=");

        if(topicName == null || voteName == null) {
            ctx.writeAndFlush(MessageProviderUtil.getMessage("error.command.delete.invalid"));
            return;
        }

        if(topicName.isBlank() || voteName.isBlank()) {
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

        if(!vote.get().getAuthorName()
                .equals(userRepository.findUserByChannelId(ctx.channel().id().asLongText()))){
            ctx.writeAndFlush(MessageProviderUtil.getMessage("error.vote.no_delete", voteName));
            return;
        }

        topicRepository.removeVote(topicName, voteName);
        ctx.writeAndFlush(MessageProviderUtil.getMessage("command.delete.success", voteName));
    }
}