package com.github.cardhole.networking.configuration;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.github.cardhole.networking.domain.Message;
import com.github.cardhole.networking.domain.MessageTypeRegister;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ObjectMapperConfiguration {

    @Bean
    public ObjectMapper objectMapper(final List<MessageTypeRegister> messageTypeRegisters) {
        final ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.addMixIn(Message.class, MessageTypeMixIn.class);
        objectMapper.registerSubtypes(
                messageTypeRegisters.stream()
                        .map(messageTypeRegister -> new NamedType(messageTypeRegister.domainClass(), messageTypeRegister.type()))
                        .toList()
                        .toArray(NamedType[]::new)
        );

        return objectMapper;
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = true)
    public abstract static class MessageTypeMixIn {
    }
}
