package vn.aptech.beehub.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.aspectj.bridge.Message;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import vn.aptech.beehub.dto.GroupDto;
import vn.aptech.beehub.dto.GroupMediaDto;
import vn.aptech.beehub.dto.GroupMemberDto;
import vn.aptech.beehub.dto.PostDto;
import vn.aptech.beehub.models.*;
import vn.aptech.beehub.payload.request.CreateUserRequest;
import vn.aptech.beehub.payload.response.*;
import vn.aptech.beehub.repository.*;
import vn.aptech.beehub.security.services.UserDetailsImpl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@Tag(name = "Admin")
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = "http://localhost:5173", maxAge = 3600)
public class AdminController {

    @Autowired
    ReportRepository reportRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    GroupRepository groupRepository;

    @Autowired
    GroupMemberRepository groupMemberRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    RelationshipUsersRepository relationshipUsersRepository;

    /*Profile*/
    @GetMapping("/profile")
    public ResponseEntity<AdminProfile> getProfile() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = userDetails.getId();
        var user = userRepository.findById(userId);
        var admin = new AdminProfile();
        if (user.isPresent()) {
            admin.setUsername(user.get().getUsername());
            admin.setFullName(user.get().getFullname());
            admin.setEmail(user.get().getEmail());
            admin.setCreatedAt(user.get().getCreate_at());
            return ResponseEntity.ok(admin);
        } else {
            throw new UsernameNotFoundException("User not found");
        }
    }

    /*Dashboard*/
    @GetMapping("/dashboard")
    public ResponseEntity<DashboardResponse> getDashboard() {
        int numOfUsers = userRepository.findAll().size();
        int numOfGroups = groupRepository.findAll().size();
        int numOfPosts = postRepository.findAll().size();
        int numOfReports = reportRepository.findAll().size();
        return ResponseEntity.ok(new DashboardResponse(numOfUsers, numOfGroups, numOfPosts, numOfReports));
    }

    /*Reports*/

    @GetMapping("/reports")
    public ResponseEntity<List<ReportResponse>> getReports() {
        return ResponseEntity.ok(reportRepository.findAll().stream().map(r -> {
            ReportResponse report = ReportResponse.builder()
                    .id(r.getId())
                    .reporter(r.getSender().getUsername())
                    .type(r.getReport_type().getTitle())
                    .timestamp(r.getCreate_at())
                    .build();
            if (r.getTarget_user() != null) {
                report.setReportedCase(r.getTarget_user().getUsername());
                report.setCaseType("user");
                report.setStatus(r.getTarget_user().is_active() ? "active" : "inactive");
            } else if (r.getTarget_group() != null) {
                report.setReportedCase(r.getTarget_group().getGroupname());
                report.setCaseType("group");
                report.setStatus(r.getTarget_group().isActive() ? "active" : "inactive");
            }else {
                report.setReportedCase(r.getTarget_post().getId().toString());
                report.setCaseType("post");
                report.setStatus("blocked");
            }
            return report;
        }).toList());
    }

    @GetMapping("/reports/user/{id}")
    public ResponseEntity<UserResponse> getUserReports(@PathVariable Long id) {
        return ResponseEntity.ok(null);
    }

    @GetMapping("/reports/post/{id}")
    public ResponseEntity<?> getPostReports(@PathVariable Long id) {
        return ResponseEntity.ok(null);
    }

    @GetMapping("/reports/group/{id}")
    public ResponseEntity<?> getGroupReports(@PathVariable Long id) {
        return ResponseEntity.ok(null);
    }

    @DeleteMapping("/reports/{id}")
    public ResponseEntity<?> deleteReport(@PathVariable Long id) {
        return ResponseEntity.ok(null);
    }

    /*Users*/
    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getUsers() {
        return ResponseEntity.ok(userRepository.findAll().stream().map(u ->
                UserResponse.builder()
                        .id(u.getId())
                        .username(u.getUsername())
                        .email(u.getEmail())
                        .fullName(u.getFullname())
                        .gender(u.getGender())
                        .noOfPosts(u.getPosts().size())
                        .noOfFriends(userRepository.findRelationship(u.getId(), ERelationshipType.FRIEND.toString()).size())
                        .role(u.getRoles().stream().findFirst().get().getName().name())
                        .status(u.is_active()?"active":"inactive")
                        .build()).toList());
    }

    @GetMapping("/users/{username}")
    public ResponseEntity<UserResponse> getUser(@PathVariable String username) {
        var user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            var u = user.get();
            var role = u.getRoles().stream().findFirst().get().getName().name();
            return ResponseEntity.ok(UserResponse.builder()
                    .id(u.getId())
                    .username(u.getUsername())
                    .email(u.getEmail())
                    .gender(u.getGender())
                    .fullName(u.getFullname())
                    .role(u.getRoles().stream().findFirst().get().toString())
                    .noOfPosts(u.getPosts().size())
                    .noOfFriends(userRepository.findRelationship(u.getId(), ERelationshipType.FRIEND.toString()).size())
                    .role(role)
                    .status(u.is_active()?"active":"inactive")
                    .avatar(u.getImage() != null ? u.getImage().getMedia() : "")
                    .gallery(u.getGalleries().stream().map(Gallery::getMedia).toList())
                    .build());
        } else {
            throw new UsernameNotFoundException("Username not found: " + username);
        }
    }

    @PostMapping("/users")
    public ResponseEntity<?> createUser(@RequestBody CreateUserRequest userRequest) {
        if (userRepository.existsByUsername(userRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Username is already taken!"));
        }

        if (userRepository.existsByEmail(userRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Email is already in use!"));
        }

        // Create new user's account
        User user = User.builder()
                .username(userRequest.getUsername())
                .email(userRequest.getEmail())
                .password(encoder.encode(userRequest.getPassword()))
                .build();

        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        roles.add(userRole);
        user.setRoles(roles);

        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User created successfully!"));
    }

    @PatchMapping("/users/role/{id}")
    public ResponseEntity<?> updateUser() {
        return ResponseEntity.ok(null);
    }

    @PatchMapping("/users/ban/{id}")
    public ResponseEntity<?> banUser() {
        return ResponseEntity.ok(null);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser() {
        return ResponseEntity.ok(null);
    }

    /*Posts*/
    @GetMapping("/posts")
    public ResponseEntity<List<PostResponse>> getPosts() {
        return ResponseEntity.ok(postRepository.findAll().stream().map(p -> PostResponse.builder()
                .id(p.getId())
                .creator(p.getUser().getUsername())
                .timestamp(p.getCreate_at())
                .status(p.is_blocked() ? "blocked" : "active")
                .build())
                .toList());
    }

    @GetMapping("/posts/{id}")
    public ResponseEntity<?> getPost(@PathVariable Long id) {
        var op = postRepository.findById(id);
        if (op.isPresent()) {
            return ResponseEntity.ok(op.map(p -> modelMapper.map(p, PostDto.class)));
        }
        return ResponseEntity.notFound().build();
    }

    @PatchMapping("/posts/{id}")
    public ResponseEntity<?> banPost() {
        return ResponseEntity.ok(null);
    }

    /*Groups*/

    @GetMapping("/groups")
    public ResponseEntity<List<GroupDto>> getGroups() {
        return ResponseEntity.ok(groupRepository.findAll().stream().map(g -> {
            var group = new GroupDto();
            group.setId(g.getId());
            group.setPublic_group(g.isPublic_group());
            group.setGroupname(g.getGroupname());
            group.setMember_count(groupMemberRepository.findByGroup_id(group.getId()).size());
            group.setActive(g.isActive());
            group.setCreated_at(g.getCreated_at());
            var image = g.getImage_group();
            if (image != null)  group.setImage_group(image.getMedia());
            return group;
        }).toList());
    }

    @GetMapping("/groups/{id}")
    public ResponseEntity<GroupDto> getGroup(@PathVariable Long id) throws Exception {
        var optgroup = groupRepository.findById(id);
        if (optgroup.isPresent()) {
            var group = optgroup.get();
            var g = new GroupDto();
            g.setId(group.getId());
            g.setImage_group(group.getImage_group() != null ? group.getImage_group().getMedia() : "");
            g.setGroupname(group.getGroupname());
            g.setGroup_medias(group.getGroup_medias().stream().map(m -> modelMapper.map(m, GroupMediaDto.class)).toList());
            g.setGroup_members(groupMemberRepository.findByGroup_id(group.getId()).stream().map(gm -> modelMapper.map(gm, GroupMemberDto.class)).toList());
            g.setPost_count(group.getPosts().size());
            g.setActive(group.isActive());
            return ResponseEntity.ok(g);
        } else {
            throw new Exception("Group not found");
        }
    }

    /*Shop*/
}
