package vn.aptech.beehub.models;

import java.time.LocalDateTime;

import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="group_media")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupMedia {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@NotBlank
	private String media;
	@NotBlank
	private String media_type;
	private LocalDateTime create_at;
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;
	@ManyToOne
	@JoinColumn(name = "group_id")
	private Group group;

	@Nullable
	@OneToOne
	@JoinColumn(name = "post_id")
	private Post post;
	
	@Nullable
	@OneToOne(mappedBy = "image_group")
	private Group image_group;
	@Nullable
	@OneToOne(mappedBy = "background_group")
	private Group background_group;

	public GroupMedia (String media,
			String media_type,
			LocalDateTime create_at,
			User user,
			Group group,
			Post post) {
		this.media = media;
		this.media_type = media_type;
		this.create_at = create_at;
		this.user = user;
		this.group = group;
		this.post =post;
	}

}
