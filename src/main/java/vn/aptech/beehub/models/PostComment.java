package vn.aptech.beehub.models;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name="postComment")
public class PostComment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private String comment;
	@Column(name = "createdAt")
	private LocalDateTime createdAt;

	@ManyToOne
	@JoinColumn(name = "user_id", referencedColumnName = "id")
	private User user;
	@ManyToOne
	@JoinColumn(name = "post_id", referencedColumnName = "id")
	private Post post;
}
