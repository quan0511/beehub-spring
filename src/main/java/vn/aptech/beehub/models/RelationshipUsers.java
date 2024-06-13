package vn.aptech.beehub.models;

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
import lombok.ToString;

@Entity
@Table(name="relationship_users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RelationshipUsers {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@ManyToOne
	@JoinColumn(name = "user1_id")
	private User user1;
	@ManyToOne
	@JoinColumn(name = "user2_id")
	private User user2;
	@NotNull
	@Enumerated(EnumType.STRING)
    @Column(length = 20)
	private ERelationshipType type;

	public RelationshipUsers(
			User user1, User user2, ERelationshipType type
			) {
		this.user1 = user1;
		this.user2 = user2;
		this.type = type;
	}
}
