package ru.andreyszdlv.validator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@PropertySource("classpath:application.properties")
public class CommandValidator {

    private final Set<String> commands;

    public CommandValidator(@Value("${user.commands.patterns}") String commandsString) {
        this.commands = Arrays.stream(commandsString.split(","))
                .map(String::trim)
                .collect(Collectors.toSet());
    }

    public boolean validate(String command) {
        return commands.stream().anyMatch(command::matches);
    }
}