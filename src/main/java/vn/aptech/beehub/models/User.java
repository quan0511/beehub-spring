package vn.aptech.beehub.models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;

import jakarta.annotation.Nullable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;


@Entity
@Table(name = "users",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = "username"),
            @UniqueConstraint(columnNames = "email")
    })

@Data
@Builder
@AllArgsConstructor 
@NoArgsConstructor
public class User {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 20)
    private String username;

    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    @NotBlank
    @Size(max = 120)
    private String password;
    
//    @NotBlank
    @Size(max= 50)
    private String fullname;
    
//    @NotNull
    private String gender;
    
    @Nullable
    @OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name="image_id",referencedColumnName = "id")
    private Gallery image;
    @Nullable
    @OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name="background_id",referencedColumnName = "id")
    private Gallery background;
    
    @Nullable
    private String bio;
    @Nullable
    private LocalDate birthday;
    
    @Nullable
    private String google_id;
    
    @Value("${some.key:false}")
    private boolean email_verified;
    
    @Nullable
    private String email_verification_token;
    
//    @NotBlank
    @Pattern(regexp = "^(84|0[35789])+([0-9]{8})$",message = "Phone is invalid")
    private String phone;
    
    @Value("${some.key:true}")
    private boolean is_active;
    
    private LocalDateTime active_at;
    
    private LocalDateTime create_at;
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    private List<GroupMember> group_joined;
    
    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    private List<Requirement> requirements;
    
    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    private List<Requirement> sent_requirement;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    private List<Gallery> galleries;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    private List<Post> posts;
    
    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    private List<Report> reports_from_user;
    
    @OneToMany(mappedBy = "target_user", cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    private List<Report> reports_to_user;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    private List<UserSetting> user_settings;
    
    public User(String username, 
    		String email, 
    		String password,
    		String fullname, 
    		String gender, 
    		String phone, 
    		LocalDateTime create_at,
    		LocalDateTime active_at) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.fullname = fullname;
        this.gender = gender;
        this.phone = phone;
        this.create_at = create_at;
        this.active_at = active_at;
    }
    @Override
    public String toString() {
    	return "User "+this.id+"\tFullname: "+this.fullname+"\tusername:"+this.username+"\tgender: "+this.gender+"\tphone: "+this.phone+"\ncreate at: "+this.create_at+"\tis active: "+this.is_active+"\temail: "+this.email+"\temail_verified"+this.email_verified;
    }
}
