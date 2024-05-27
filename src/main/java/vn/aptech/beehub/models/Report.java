package vn.aptech.beehub.models;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
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
	
	@Null
	@ManyToOne
	@JoinColumn(name= "target_user_id")
	private User target_user;
	
	@Null
	@ManyToOne
	@JoinColumn(name= "target_group_id")
	private Group target_group;
	
	@Null
	@ManyToOne
	@JoinColumn(name= "target_post_id")
	private Post target_post;
	
	@ManyToOne
	@JoinColumn(name= "type_id")
	private ReportTypes report_type;
	
	@Null
	private String add_description;
	
	@NotNull
	private LocalDateTime create_at;
	
	@NotNull
	private LocalDateTime update_at;
}
