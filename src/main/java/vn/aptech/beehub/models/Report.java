package vn.aptech.beehub.models;

import java.time.LocalDateTime;

import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
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
@Table(name = "reports")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Report {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@ManyToOne
	@JoinColumn(name = "sender_id")
	private User sender;
	
	@Nullable
	@ManyToOne
	@JoinColumn(name= "target_user_id")
	private User target_user;
	
	@Nullable
	@ManyToOne
	@JoinColumn(name= "target_group_id")
	private Group target_group;
	
	@Nullable
	@ManyToOne
	@JoinColumn(name= "target_post_id",referencedColumnName="id")
	private Post target_post;
	
	@ManyToOne
	@JoinColumn(name= "type_id")
	private ReportTypes report_type;
	
	@Nullable
	private String add_description;
	
	@NotNull
	private LocalDateTime create_at;
	
	@NotNull
	private LocalDateTime update_at;
	
	public Report(User sender, 
			Group target_group, 
			Post target_post,
			ReportTypes type, 
			String add_des, 
			LocalDateTime create_at,
			LocalDateTime update_at) {
		this.sender = sender;
		this.target_group = target_group;
		this.target_post = target_post;
		this.report_type =type;
		this.add_description =add_des;
		this.create_at = create_at;
		this.update_at = update_at;
	}
}
