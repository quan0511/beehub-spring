package vn.aptech.beehub.services;

import vn.aptech.beehub.models.Message;
import vn.aptech.beehub.models.MessageRecipient;
import vn.aptech.beehub.payload.request.SendMessageRequest;
import vn.aptech.beehub.payload.response.GetMessageResponse;

import java.util.List;

public interface MessageService {

    MessageRecipient sendMessageForUser(SendMessageRequest request) throws Exception;

    MessageRecipient sendMessageForGroup(SendMessageRequest request) throws Exception;

    List<GetMessageResponse> getChatMessagesByUserId(Long id) throws Exception;

    Message findMessageById();

    void deleteMessage();

}