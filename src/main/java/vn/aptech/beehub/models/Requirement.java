package vn.aptech.beehub.models;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;

import jakarta.annotation.Nullable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "requirements")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Requirement {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@ManyToOne
	@JoinColumn(name = "sender_id")
	private User sender;
	@Nullable
	@ManyToOne
	@JoinColumn(name = "receiver_id")
	private User receiver;
	@Nullable
	@ManyToOne
	@JoinColumn(name = "group_id")
	private Group group_receiver;
	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(length = 20)
	private ERequirement type;
	@Value("${some.key:true}")
	private boolean is_accept;
	@NotNull
	private LocalDateTime create_at;
	
	public Requirement(User sender, User receiver) {
		this.sender = sender;
		this.receiver = receiver;
		this.type  = ERequirement.ADD_FRIEND;
		this.is_accept = false;
		this.create_at = LocalDateTime.now();
	}
	public Requirement(User sender,Group group_receiver) {
		this.sender = sender;
		this.group_receiver = group_receiver;
		this.type = ERequirement.JOIN_GROUP;
		this.is_accept = false;
		this.create_at = LocalDateTime.now();
	}
}
