package vn.aptech.beehub.models;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name="post")
@JsonIgnoreProperties({"comments", "reactions", "likes"})//cho chỉ định các thuộc tính sẽ bị bỏ qua trong quá trình tuần tự hóa.
public class Post {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private String text;
	private String media;
	private String background;
	private String color;
	private LocalDateTime createdAt;
	@ManyToOne
	@JoinColumn(name = "user_id", referencedColumnName = "id")
	private User user;
	@OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostComment> comments;
    
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostReaction> reactions;
    
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LikeUser> likes;
}
