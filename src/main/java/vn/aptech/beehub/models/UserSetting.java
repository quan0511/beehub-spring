package vn.aptech.beehub.models;

import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="user_setting")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSetting {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name="user_id")
	private User user;
	
	@Enumerated(EnumType.STRING)
	@Column(length = 20)
	private ESettingType setting_type;
	
	@Nullable
	@OneToOne(mappedBy = "user_setting")
	private Post post;
	
	@Nullable
	private String setting_item;
	public UserSetting(User user, ESettingType type) {
		this.user = user;
		this.setting_type = type;
	}
}
