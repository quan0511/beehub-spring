package vn.aptech.beehub.models;

import java.time.LocalDateTime;
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
@Table(name="postReaction")
public class PostReaction {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	@ManyToOne
	@JoinColumn(name = "post_id", referencedColumnName = "id")
	private Post post;
	@ManyToOne
	@JoinColumn(name = "user_id", referencedColumnName = "id")
	private User user;
	@ManyToOne
	@JoinColumn(name = "postComment_id", referencedColumnName = "id")
	private PostComment postComment;
	private String reaction;
	private LocalDateTime createdAt;
}
