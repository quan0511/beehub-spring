package vn.aptech.beehub.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomSocketMessage {
    private String type;
    private String data;
}
