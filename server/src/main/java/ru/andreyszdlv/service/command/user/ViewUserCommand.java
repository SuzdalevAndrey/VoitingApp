package ru.andreyszdlv.service.command.user;

import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.andreyszdlv.enums.UserCommandType;
import ru.andreyszdlv.repo.TopicRepository;
import ru.andreyszdlv.util.MessageProviderUtil;
import ru.andreyszdlv.util.ParamUtil;

@Slf4j
@Service
@RequiredArgsConstructor
public class ViewUserCommand implements UserCommandHandler {

    private final TopicRepository topicRepository;

    @Override
    public void execute(ChannelHandlerContext ctx, String[] paramsCommand) {
        log.info("Executing view command with parameters: {}", (Object) paramsCommand);

        switch (paramsCommand.length) {
            case 0 -> handleNoParams(ctx);
            case 1 -> handleSingleParam(ctx, paramsCommand);
            case 2 -> handleTwoParams(ctx, paramsCommand);
            default -> {
                log.warn("Invalid number of parameters for view command");
                ctx.writeAndFlush(MessageProviderUtil.getMessage("error.invalid_command"));
            }
        }
    }

    @Override
    public UserCommandType getType() {
        return UserCommandType.VIEW;
    }

    private void handleNoParams(ChannelHandlerContext ctx) {
        log.info("Fetching all topics");
        StringBuilder response = new StringBuilder();

        topicRepository.findAll().forEach((name, topic) ->
                response.append(String.format("\"%s\" (votes in topic=%d)\n", name, topic.countVotes()))
        );

        if (response.length() > 0) {
            response.setLength(response.length() - System.lineSeparator().length());
            ctx.writeAndFlush(response.toString());
            return;
        }

        ctx.writeAndFlush(MessageProviderUtil.getMessage("view.topics.not_found"));
    }

    private void handleSingleParam(ChannelHandlerContext ctx, String[] paramsCommand) {

        String topicName = ParamUtil.extractValueByPrefix(paramsCommand[0], "-t=");

        log.info("Fetching topic: {}", topicName);
        topicRepository.findTopicByName(topicName)
                .ifPresentOrElse(
                        topic -> {
                            log.info("Topic found: \"{}\"", topicName);
                            ctx.writeAndFlush(topic.toString());
                        },
                        () -> {
                            log.warn("Topic not found: \"{}\"", topicName);
                            ctx.writeAndFlush(MessageProviderUtil
                                    .getMessage("error.topic.not_found", topicName));
                        }
                );
    }

    private void handleTwoParams(ChannelHandlerContext ctx, String[] paramsCommand) {

        String topicName = ParamUtil.extractValueByPrefix(paramsCommand[0], "-t=");
        String voteName = ParamUtil.extractValueByPrefix(paramsCommand[1], "-v=");

        log.info("Fetching topic \"{}\" find vote \"{}\"", topicName, voteName);
        topicRepository.findTopicByName(topicName)
                .ifPresentOrElse(
                        topic -> topic.getVoteByName(voteName)
                                .ifPresentOrElse(
                                        vote -> {
                                            log.info("Vote found: \"{}\"", voteName);
                                            ctx.writeAndFlush(vote.toString());
                                        },
                                        () -> {
                                            log.warn("Vote not found: \"{}\"", voteName);
                                            ctx.writeAndFlush(MessageProviderUtil
                                                    .getMessage("error.vote.not_found", voteName));
                                        }
                                ),
                        () -> {
                            log.warn("Topic not found: \"{}\"", topicName);
                            ctx.writeAndFlush(MessageProviderUtil
                                    .getMessage("error.topic.not_found", topicName));
                        }
                );
    }
}