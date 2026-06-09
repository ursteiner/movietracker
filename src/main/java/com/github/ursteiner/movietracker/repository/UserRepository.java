package com.github.ursteiner.movietracker.repository;

import com.github.ursteiner.movietracker.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<AppUser, UUID> {
    AppUser findUserByGithubId(Integer githubId);
}
