package vn.aptech.beehub.models;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.annotation.Nullable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="posts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"comments", "reactions", "likes","gallerys","reports_of_post","shares"})//cho chỉ định các thuộc tính sẽ bị bỏ qua trong quá trình tuần tự hóa.
public class Post {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name="setting_id",referencedColumnName = "id")
	private UserSetting user_setting;
	
	@ManyToOne(cascade = CascadeType.REMOVE)
	@JoinColumn(name = "user_id")
	private User user;
	
	@ManyToOne(cascade = CascadeType.REMOVE)
	@JoinColumn(name = "postshare")
	private Post postshare;

	@Nullable
	@ManyToOne(cascade = CascadeType.REMOVE)
	@JoinColumn(name = "group_id")
	private Group group;
	
	@Nullable
	@OneToOne(mappedBy = "post", cascade = CascadeType.REMOVE)
	private Gallery media;
	
	@Nullable
	@OneToOne(mappedBy = "post", cascade = CascadeType.REMOVE)
	private GroupMedia group_media;
	
	@OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostComment> comments;
    
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostReaction> reactions;
    
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LikeUser> likes;
    
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Gallery> gallerys;
	private String text;
	@Nullable
	private String color;
	@Nullable
	private String background;
	@NotNull
	private LocalDateTime create_at;
	private String medias;
	private Boolean share;
	private LocalDateTime timeshare;
	public Post(
			String text,
			User user,
			LocalDateTime create_at,
			Group group
			) {
		this.text = text;
		this.user = user;
		this.create_at = create_at;		
		this.user_setting = new UserSetting(user,ESettingType.PUBLIC);
		this.group = group;
		
	}
	public Post(
			String text,
			User user,
			LocalDateTime create_at,
			ESettingType type
			) {
		this.text = text;
		this.user = user;
		this.create_at = create_at;		
		this.user_setting = new UserSetting(user,type);
	}
	public Post(
			String text,
			User user,
			LocalDateTime create_at,
			ESettingType type,
			String color,
			String background
			) {
		this.text = text;
		this.user = user;
		this.create_at = create_at;		
		this.user_setting = new UserSetting(user,type);
		this.color = color;
		this.background = background;
	}
	
	@Override
	public String toString() {
		return "Post "+this.id+": "+this.text+"\t User: "+this.user.getUsername();
	}

}