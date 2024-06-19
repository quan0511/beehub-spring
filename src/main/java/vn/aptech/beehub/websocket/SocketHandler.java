package vn.aptech.beehub.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import vn.aptech.beehub.models.GroupMember;
import vn.aptech.beehub.payload.response.GetMessageResponse;
import vn.aptech.beehub.repository.GroupMemberRepository;
import vn.aptech.beehub.repository.GroupRepository;
import vn.aptech.beehub.repository.UserRepository;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class SocketHandler extends TextWebSocketHandler {

    private final UserRepository userRepository;

    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    private final ObjectMapper objectMapper;

    @Autowired
    private GroupMemberRepository groupMemberRepository;

    @Autowired
    SocketHandler(UserRepository userRepository, ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(SocketHandler.class);
    List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message)
            throws InterruptedException, IOException {
        handleMessage(session, message);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        LOGGER.info(session.getId());
        CustomSocketMessage messageSent = new CustomSocketMessage("PROFILE", session.getId());
        String json = objectMapper.writeValueAsString(messageSent);
        session.sendMessage(new TextMessage(json));
        sessions.add(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        LOGGER.info("{} disconnect", session.getId());
        sessions.remove(session);
    }

    private void handleMessage(WebSocketSession session, TextMessage message) throws IOException {
        CustomSocketMessage socketMessage = getCustomSocketMessage(message);

        switch (socketMessage.getType()) {
            case "PROFILE": {
                UserDetails userDetails = userDetailsService.loadUserByUsername(socketMessage.getData()); // email in this case
                Principal principal =  new UsernamePasswordAuthenticationToken(userDetails.getUsername(), null, userDetails.getAuthorities());
                session.getAttributes().put("principal", principal);
                session.sendMessage(getTextMessage("PROFILE_SUCCESS", "Setup profile success"));
                break;
            }
            case "SEND_USER_MESSAGE": {
                GetMessageResponse userMessage = getMessageResponse(socketMessage.getData());
                var ou = userRepository.findById(userMessage.getRecipientId());
                if (ou.isPresent()) {
                    var user = ou.get();
                    var email = user.getEmail(); // receiver email
                    for (WebSocketSession webSocketSession : sessions) { // loop through every socket
                        if (webSocketSession.isOpen()) {
                            Principal principal = null;
                            Object object = webSocketSession.getAttributes().get("principal");
                            if (object instanceof Principal) principal = (Principal) object;

                            if (principal != null && email.equals(principal.getName())) { // looking for a principal match the email of receiver
                                LOGGER.info("sent");
                                webSocketSession.sendMessage(getTextMessage("RECEIVE_USER_MESSAGE", userMessage)); // send
                            }
                        }
                    }
                }
                break;
            }
            case "SEND_GROUP_MESSAGE": {
                GetMessageResponse userMessage = getMessageResponse(socketMessage.getData());
                List<GroupMember> ogm = groupMemberRepository.findByGroup_id(userMessage.getRecipientId());
                ogm.forEach(gm -> {
                    var user = gm.getUser();
                    var userEmail = user.getEmail();
                    for (WebSocketSession webSocketSession : sessions) { // loop through every socket
                        if (webSocketSession.isOpen()) {
                            Principal principal = null;
                            Object object = webSocketSession.getAttributes().get("principal");
                            if (object instanceof Principal) principal = (Principal) object;

                            if (principal != null && userEmail.equals(principal.getName()) && !Objects.equals(user.getId(), userMessage.getCreatorId())) { // looking for a principal match the email of receiver
                                try {
                                    webSocketSession.sendMessage(getTextMessage("RECEIVE_GROUP_MESSAGE", userMessage)); // send
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                    }
                });
                break;
            }
            default:
                break;
        }
    }

    private TextMessage getTextMessage(String type, Object data) throws JsonProcessingException {
        CustomSocketMessage messageSent = new CustomSocketMessage(type, objectMapper.writeValueAsString(data));
        String json = objectMapper.writeValueAsString(messageSent);
        return new TextMessage(json);
    }

    private CustomSocketMessage getCustomSocketMessage(TextMessage message) throws JsonProcessingException {
        return objectMapper.readValue(message.getPayload(), CustomSocketMessage.class);
    }

    private GetMessageResponse getMessageResponse(String data) throws JsonProcessingException {
        return objectMapper.readValue(data, GetMessageResponse.class);
    }


}
