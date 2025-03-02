package ru.andreyszdlv.validator;

import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.andreyszdlv.repo.UserRepository;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationValidatorTest {

    @Mock
    UserRepository userRepository;

    @Mock
    Channel channel;

    @Mock
    ChannelId channelId;

    @InjectMocks
    AuthenticationValidator authenticationValidator;

    String stringChannelId = "ChannelId";

    @Test
    void isAuthenticated_ReturnTrue_WhenUserIsAuthenticated() {
        when(channel.id()).thenReturn(channelId);
        when(channelId.asLongText()).thenReturn(stringChannelId);
        when(userRepository.containsUserByChannelId(stringChannelId)).thenReturn(true);

        boolean response = authenticationValidator.isAuthenticated(channel);

        assertTrue(response);
        verify(channel, times(1)).id();
        verify(channelId, times(1)).asLongText();
        verify(userRepository, times(1)).containsUserByChannelId(stringChannelId);
        verifyNoMoreInteractions(userRepository, channel, channelId);
    }

    @Test
    void isAuthenticated_ReturnFalse_WhenUserIsNotAuthenticated() {
        when(channel.id()).thenReturn(channelId);
        when(channelId.asLongText()).thenReturn(stringChannelId);
        when(userRepository.containsUserByChannelId(stringChannelId)).thenReturn(false);

        boolean response = authenticationValidator.isAuthenticated(channel);

        assertFalse(response);
        verify(channel, times(1)).id();
        verify(channelId, times(1)).asLongText();
        verify(userRepository, times(1)).containsUserByChannelId(stringChannelId);
        verifyNoMoreInteractions(userRepository, channel, channelId);
    }
}