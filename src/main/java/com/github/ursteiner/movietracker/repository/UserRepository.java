package com.github.ursteiner.movietracker.repository;

import com.github.ursteiner.movietracker.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<AppUser, Long> {
    AppUser findUserByGithubId(Integer githubId);
}
