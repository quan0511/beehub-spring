package vn.aptech.beehub.seeders;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import vn.aptech.beehub.models.*;
import vn.aptech.beehub.repository.*;

import java.time.LocalDateTime;
import java.util.*;

@Component
public class DatabaseSeeder {

    private final GalleryRepository galleryRepository;
    private Logger logger = LoggerFactory.getLogger(DatabaseSeeder.class);
    private RequirementRepository requirementRepository;
    private GroupRepository groupRepository;
    private GroupMemberRepository groupMemberRepository;
    private ReportTypeRepository reportTypeRepository;
    private PostRepository postRepository;
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private RelationshipUsersRepository relationshipUsersRepository;
    private PasswordEncoder passwordEncoder;
    private RequirementRepository requirementRep;
    private ReportRepository reportRep;
    private GroupMediaRepository groupMediaRepository;

    public DatabaseSeeder(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            RequirementRepository requirementRepository,
            GroupRepository groupRepository,
            GroupMemberRepository groupMemberRepository,
            ReportTypeRepository reportTypeRepository,
            PostRepository postRepository,
            RelationshipUsersRepository relationshipUsersRepository,
            RequirementRepository requirementRep,
            ReportRepository reportRep,
            GalleryRepository galleryRepository,
            GroupMediaRepository groupMediaRepository, UserSettingRepository userSettingRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.requirementRepository = requirementRepository;
        this.groupRepository = groupRepository;
        this.groupMemberRepository = groupMemberRepository;
        this.reportTypeRepository = reportTypeRepository;
        this.postRepository = postRepository;
        this.relationshipUsersRepository = relationshipUsersRepository;
        this.requirementRep = requirementRep;
        this.reportRep= reportRep;
        this.galleryRepository = galleryRepository;
        this.groupMediaRepository = groupMediaRepository;
    }

    @EventListener
    public void seed(ContextRefreshedEvent event) {
//        seedRoles();
//        seederUser();
//        seederGroup();
//        seederGroupMember();
//        seederRelationshipUser();
//        seederReportType();
//        seederPosts();
//        seederRequirements();
//        seederGroupRequirements();
//        seedReports();
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

    private void seederUser() {
        List<User> users = userRepository.findAll();
        if(users.isEmpty()) {
        	List<String> names = Arrays.asList("Laurentina","Olivia Silence","Celinia Texas","Lappland Saluzzo","Lavinia Falcone","Heidi Thomson");
            Role userRole = roleRepository.findByName(ERole.ROLE_USER).get();
            for(int i =1; i<=6;i++) {
                String gernateGen= i%2==0?"female":"male";
                String phone = "09192343"+ (int) Math.floor(Math.random()*70+10);
                logger.info(phone);
                User user= new User("user"+i, "user"+i+"@gmail.com",passwordEncoder.encode("a123456"), names.get(i-1),gernateGen, phone, LocalDateTime.now());
                HashSet<Role> user1roles = new HashSet<Role>();
                user1roles.add(userRole);
                user.setRoles(user1roles);
                user.set_active(true);
                logger.info(user.toString());
                userRepository.save(user);
                logger.info("User "+i+" saved");
            }

            User admin = User.builder()
                    .username("admin")
                    .email("admin@gmail.com")
                    .fullname("admin")
                    .gender("male")
                    .password(passwordEncoder.encode("123456"))
                    .create_at(LocalDateTime.now())
                    .build();

            Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN).get();
            HashSet<Role> roles = new HashSet<>();
            roles.add(adminRole);

            admin.setRoles(roles);
            userRepository.save(admin);
            logger.info("Admin saved");
        }else {
            logger.trace("Seeding User is not required");
        }
    }
    private void seederGroup() {
        List<Group> groups = groupRepository.findAll();
        if(groups.isEmpty()) {
        	List<String> names = Arrays.asList("Rhodes Island ","Abyssal Hunters","Rhine Lab LLC.","Siracusa","Blacksteel Worldwide","Penguin Logistics","Ursus Student Group");
            for(int i =1; i<=7;i++) {
                Group group= new Group(names.get(i-1),"Description of "+names.get(i-1));
                group.setActive(true);
                group.setPublic_group(i%2==0);
                groupRepository.save(group);
                logger.info("Group "+i+" saved");
            }
        }else {
            logger.trace("Seeding Group is not required");
        }
    }
    private void seederGroupMember() {
    	List<GroupMember> groupmembers= groupMemberRepository.findAll();
		if(groupmembers.isEmpty()) {
			List<User> users = userRepository.findAll();
			List<Group> groups = groupRepository.findAll();
			if(!users.isEmpty()&& !groups.isEmpty()) {
				for (Iterator<Group> iterator = groups.iterator(); iterator.hasNext();) {
					Group group = (Group) iterator.next();
					GroupMember creator = new GroupMember();
					int ran1 = (int) Math.round(Math.random()*users.size()) ;
					ran1 = ran1>= 0&& ran1 <5?ran1:ran1-1;
					User user = users.get(ran1);
					creator.setGroup(group);
					creator.setUser(user);
					creator.setRole(EGroupRole.GROUP_CREATOR);
					groupMemberRepository.save(creator);
					logger.info("Group "+group.getId()+" creator userid: "+user.getUsername()+" saved");
					int numberF = ran1==users.size()?ran1-1:ran1;
					List<Integer> listRan = new LinkedList<Integer>();
					listRan.add(ran1);
					for(int i = 0; i<= numberF; i++) {
						GroupMember member = new GroupMember();
						member.setGroup(group);
						int ran2 = (int) Math.round(Math.random()*users.size());
						ran2 = ran2>=0 && ran2<5 && !(ran1==3&&ran2==1)? ran2: ran2-1;
						logger.info("Random 1: "+ran1);						
						do {
							if(!listRan.contains(ran2)) {
								User userMem = users.get(ran2);
								member.setUser(userMem);
								listRan.add(ran2);
							}else {
								ran2 = (int) Math.round(Math.random()*users.size());
								ran2 = ran1==3&&ran2==1?  (int) Math.round(Math.random()*users.size()) : ran2;
							}
							logger.info("Random 2: "+ran2);
						}while(listRan.contains(ran2));
						if(member.getUser()!=null) {
							member.setRole(EGroupRole.MEMBER);
							groupMemberRepository.save(member);				
							logger.info("Group "+group.getId()+" member saved");
						}
					}
				}
			}
		}else {
			logger.trace("Seeding Group member is not required");
		}
    }

    private void seederRelationshipUser() {
        List<RelationshipUsers> relas = relationshipUsersRepository.findAll();
        if(relas.isEmpty()) {
            List<User> users = userRepository.findAll();
            List<RelationshipUsers> createRelationship = Arrays.asList(
                    //Friends
                    new RelationshipUsers(users.get(0), users.get(1), ERelationshipType.FRIEND),
                    new RelationshipUsers(users.get(0), users.get(3), ERelationshipType.FRIEND),
                    new RelationshipUsers(users.get(1), users.get(3), ERelationshipType.FRIEND),
                    new RelationshipUsers(users.get(3), users.get(4), ERelationshipType.FRIEND),
                    new RelationshipUsers(users.get(5), users.get(2), ERelationshipType.FRIEND),
                    //Blocked
                    new RelationshipUsers(users.get(1), users.get(2), ERelationshipType.BLOCKED),
                    new RelationshipUsers(users.get(2), users.get(0), ERelationshipType.BLOCKED),
                    new RelationshipUsers(users.get(0), users.get(5), ERelationshipType.BLOCKED)
                    
            );
            for (RelationshipUsers rel : createRelationship) {
                relationshipUsersRepository.save(rel);
                logger.info("Relationship [ "+rel.getUser1().getUsername()+", "+rel.getUser2().getUsername()+" ] "+rel.getType()+" saved");
            }
        }else {
            logger.trace("Seeding User Relationship is not required");
        }
    }
    private void seederReportType() {
        List<ReportTypes> reportTypes = reportTypeRepository.findAll();
        if(reportTypes.isEmpty()) {
            List<ReportTypes>listRep = Arrays.asList(
                    new ReportTypes("nudity","If someone is in immediate danger, get help before reporting to Beehub. Don't wait."),
                    new ReportTypes("violence","If someone is in immediate danger, get help before reporting to Beehub. Don't wait."),
                    new ReportTypes("spam","We don't allow things such as: Buying, selling or giving away accounts, roles or permissions, directing people away from Beehub through the misleading use of links"),
                    new ReportTypes("involve a child","If someone is in immediate danger, get help before reporting to Beehub. Don't wait."),
                    new ReportTypes("drugs","If someone is in immediate danger, get help before reporting to Beehub. Don't wait.")
            );
            for (Iterator<ReportTypes> iterator = listRep.iterator(); iterator.hasNext();) {
                ReportTypes reportT = (ReportTypes) iterator.next();
                reportTypeRepository.save(reportT);
                logger.info("Saved Report Type "+reportT.getTitle());
            }
        }else {
            logger.trace("Seeding Report Type is not required");
        }
    }
    private void seedReports() {
        List<Report> reports = reportRep.findAll();

        if (reports.isEmpty()) {
            ReportTypes violence = reportTypeRepository.findByTitle("violence").get();
            ReportTypes spam = reportTypeRepository.findByTitle("spam").get();
            ReportTypes nudity = reportTypeRepository.findByTitle("nudity").get();
            Role userRole = roleRepository.findByName(ERole.ROLE_USER).get();
            HashSet<Role> roles = new HashSet<>();
            roles.add(userRole);

            User bumblebee = User.builder()
                    .username("Bumblebee")
                    .email("bumblebee@gmail.com")
                    .password(passwordEncoder.encode("123123"))
                    .roles(roles)
                    .gender("male")
                    .create_at(LocalDateTime.now())
                    .build();
            bumblebee = userRepository.save(bumblebee);
            Gallery bumbleBeeImage = galleryRepository.save(new Gallery(bumblebee, "https://th.bing.com/th/id/OIP.EmRwBDZe4rBVjntQ1uzpVwHaHa?w=164&h=180&c=7&r=0&o=5&pid=1.7", "image", LocalDateTime.now()));
            bumblebee = userRepository.findByUsername("Bumblebee").get();
            bumblebee.setImage(bumbleBeeImage);
            userRepository.save(bumblebee);
            galleryRepository.saveAll(List.of(
                    new Gallery(bumblebee, "https://th.bing.com/th/id/OIP.EmRwBDZe4rBVjntQ1uzpVwHaHa?w=164&h=180&c=7&r=0&o=5&pid=1.7", "image", LocalDateTime.now()),
                    new Gallery(bumblebee, "https://th.bing.com/th/id/OIP.VIHMP3vrUeXRhNq6yaR9sAHaEK?w=321&h=180&c=7&r=0&o=5&pid=1.7", "image", LocalDateTime.now()),
                    new Gallery(bumblebee, "https://th.bing.com/th/id/OIP.Eiw8hrGkSx-wxgXOHedEbgHaEp?w=307&h=192&c=7&r=0&o=5&pid=1.7", "image", LocalDateTime.now())
            ));

            User optimus = User.builder()
                    .username("Optimus")
                    .email("optimus@gmail.com")
                    .password(passwordEncoder.encode("123123"))
                    .roles(roles)
                    .gender("male")
                    .create_at(LocalDateTime.now())
                    .build();
            optimus = userRepository.save(optimus);
            Gallery optimusImage = galleryRepository.save(new Gallery(optimus, "https://th.bing.com/th/id/OIP.UhzoNGGIvKYW8YahcshtwAHaHa?w=184&h=184&c=7&r=0&o=5&pid=1.7", "image", LocalDateTime.now()));
            optimus = userRepository.findByUsername("Optimus").get();
            optimus.setImage(optimusImage);
            userRepository.save(optimus);
            galleryRepository.saveAll(List.of(
                    new Gallery(optimus, "https://th.bing.com/th/id/OIP.FGuDQu58aABECzJerNm30wHaEQ?w=326&h=187&c=7&r=0&o=5&pid=1.7", "image", LocalDateTime.now()),
                    new Gallery(optimus, "https://th.bing.com/th/id/OIP.kPzrHJuZuOnHmgNDs90xOAHaD5?w=324&h=180&c=7&r=0&o=5&pid=1.7", "image", LocalDateTime.now()),
                    new Gallery(optimus, "https://th.bing.com/th/id/OIP.YXGRuNl-quzuxAwT7EEVAAHaEJ?w=319&h=180&c=7&r=0&o=5&pid=1.7", "image", LocalDateTime.now())
            ));

            Group optimusGang = groupRepository.save(new Group("OptimusGang", "Buy car features"));
            GroupMedia optimusGangImage = groupMediaRepository.save(new GroupMedia("https://th.bing.com/th/id/OIP.UhzoNGGIvKYW8YahcshtwAHaHa?w=184&h=184&c=7&r=0&o=5&pid=1.7","image", LocalDateTime.now()));
            optimusGang = groupRepository.findByGroupname("OptimusGang").get();
            optimusGang.setImage_group(optimusGangImage);
            groupRepository.save(optimusGang);
            groupMemberRepository.save(new GroupMember(optimus, optimusGang, EGroupRole.GROUP_CREATOR));
            groupMemberRepository.save(new GroupMember(bumblebee, optimusGang, EGroupRole.MEMBER));

            Post optimusPost = new Post("Victory Dance",optimus , LocalDateTime.now().minusDays(1),ESettingType.PUBLIC);
            optimusPost.setMedias("https://th.bing.com/th/id/OIP.JsDu3_q9ZIft7cATRgztQAHaFG?rs=1&pid=ImgDetMain");
            optimusPost.setCreate_at(LocalDateTime.now());
            optimusPost = postRepository.save(optimusPost);

            Report report1 = new Report();
            report1.setCreate_at(LocalDateTime.now());
            report1.setUpdate_at(LocalDateTime.now());
            report1.setSender(bumblebee);
            report1.setTarget_user(optimus);
            report1.setReport_type(nudity);
            reportRep.save(report1);
            Report report2 = new Report();
            report2.setCreate_at(LocalDateTime.now());
            report2.setUpdate_at(LocalDateTime.now());
            report2.setSender(bumblebee);
            report2.setTarget_group(optimusGang);
            report2.setReport_type(spam);
            reportRep.save(report2);
            Report report3 = new Report();
            report3.setCreate_at(LocalDateTime.now());
            report3.setUpdate_at(LocalDateTime.now());
            report3.setSender(bumblebee);
            report3.setTarget_post(optimusPost);
            report3.setReport_type(violence);
            reportRep.save(report3);
        }
    }
    private void seederPosts() {
        List<Post> posts = postRepository.findAll();
        if(posts.isEmpty()) {
            List<User> users = userRepository.findAll();
            List<Post> listPo = Arrays.asList(
                    new Post("Naturally, they and the darkness behind them will occasionally prevail... But mark my words, even the most frustrating rainy season will pass in time. Until then, all we can do is persevere, and keep trying.",users.get(0) , LocalDateTime.now().minusDays(1),ESettingType.PUBLIC),
                    new Post("There is no such thing as absolute justice, but I still invoke it!.",users.get(0) , LocalDateTime.now(),ESettingType.FOR_FRIEND),
                    new Post("Sitting around in an office won't bring about justice. That's one thing I know for a fact.",users.get(0) , LocalDateTime.now(),ESettingType.FOR_FRIEND),
                    new Post("Sleep tight... enjoy your sweet dreams while you still can.",users.get(1) , LocalDateTime.now().minusDays(2),ESettingType.HIDDEN),
                    new Post("The fence was confused about whether it was supposed to keep things in or keep things out.",users.get(1) , LocalDateTime.now(),ESettingType.FOR_FRIEND),
                    new Post("You showed me that even weeds struggling to grow in the ruins can stand tall. Thank you.",users.get(1) , LocalDateTime.now(),ESettingType.PUBLIC),
                    new Post("No matter how hard I run, my past will catch me one day.",users.get(2) , LocalDateTime.now().minusDays(3),ESettingType.PUBLIC),
                    new Post("Where did you find the courage to challenge me?",users.get(2) , LocalDateTime.now().minusDays(1),ESettingType.PUBLIC),
                    new Post("Texas? Ha, she's afraid of me, but I don't wanna do anything to her, except maybe try to bring back the old Texas. The one we got here's a cucciolo impaurito",users.get(3) , LocalDateTime.now().minusDays(1),ESettingType.FOR_FRIEND),
                    new Post("You want to know what happened between me and Texas? Hehe, you don't have a single idea of what you are sticking your nose into. You'll find out the truth someday, but not from me.",users.get(3) , LocalDateTime.now().minusHours(3),ESettingType.PUBLIC),
                    new Post("The only way to solve a problem is to clear all the obstacles in the way, no matter what they might be and keep moving.",users.get(4) , LocalDateTime.now(),ESettingType.HIDDEN),
                    new Post("Clouds ranged as billows, distant-run roads withered. I, departed of the vistas, sailed a ferry in late autumn.",users.get(4) , LocalDateTime.now(),ESettingType.FOR_FRIEND),
                    new Post("Though antithetical at a glance, your nature is quite similar.",users.get(4) , LocalDateTime.now(),ESettingType.PUBLIC),
                    new Post("Though antithetical at a glance, your nature is quite similar.",users.get(4) , LocalDateTime.now(),ESettingType.PUBLIC),
                    new Post("To scatter the fill of a brimming cup, to share the sip for a soothed sorrow.",users.get(5) , LocalDateTime.now(),ESettingType.FOR_FRIEND),
                    new Post("I've had a taste of how Rhodes Island fights before, but, well, this still makes a great reference...",users.get(5) , LocalDateTime.now(),ESettingType.HIDDEN),
                    new Post("Running is useless... resist it, even if you are not willing to resist. This is the only way I've come to know to face suffering.",users.get(5) , LocalDateTime.now(),ESettingType.FOR_FRIEND),
                    new Post("They say \"the law is the basis of morality,\" but that's not a natural truth. It emerges from a consensus of the people. When the people come to see violence as order, the law is nothing but a fantasy to dream of.",users.get(4) , LocalDateTime.now(),ESettingType.PUBLIC),
                    new Post("In Siracusa, being a city court judge is a dangerous job. Either you join \"them,\" or... you disappear. ", users.get(5), LocalDateTime.now(), ESettingType.PUBLIC,"#fff","rgb(0, 157, 255)")
            );
            for (Iterator<Post> iterator = listPo.iterator(); iterator.hasNext();) {
                Post post = (Post) iterator.next();
                post.setColor("inherit") ;
                post.setBackground("inherit");
                postRepository.save(post);
                logger.info("Saved Post.");
            }
            List<Group> groups = groupRepository.findAll();
            List<String> content = Arrays.asList(
            		"Follow my light. Watch out for the tides and mist.",
            		"The most dangerous enemies are usually masters of disguise.",
            		"You can't let them see you exhausted, can you...?",
            		"Give me a reason to let you go. Don't have one? What a pity.",
            		"I hope I won't have to explain to you what we mean by \"necessary methods.\"",
            		"There is always a way to correct a mistake. Don't let anger get the best of you.",
            		"\"When 'tis a choice of life and death, why shouldst thou lay down thy arms?\"",
            		"Such simple misfortunes are only the beginning.",
            		"Better safe than sorry.",
            		"Mm, another beautiful day.",
            		"Show me what you've got.",
            		"Honey to me, poison to you."
            		);
			for (Iterator<Group> iterator = groups.iterator(); iterator.hasNext();) {
				Group group = (Group) iterator.next();
				List<GroupMember> members = groupMemberRepository.findByGroup_id(group.getId());
				for (Iterator<GroupMember> iterator2 = members.iterator(); iterator2.hasNext();) {
					GroupMember groupMember = (GroupMember) iterator2.next();
					User member = groupMember.getUser();
					int randomDay=(int) Math.round(Math.random()+5);
					int randomPost=(int) Math.floor(Math.random()*content.size());
					randomPost = randomPost<0?0:randomPost;
					Post postFromMem = new Post(content.get(randomPost),member, LocalDateTime.now().minusDays(randomDay),group);
					postFromMem.setColor("inherit") ;
					postFromMem.setBackground("inherit");
					postRepository.save(postFromMem);
					logger.info("Saved Post to Group "+group.getGroupname());
				}
			}
        }else {
            logger.trace("Seeding Post is not required");
        }
    }
    private void seederRequirements() {
        List<Requirement> listre = requirementRepository.findAll();
        if(listre.isEmpty()) {
            List<User> users = userRepository.findAll();
            List<Group> groups = groupRepository.findAll();
            if(!users.isEmpty() && !groups.isEmpty()) {
                List<Requirement> list = Arrays.asList(
                        //Add friends requirements
                        new Requirement(users.get(0),users.get(4)),
                        new Requirement(users.get(4),users.get(2))
                );
                for (Iterator<Requirement> iterator = list.iterator(); iterator.hasNext();) {
                    Requirement requirement = (Requirement) iterator.next();
                    requirementRepository.save(requirement);
                    logger.info("Requirement "+requirement.getSender().getUsername()+" "+requirement.getType()+" saved");
                }
            }
        }else{
            logger.trace("Requirement is not required seeder");
        }
    }
    private void seederGroupRequirements() {
		List<Group> groups = groupRepository.findAll();
		if(groups.isEmpty()) {
			for (Iterator<Group> iterator = groups.iterator(); iterator.hasNext();) {
				Group group = (Group) iterator.next();
				List<User> listUser = userRepository.findUsersNotJoinedGroup(group.getId());
				for (Iterator<User> iterator2 = listUser.iterator(); iterator2.hasNext();) {
					User user = (User) iterator2.next();
					Requirement requirement = new Requirement(user, group);		
					requirementRep.save(requirement);						
					logger.info("Seed Requirement for Group Success");
				}
			}
		}
	}
}
