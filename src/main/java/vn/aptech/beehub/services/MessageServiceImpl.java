package vn.aptech.beehub.services;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import vn.aptech.beehub.models.*;
import vn.aptech.beehub.payload.request.SendMessageRequest;
import vn.aptech.beehub.payload.response.GetMessageResponse;
import vn.aptech.beehub.repository.*;
import vn.aptech.beehub.security.services.UserDetailsImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class MessageServiceImpl implements  MessageService {
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final MessageRecipientRepository messageRecipientRepository;
    private final GroupMemberRepository groupMemberRepository;

    public MessageServiceImpl(UserRepository userRepository, MessageRepository messageRepository, MessageRecipientRepository messageRecipientRepository, GroupMemberRepository groupMemberRepository) {
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
        this.messageRecipientRepository = messageRecipientRepository;
        this.groupMemberRepository = groupMemberRepository;
    }

    @Override
    public GetMessageResponse sendMessageForUser(SendMessageRequest request) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Message message = new Message();
        userRepository.findById(userDetails.getId()).ifPresentOrElse(message::setCreator,
                () -> {throw new UsernameNotFoundException("Sender not found");});
        message.setMessageBody(request.getContent());
        message.setCreateAt(LocalDateTime.now());
        message = messageRepository.save(message);

        MessageRecipient messageRecipient = new MessageRecipient();
        messageRecipient.setMessage(message);
        messageRecipient.setRead(false);
        userRepository.findById(request.getRecipientId()).ifPresentOrElse(messageRecipient::setRecipient,
                () -> {throw new UsernameNotFoundException("Recipient not found");});
        messageRecipient = messageRecipientRepository.save(messageRecipient);

        return GetMessageResponse.builder()
                .id(message.getId())
                .messageBody(message.getMessageBody())
                .createdAt(message.getCreateAt())
                .creatorAvatar(message.getCreator().getImage() != null ?
                        message.getCreator().getImage().getMedia() :
                        "female".equals(message.getCreator().getGender()) ?
                                "female" :
                                "male")
                .creatorId(message.getCreator().getId())
                .creatorName(message.getCreator().getUsername())
                .recipientId(messageRecipient.getRecipient().getId())
                .build();
    }

    @Override
    public GetMessageResponse sendMessageForGroup(SendMessageRequest request) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Message message = new Message();
        userRepository.findById(userDetails.getId()).ifPresentOrElse(message::setCreator,
                () -> {throw new UsernameNotFoundException("Sender not found");});
        message.setMessageBody(request.getContent());
        message.setCreateAt(LocalDateTime.now());
        message = messageRepository.save(message);

        List<MessageRecipient> messageRecipients = new ArrayList<>();
        Message finalMessage = message;
        groupMemberRepository.findByGroup_id(request.getRecipientId()).forEach(groupMember -> {
            MessageRecipient messageRecipient = new MessageRecipient();
            messageRecipient.setMessage(finalMessage);
            messageRecipient.setRecipientGroup(groupMember);
            messageRecipient.setRead(false);
            messageRecipients.add(messageRecipient);
        });
        messageRecipientRepository.saveAll(messageRecipients);

        return GetMessageResponse.builder()
                .id(message.getId())
                .messageBody(message.getMessageBody())
                .createdAt(message.getCreateAt())
                .creatorAvatar(message.getCreator().getImage() != null ?
                        message.getCreator().getImage().getMedia() :
                        "female".equals(message.getCreator().getGender()) ?
                                "female" :
                                "male")
                .creatorId(message.getCreator().getId())
                .creatorName(message.getCreator().getUsername())
                .recipientId(request.getRecipientId()) // group id
                .build();
    }

    @Override
    public List<GetMessageResponse> getChatMessagesByUserId(Long id) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try {
            return messageRecipientRepository.findAllByUserVsUser(userDetails.getId(), id)
                    .stream()
                    .map(this::mapToGetMessageResponse)
                    .toList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    @Override
    public List<GetMessageResponse> getChatMessagesByGroupId(Long groupId) {
        try {
            return messageRecipientRepository.findAllByGroupIdOrderByCreatedAt(groupId)
                    .stream()
                    .map(this::mapToGetMessageResponse)
                    .toList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private GetMessageResponse mapToGetMessageResponse(MessageRecipient mr) {
        var response = new GetMessageResponse();
        response.setId(mr.getMessage().getId());
        response.setMessageBody(mr.getMessage().getMessageBody());
        response.setCreatorId(mr.getMessage().getCreator().getId());
        var creator = mr.getMessage().getCreator();
        if (creator != null) {
            response.setCreatorName(creator.getUsername());
            response.setCreatorAvatar(
                    creator.getImage() != null ?
                            creator.getImage().getMedia() :
                    "female".equals(creator.getGender()) ?
                            "female" :
                            "male");
        }
        if (mr.getRecipient() != null) {
            response.setRecipientId(mr.getRecipient().getId());
        } else if (mr.getRecipientGroup() != null) {
            response.setRecipientId(mr.getRecipientGroup().getId());
        }
        response.setCreatedAt(mr.getMessage().getCreateAt());
        response.setRead(mr.isRead());
        return response;
    }
}
