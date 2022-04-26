package com.bryan.staff.impl;

import static com.bryan.staff.Constant.FileConstant.*;
import static com.bryan.staff.Constant.UserImplConstant.EMAIL_ALREADY_EXISTS;
import static com.bryan.staff.Constant.UserImplConstant.*;
import static com.bryan.staff.Constant.UserImplConstant.RETURNING_FOUND_USER_BY_USERNAME;
import static com.bryan.staff.Constant.UserImplConstant.USERNAME_ALREADY_EXISTS;
import static com.bryan.staff.enumeration.Role.ROLE_USER;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.springframework.http.MediaType.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.mail.MessagingException;
import javax.transaction.Transactional;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.bryan.staff.Repository.UserRepository;
import com.bryan.staff.Service.EmailService;
import com.bryan.staff.Service.LoginAttemptService;
import com.bryan.staff.Service.UserService;
import com.bryan.staff.domain.User;
import com.bryan.staff.domain.UserPrincipal;
import com.bryan.staff.enumeration.Role;
import com.bryan.staff.exception.domain.EmailExistsException;
import com.bryan.staff.exception.domain.EmailNotFoundException;
import com.bryan.staff.exception.domain.NotAnImageFileException;
import com.bryan.staff.exception.domain.UserNotFoundException;
import com.bryan.staff.exception.domain.UsernameExistsException;

@Service
@Transactional
@Qualifier("userDetailsService")
public class UserServiceImpl implements UserService, UserDetailsService {
	private Logger LOGGER = LoggerFactory.getLogger(getClass());
	private UserRepository userRepository;
	private BCryptPasswordEncoder passwordEncoder;
	private LoginAttemptService loginAttemptService;
	private EmailService emailService;

	@Autowired
	public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder,
			LoginAttemptService loginAttemptService,EmailService emailService) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.loginAttemptService = loginAttemptService;
		this.emailService = emailService;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findUserByUsername(username);
		if (user == null) {
			LOGGER.error(NO_USER_FOUND_BY_USERNAME + username);
			throw new UsernameNotFoundException(NO_USER_FOUND_BY_USERNAME + username);
		} else {
			validateLoginAttempt(user);
			user.setLastLoginDateDisplay(user.getLastLoginDate());
			user.setLastLoginDate(new Date());
			userRepository.save(user);
			UserPrincipal userPrincipal = new UserPrincipal(user);
			LOGGER.info(RETURNING_FOUND_USER_BY_USERNAME + username);
			return userPrincipal;
		}
	}

	private void validateLoginAttempt(User user) {
		if (user.isNotLocked()) {
			if (loginAttemptService.hasExceededMaxAttempts(user.getUsername())) {
				user.setNotLocked(false);
			} else {
				user.setNotLocked(true);
			}
		} else {
			loginAttemptService.evictUserFromLoginAttemptCache(user.getUsername());
		}

	}

//	@Override
//	public User register(String firstname, String lastname, String username, String email, String position,
//			String department, Long nid, Long mobile)
//			throws UserNotFoundException, UsernameExistsException, EmailExistsException {
//		validateNewUsernameAndEmail(EMPTY, username, email);
//		User user = new User();
//		user.setUserid(generateUserId());
//		String password = generatePassword();
//		String encodedPassword = encodePassword(password);
//		user.setFirstName(firstname);
//		user.setLastName(lastname);
//		user.setUsername(username);
//		user.setNid(nid);
//		user.setMobile(mobile);
//		user.setDepartment(department);
//		user.setPosition(position);
//		user.setEmail(email);
//		user.setJoinDate(new Date());
//		user.setPassword(encodedPassword);
//		user.setActive(true);
//		user.setNotLocked(true);
//		user.setRoles(ROLE_USER.name());
//		user.setAuthorities(ROLE_USER.getAuthorities());
//		user.setProfileImageUrl(getTemporaryProfileImageUrl(username));
//		userRepository.save(user);
//		LOGGER.info("New user Password:" + password);
//		try {
//			emailService.sendNewPasswordemail(firstname, password, email);
//		} catch (MessagingException e) {
//			e.printStackTrace();
//		}
//		return user;
//	}
	@Override
	public User register(String firstname, String lastname, String username, String email, Long nid, Long mobile)
			throws UserNotFoundException, UsernameExistsException, EmailExistsException {
		validateNewUsernameAndEmail(EMPTY, username, email);
		User user = new User();
		user.setUserid(generateUserId());
		String password = generatePassword();
		String encodedPassword = encodePassword(password);
		user.setFirstName(firstname);
		user.setLastName(lastname);
		user.setUsername(username);
		user.setNid(nid);
		user.setMobile(mobile);
		user.setEmail(email);
		user.setJoinDate(new Date());
		user.setPassword(encodedPassword);
		user.setActive(true);
		user.setNotLocked(true);
		user.setRoles(ROLE_USER.name());
		user.setAuthorities(ROLE_USER.getAuthorities());
		user.setProfileImageUrl(getTemporaryProfileImageUrl(username));
		userRepository.save(user);
		LOGGER.info("New user Password:" + password);
		try {
			emailService.sendNewPasswordemail(firstname, password, email);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		return user;
	}

	private String getTemporaryProfileImageUrl(String username) {
		return ServletUriComponentsBuilder.fromCurrentContextPath().path(DEFAULT_USER_IMAGE_PATH + username).toUriString();
	}

	private String encodePassword(String password) {
		return passwordEncoder.encode(password);
	}

	private String generatePassword() {
		return RandomStringUtils.randomAlphanumeric(10);
	}

	private String generateUserId() {
		return RandomStringUtils.randomNumeric(10);
	}

	private User validateNewUsernameAndEmail(String currentUsername, String newUsername, String newEmail)
			throws UserNotFoundException, UsernameExistsException, EmailExistsException {
		User userByNewUsername = findByUsername(newUsername);
		User userByNewEmail = findUserByEmail(newEmail);
		User currentUser = findByUsername(currentUsername);
		if (isNotBlank(currentUsername)) {
			if (currentUser == null) {
				throw new UserNotFoundException(NO_USER_FOUND_BY_USERNAME + currentUsername);
			}
			if (userByNewUsername != null && !currentUser.getId().equals(userByNewUsername.getId())) {
				throw new UsernameExistsException(USERNAME_ALREADY_EXISTS);
			}
			if (userByNewEmail != null && !currentUser.getId().equals(userByNewEmail.getId())) {
				throw new EmailExistsException(EMAIL_ALREADY_EXISTS);
			}
			return currentUser;
		} else {
			if (userByNewUsername != null) {
				throw new UsernameExistsException(USERNAME_ALREADY_EXISTS);
			}
			if (userByNewEmail != null) {
				throw new EmailExistsException(EMAIL_ALREADY_EXISTS);
			}
		}
		return null;
	}

	@Override
	public List<User> getUser() {
		return userRepository.findAll();
	}

	@Override
	public User findByUsername(String username) {
		return userRepository.findUserByUsername(username);
	}

	@Override
	public User findUserByEmail(String email) {
		return userRepository.findUserByEmail(email);
	}

	@Override
	public User addNewUser(String firstname, String lastname, String username, String email, String position,
			String department, Long nid, Long mobile, String role, boolean isNotLocked, boolean isActive,
			MultipartFile profileImage) throws UserNotFoundException, UsernameExistsException, EmailExistsException, IOException, NotAnImageFileException {
		validateNewUsernameAndEmail(EMPTY, username, email);
		User user = new User();
		String password = generatePassword();
		user.setUserid(generateUserId());
		user.setFirstName(firstname);
		user.setLastName(lastname);
		user.setJoinDate(new Date());
		user.setUsername(username);
		user.setEmail(email);
		user.setMobile(mobile);
		user.setDepartment(department);
		user.setPosition(position);
		user.setNid(nid);
		user.setPassword(encodePassword(password));
		user.setActive(isActive);
		user.setNotLocked(isNotLocked);
		user.setRoles(getRoleEnumName(role).name());
		user.setAuthorities(getRoleEnumName(role).getAuthorities());
		user.setProfileImageUrl(getTemporaryProfileImageUrl(username));
		userRepository.save(user);
		saveProfileImage(user, profileImage);
		LOGGER.info("New user Password:" + password);
		return user;
	}

	private void saveProfileImage(User user, MultipartFile profileImage) throws IOException, NotAnImageFileException {
		if(profileImage !=null) {
			if(!Arrays.asList(IMAGE_JPEG_VALUE,IMAGE_PNG_VALUE,IMAGE_GIF_VALUE).contains(profileImage.getContentType())) {
				throw new NotAnImageFileException(profileImage.getOriginalFilename() + "is not an image file. Please upload an image");
			}
			Path userfolder = Paths.get(USER_FOLDER + user.getUsername()).toAbsolutePath().normalize();
			if(!Files.exists(userfolder)) {
				Files.createDirectories(userfolder);
				LOGGER.info(DIRECTORY_CREATED);
			}
			Files.deleteIfExists(Paths.get(userfolder + user.getUsername() + DOT + JPG_EXTENSION));
			Files.copy(profileImage.getInputStream(),userfolder.resolve(user.getUsername()+DOT + JPG_EXTENSION),REPLACE_EXISTING);
			user.setProfileImageUrl(setProfileImageUrl(user.getUsername()));
			userRepository.save(user);
			LOGGER.info(FILE_SAVED_IN_FILE_SYSTEM + profileImage.getOriginalFilename());
		}
	}

	private String setProfileImageUrl(String username) {
		return ServletUriComponentsBuilder.fromCurrentContextPath().path(USER_IMAGE_PATH + username + FORWARD_SLASH + username + DOT + JPG_EXTENSION).toUriString();
	}

	private Role getRoleEnumName(String role) {
		
		return Role.valueOf(role.toUpperCase());
	}

	@Override
	public User updateUser(String currentUsername, String newFirstname, String newLastname, String newUsername,
			String newEmail, String newPosition, String newDepartment, Long newNid, Long newMobile, String role,
			boolean isNotLocked, boolean isActive, MultipartFile profileImage) throws UserNotFoundException, UsernameExistsException, EmailExistsException, IOException, NotAnImageFileException {
		User currentUser = validateNewUsernameAndEmail(currentUsername, newUsername, newEmail);
		currentUser.setFirstName(newFirstname);
		currentUser.setLastName(newLastname);
		currentUser.setUsername(newUsername);
		currentUser.setEmail(newEmail);
		currentUser.setMobile(newMobile);
		currentUser.setDepartment(newDepartment);
		currentUser.setPosition(newPosition);
		currentUser.setNid(newNid);
		currentUser.setActive(isActive);
		currentUser.setNotLocked(isNotLocked);
		currentUser.setRoles(getRoleEnumName(role).name());
		currentUser.setAuthorities(getRoleEnumName(role).getAuthorities());
		userRepository.save(currentUser);
		saveProfileImage(currentUser, profileImage);
		return currentUser;
	}

	public void deleteuser(String username) throws IOException {
		User user = userRepository.findUserByUsername(username);
		Path userFolder = Paths.get(USER_FOLDER, user.getUsername()).toAbsolutePath().normalize(); 
		FileUtils.deleteDirectory(new File(userFolder.toString()));
		userRepository.deleteById(user.getId());
	}

	@Override
	public void resetPassword(String email) throws EmailNotFoundException, MessagingException {
		User user = userRepository.findUserByEmail(email);
		if(user == null) {
			throw new EmailNotFoundException(NO_USER_FOUND_BY_EMAIL + email);
		}
		String password = generatePassword();
		user.setPassword(encodePassword(password));
		userRepository.save(user);
		LOGGER.info("New user Password:" + password);
		emailService.sendNewPasswordemail(user.getFirstName(), password, user.getEmail());
		LOGGER.info("New user Password:" + password);
		
	}

	@Override
	public User updateProfileImage(String username, MultipartFile profileImage) throws UserNotFoundException, UsernameExistsException, EmailExistsException, IOException, NotAnImageFileException {
		User user = validateNewUsernameAndEmail(username, null, null);
		saveProfileImage(user, profileImage);
		return user;
	}
}
