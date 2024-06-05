package vn.aptech.beehub.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.aspectj.bridge.Message;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import vn.aptech.beehub.dto.GroupDto;
import vn.aptech.beehub.dto.PostDto;
import vn.aptech.beehub.models.ERelationshipType;
import vn.aptech.beehub.models.Gallery;
import vn.aptech.beehub.payload.response.MessageResponse;
import vn.aptech.beehub.payload.response.ReportResponse;
import vn.aptech.beehub.payload.response.UserResponse;
import vn.aptech.beehub.repository.*;

import java.util.List;

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
    private PostRepository postRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    RelationshipUsersRepository relationshipUsersRepository;

    /*Reports*/

    @GetMapping("/reports")
    public ResponseEntity<List<ReportResponse>> getReports() {
        return ResponseEntity.ok(reportRepository.findAll().stream().map(r -> {
            ReportResponse report = ReportResponse.builder()
                    .from(r.getSender().getUsername())
                    .type(r.getReport_type().getTitle())
                    .timeStamp(r.getCreate_at())
                    .build();
            if (r.getTarget_user() != null) {
                report.setTo(r.getTarget_user().getUsername());
                report.setUser(true);
                report.setStatus(r.getTarget_user().is_active() ? "active" : "inactive");
            } else if (r.getTarget_group() != null) {
                report.setTo(r.getTarget_group().getGroupname());
                report.setGroup(true);
                report.setStatus(r.getTarget_group().isActive() ? "active" : "inactive");
            }else {
                report.setTo(r.getTarget_post().getId().toString());
                report.setPost(true);
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
                    .avatar(u.getImage().getMedia())
                    .gallery(u.getGalleries().stream().map(Gallery::getMedia).toList())
                    .build());
        } else {
            throw new UsernameNotFoundException("Username not found: " + username);
        }
    }

    @PostMapping("/users")
    public ResponseEntity<?> createUser() {
        return ResponseEntity.ok(null);
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
    public ResponseEntity<List<PostDto>> getPosts() {
        return ResponseEntity.ok(postRepository.findAll().stream().map(p -> modelMapper.map(p, PostDto.class)).toList());
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
            group.setGroupname(g.getGroupname());
            group.setActive(g.isActive());
            var image = g.getImage_group();
            if (image != null)  group.setImage_group(image.getMedia());
            return group;
        }).toList());
    }

    @GetMapping("/groups/{groupname}")
    public ResponseEntity<GroupDto> getGroup(@PathVariable String groupname) throws Exception {
        var optgroup = groupRepository.findByGroupname(groupname);
        if (optgroup.isPresent()) {
            var group = optgroup.get();
            var g = new GroupDto();
            g.setId(group.getId());
            g.setImage_group(group.getImage_group().getMedia());
            g.setGroupname(group.getGroupname());
            return ResponseEntity.ok(g);
        } else {
            throw new Exception("Group not found");
        }
    }

    /*Shop*/
}
