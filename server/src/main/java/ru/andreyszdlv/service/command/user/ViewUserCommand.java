package ru.andreyszdlv.service.command.user;

import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.andreyszdlv.enums.UserCommandType;
import ru.andreyszdlv.repo.TopicRepository;
import ru.andreyszdlv.service.MessageService;
import ru.andreyszdlv.util.ParamUtil;

@Slf4j
@Service
@RequiredArgsConstructor
public class ViewUserCommand implements UserCommandHandler {

    private final TopicRepository topicRepository;
    private final MessageService messageService;

    @Override
    public void execute(ChannelHandlerContext ctx, String[] paramsCommand) {
        log.info("Executing view command with parameters: {}", (Object) paramsCommand);

        switch (paramsCommand.length) {
            case 0 -> handleNoParams(ctx);
            case 1 -> handleSingleParam(ctx, paramsCommand);
            case 2 -> handleTwoParams(ctx, paramsCommand);
            default -> {
                log.warn("Invalid number of parameters for view command");
                messageService.sendMessageByKey(ctx, "error.invalid_command");
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
                response.append(String.format("\"%s\" (votes in topic=%d)\n", name, topic.countVotes())));

        if (!response.isEmpty()) {
            response.setLength(response.length() - System.lineSeparator().length());
            messageService.sendMessage(ctx, response.toString());
            return;
        }

        messageService.sendMessageByKey(ctx, "view.topics.not_found");
    }

    private void handleSingleParam(ChannelHandlerContext ctx, String[] paramsCommand) {

        String topicName = ParamUtil.extractValueByPrefix(paramsCommand[0], "-t=");

        log.info("Fetching topic: {}", topicName);
        topicRepository.findTopicByName(topicName)
                .ifPresentOrElse(
                        topic -> {
                            log.info("Topic found: \"{}\"", topicName);
                            messageService.sendMessage(ctx, topic.toString());
                        },
                        () -> {
                            log.warn("Topic not found: \"{}\"", topicName);
                            messageService.sendMessageByKey(ctx,
                                    "error.topic.not_found",
                                    topicName);
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
                                            messageService.sendMessage(ctx, vote.toString());
                                        },
                                        () -> {
                                            log.warn("Vote not found: \"{}\"", voteName);
                                            messageService.sendMessageByKey(ctx,
                                                    "error.vote.not_found",
                                                    voteName);
                                        }
                                ),
                        () -> {
                            log.warn("Topic not found: \"{}\"", topicName);
                            messageService.sendMessageByKey(ctx,
                                    "error.topic.not_found",
                                    topicName);
                        }
                );
    }
}