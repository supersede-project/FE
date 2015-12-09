package eu.supersede.fe.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import eu.supersede.fe.model.User;

public interface UsersJpa extends JpaRepository<User, Long> {

	/*@Query("SELECT u FROM User u WHERE u.alias IS NOT NULL")
    List<User> findAliased();*/
	
	User findByEmail(String email);
	
}