package vn.aptech.beehub.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import vn.aptech.beehub.models.Post;

public interface PostRepository extends JpaRepository<Post, Integer>{

}
