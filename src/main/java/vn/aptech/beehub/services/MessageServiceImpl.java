package vn.aptech.beehub.services;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import vn.aptech.beehub.models.Message;
import vn.aptech.beehub.models.MessageRecipient;
import vn.aptech.beehub.payload.request.SendMessageRequest;
import vn.aptech.beehub.payload.response.GetMessageResponse;
import vn.aptech.beehub.repository.GroupMemberRepository;
import vn.aptech.beehub.repository.MessageRecipientRepository;
import vn.aptech.beehub.repository.MessageRepository;
import vn.aptech.beehub.repository.UserRepository;
import vn.aptech.beehub.security.services.UserDetailsImpl;
import vn.aptech.beehub.services.impl.UserService;

import java.time.LocalDateTime;
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
    public MessageRecipient sendMessageForUser(SendMessageRequest request) throws Exception {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Message message = new Message();
        userRepository.findById(userDetails.getId()).ifPresentOrElse(message::setCreator,
                () -> {throw new UsernameNotFoundException("User not found");});
        message.setMessageBody(request.getContent());
        message.setCreateAt(LocalDateTime.now());
        message = messageRepository.save(message);

        MessageRecipient messageRecipient = new MessageRecipient();
        messageRecipient.setMessage(message);
        messageRecipient.setRead(false);
        userRepository.findById(request.getRecipientId()).ifPresentOrElse(messageRecipient::setRecipient,
                () -> {throw new UsernameNotFoundException("User not found");});
        messageRecipientRepository.save(messageRecipient);
        return messageRecipient;
    }

    @Override
    public MessageRecipient sendMessageForGroup(SendMessageRequest request) throws Exception {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Message message = new Message();
        userRepository.findById(userDetails.getId()).ifPresentOrElse(message::setCreator,
                () -> {throw new UsernameNotFoundException("User not found");});
        message.setMessageBody(request.getContent());

        MessageRecipient messageRecipient = new MessageRecipient();
        messageRecipient.setMessage(message);
        groupMemberRepository.findByGroup_id(request.getRecipientId()).forEach(groupMember -> {
//            messageRecipient.
        });

        return messageRecipient;
    }

    @Override
    public List<GetMessageResponse> getChatMessagesByUserId(Long id) throws Exception {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try {
            return messageRecipientRepository.findMessagesForUser(userDetails.getId(), id).stream().map(mr -> {
                var response = new GetMessageResponse();
                response.setId(mr.getMessage().getId());
                response.setMessageBody(mr.getMessage().getMessageBody());
                response.setCreatorId(mr.getMessage().getCreator().getId());
                response.setRead(mr.isRead());
                return response;
            }).toList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Message findMessageById() {
        return null;
    }

    @Override
    public void deleteMessage() {

    }
}
