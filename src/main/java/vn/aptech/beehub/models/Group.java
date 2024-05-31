package vn.aptech.beehub.models;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;

import jakarta.annotation.Nullable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "groups")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Group {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotBlank
    @Size(max = 50)
    private String groupname;
	@Value("${some.key:true}")
	private boolean public_group;
	
	@Size(max = 250,min = 0)
	private String description;
	@Value("${some.key:true}")
	private boolean active;
	@NotNull
    private LocalDateTime created_at;
	
	@Nullable
    @OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name="image_id",referencedColumnName = "id")
	private GroupMedia image_group;
	
	@Nullable
    @OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name="background_id",referencedColumnName = "id")
	private GroupMedia background_group;
	

	@OneToMany(mappedBy = "group",cascade =  CascadeType.REMOVE,fetch = FetchType.EAGER,orphanRemoval = true)
	private List<Post> posts;
	
	
	@OneToMany(mappedBy = "group",cascade =  CascadeType.REMOVE,fetch = FetchType.EAGER,orphanRemoval = true)
	private List<GroupMedia> group_medias;
	
	public Group(
			String groupname,
			String description	
			){
		this.groupname = groupname;
		this.description = description;
		this.created_at = LocalDateTime.now();
	}
}
