package com.acme.prsserver.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer>{
	
	Optional<User> findByUsernameAndPassword(String username, String password);
	
	/*how to do a query like above in more sql language. ?1/?2 are 1st and second params
	 * @Query("select u from User u where u.username = ?1 and u.password = ?2)
	 * User findByLogin(String username, String password);
	 * 
	 * @Query("select u from User u wehre u.lastname = ?1")
	 * List<User> findByLastname(String lastname);
	 */
	
	

}
