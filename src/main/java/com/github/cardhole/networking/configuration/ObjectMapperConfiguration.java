package com.github.cardhole.networking.configuration;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.github.cardhole.networking.domain.Message;
import com.github.cardhole.networking.domain.MessageHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.List;

@Configuration
public class ObjectMapperConfiguration {

    @Bean
    @Primary
    public ObjectMapper defaultObjectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public ObjectMapper outputObjectMapper() {
        final ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.addMixIn(Message.class, MessageTypeMixIn.class);

        return objectMapper;
    }

    @Bean
    public ObjectMapper inputObjectMapper(final List<MessageHandler<?>> messageTypeRegisters) {
        final ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.addMixIn(Message.class, MessageTypeMixIn.class);
        objectMapper.registerSubtypes(
                messageTypeRegisters.stream()
                        .map(messageTypeRegister -> new NamedType(messageTypeRegister.supportedMessage(),
                                messageTypeRegister.supportedMessage().getSimpleName()))
                        .toList()
                        .toArray(NamedType[]::new)
        );

        return objectMapper;
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = true)
    public abstract static class MessageTypeMixIn {
    }
}
