package vn.aptech.beehub.seeders;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import vn.aptech.beehub.models.ERole;
import vn.aptech.beehub.models.Role;
import vn.aptech.beehub.models.User;
import vn.aptech.beehub.repository.RoleRepository;
import vn.aptech.beehub.repository.UserRepository;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@Component
public class DatabaseSeeder {

    private Logger logger = LoggerFactory.getLogger(DatabaseSeeder.class);
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    PasswordEncoder passwordEncoder;

    @Autowired
    public DatabaseSeeder(
        UserRepository userRepository,
        RoleRepository roleRepository,
        PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @EventListener
    public void seed(ContextRefreshedEvent event) {
        seedRoles();
        seedAdmin();
    }

    private void seedRoles() {
        List<Role> roles = roleRepository.findAll();
        if (roles.isEmpty()) {
            Role admin = new Role();
            admin.setName(ERole.ROLE_ADMIN);
            roleRepository.save(admin);
            logger.info("Role admin saved");

            Role user = new Role();
            user.setName(ERole.ROLE_USER);
            roleRepository.save(user);
            logger.info("Role user saved");
        } else {
            logger.trace("Seeding is not required");
        }
    }

    private void seedAdmin() {
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            User admin = new User();
            
            admin.setUsername("admin");
            admin.setEmail("admin@gmail.com");
            admin.setPassword(passwordEncoder.encode("123456"));
            		
            Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN).get();
            HashSet<Role> roles = new HashSet<>();
            roles.add(adminRole);

            admin.setRoles(roles);
            userRepository.save(admin);
            logger.info("Admin saved");
        } else {
            logger.trace("Seeding is not required");
        }
    }
}
