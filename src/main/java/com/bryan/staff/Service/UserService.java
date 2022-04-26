package com.bryan.staff.Service;

import com.bryan.staff.domain.User;
import com.bryan.staff.exception.domain.EmailExistsException;
import com.bryan.staff.exception.domain.EmailNotFoundException;
import com.bryan.staff.exception.domain.NotAnImageFileException;
import com.bryan.staff.exception.domain.UserNotFoundException;
import com.bryan.staff.exception.domain.UsernameExistsException;

import java.io.IOException;
import java.util.List;

import javax.mail.MessagingException;

import org.springframework.web.multipart.MultipartFile;

public interface UserService {
//
//    User register(String firstname, String lastname, String username, String email, String position, String department, Long nid, Long mobile) throws UserNotFoundException, UsernameExistsException, EmailExistsException;

    List<User> getUser();

    User findByUsername(String username);

    User findUserByEmail(String email);
    
    User addNewUser(String firstname, String lastname, String username, String email, String position, String department, Long nid, Long mobile,String role, boolean isNotLocked, boolean isActive, MultipartFile profileImage) throws UserNotFoundException, UsernameExistsException, EmailExistsException, IOException, NotAnImageFileException;
    
    User updateUser(String currentUsername,String newFirstname, String newLastname, String newUsername, String newEmail, String newPosition, String newDepartment, Long newNid, Long newMobile,String role, boolean isNotLocked, boolean isActive, MultipartFile profileImage) throws UserNotFoundException, UsernameExistsException, EmailExistsException, IOException, NotAnImageFileException;

    void deleteuser(String username) throws IOException;
    
    void resetPassword(String email) throws EmailNotFoundException, MessagingException;
    
    User updateProfileImage(String username, MultipartFile profileImage) throws UserNotFoundException, UsernameExistsException, EmailExistsException, IOException, NotAnImageFileException;


    User register(String firstname, String lastname, String username, String email, Long nid, Long mobile) throws UserNotFoundException, UsernameExistsException, EmailExistsException;

}
