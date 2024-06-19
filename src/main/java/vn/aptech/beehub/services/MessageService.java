package vn.aptech.beehub.services;

import vn.aptech.beehub.payload.request.SendMessageRequest;
import vn.aptech.beehub.payload.response.GetMessageResponse;

import java.util.List;

public interface MessageService {

    GetMessageResponse sendMessageForUser(SendMessageRequest request);

    GetMessageResponse sendMessageForGroup(SendMessageRequest request);

    List<GetMessageResponse> getChatMessagesByUserId(Long id);

    List<GetMessageResponse> getChatMessagesByGroupId(Long id);

}