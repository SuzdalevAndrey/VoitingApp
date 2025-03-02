package ru.andreyszdlv.service.file;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.andreyszdlv.model.Topic;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JsonFileHandlerTest {

    @Mock
    ObjectMapper objectMapper;

    @InjectMocks
    JsonFileHandler jsonFileHandler;

    @Test
    void save_ShouldCallObjectMapperWriteValue_WhenValidArguments() throws IOException {
        String filename = "topics.json";
        Map<String, Topic> topics = new HashMap<>();
        Topic topic = mock(Topic.class);
        topics.put("topic", topic);

        jsonFileHandler.save(filename, topics);

        verify(objectMapper, times(1)).writeValue(new File(filename), topics);
        verifyNoMoreInteractions(objectMapper);
    }

    @Test
    void save_ShouldThrowIOException_WhenObjectMapperThrowsIOException() throws IOException {
        String filename = "topics.json";
        Map<String, Topic> topics = new HashMap<>();
        Topic topic = mock(Topic.class);
        topics.put("topic", topic);

        doThrow(IOException.class).when(objectMapper).writeValue(any(File.class), any(Map.class));

        assertThrows(IOException.class, () -> jsonFileHandler.save(filename, topics));

        verify(objectMapper, times(1)).writeValue(new File(filename), topics);
        verifyNoMoreInteractions(objectMapper);
    }
}