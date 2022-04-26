package com.bryan.staff.resource;

import com.bryan.staff.Service.UserService;
import com.bryan.staff.Utility.JWTTokenProvider;
import com.bryan.staff.domain.HttpResponse;
import com.bryan.staff.domain.User;
import com.bryan.staff.domain.UserPrincipal;
import com.bryan.staff.exception.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static com.bryan.staff.Constant.FileConstant.*;
import static com.bryan.staff.Constant.SecurityConstant.JWT_TOKEN_HEADER;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;

@RestController
@RequestMapping(path = { "/", "/user" })
//@CrossOrigin(origins = "http://localhost:4200")
public class UserResource extends ExceptionHandling {
	private UserService userService;
	private AuthenticationManager authenticationManager;
	private JWTTokenProvider jwtTokenProvider;

	@Autowired
	public UserResource(UserService userService, AuthenticationManager authenticationManager,
			JWTTokenProvider jwtTokenProvider) {
		this.userService = userService;
		this.authenticationManager = authenticationManager;
		this.jwtTokenProvider = jwtTokenProvider;
	}

	@PostMapping("/login")
	public ResponseEntity<User> login(@RequestBody User user) {
		authenticate(user.getUsername(), user.getPassword());
		User loginUser = userService.findByUsername(user.getUsername());
		UserPrincipal userPrincipal = new UserPrincipal(loginUser);
		HttpHeaders jwtHeader = getJwtHeader(userPrincipal);
		return new ResponseEntity<>(loginUser, jwtHeader, OK);
	}

//	@PostMapping("/register")
//	public ResponseEntity<User> register(@RequestBody User user)
//			throws UserNotFoundException, UsernameExistsException, EmailExistsException {
//		User newUser = userService.register(user.getFirstName(), user.getLastName(), user.getUsername(),
//				user.getEmail(), user.getPosition(), user.getDepartment(), user.getNid(), user.getMobile());
//		return new ResponseEntity<>(newUser, OK);
//	}
	@PostMapping("/register")
	public ResponseEntity<User> register(@RequestBody User user)
			throws UserNotFoundException, UsernameExistsException, EmailExistsException {
		User newUser = userService.register(user.getFirstName(), user.getLastName(), user.getUsername(),
				user.getEmail(), user.getNid(), user.getMobile());
		return new ResponseEntity<>(newUser, OK);
	}


	@PostMapping("/add")
	public ResponseEntity<User> addNewUser(@RequestParam("firstName") String firstName,
			@RequestParam("lastName") String lastName, @RequestParam("username") String username,
			@RequestParam("email") String email, @RequestParam("nid") String nid, @RequestParam("mobile") String mobile,
			@RequestParam("position") String position, @RequestParam("department") String department,
			@RequestParam("role") String role, @RequestParam("isNotLocked") String isNotLocked,
			@RequestParam("isActive") String isActive,
			@RequestParam(value = "profileImage", required = false) MultipartFile profileImage)
			throws NumberFormatException, UserNotFoundException, UsernameExistsException, EmailExistsException,
			IOException, NotAnImageFileException {
		User newUser = userService.addNewUser(firstName, lastName, username, email, position, department,
				Long.parseLong(nid), Long.parseLong(mobile), role, Boolean.parseBoolean(isNotLocked),
				Boolean.parseBoolean(isActive), profileImage);
		return new ResponseEntity<>(newUser, OK);

	}

	@PostMapping("/update")
	public ResponseEntity<User> update(@RequestParam("firstName") String firstName,
			@RequestParam("currentUsername") String currentUsername, @RequestParam("lastName") String lastName,
			@RequestParam("username") String username, @RequestParam("email") String email,
			@RequestParam("nid") String nid, @RequestParam("mobile") String mobile,
			@RequestParam("position") String position, @RequestParam("department") String department,
			@RequestParam("role") String role, @RequestParam("isNotLocked") String isNotLocked,
			@RequestParam("isActive") String isActive,
			@RequestParam(value = "profileImage", required = false) MultipartFile profileImage)
			throws NumberFormatException, UserNotFoundException, UsernameExistsException, EmailExistsException,
			IOException, NotAnImageFileException {
		User updatedUser = userService.updateUser(currentUsername, firstName, lastName, username, email, position,
				department, Long.parseLong(nid), Long.parseLong(mobile), role, Boolean.parseBoolean(isNotLocked),
				Boolean.parseBoolean(isActive), profileImage);
		return new ResponseEntity<>(updatedUser, OK);

	}
	
	@GetMapping("/find/{username}")
	public ResponseEntity<User> getUser(@PathVariable("username")String username){
		User user = userService.findByUsername(username);
		return new ResponseEntity<>(user, OK);
	}
	
	@GetMapping("/list")
	public ResponseEntity<List<User>> getAllUser(){
		List<User> users = userService.getUser();
		return new ResponseEntity<>(users, OK);
	}
	
	@GetMapping("/resetpassword/{email}")
	public ResponseEntity<HttpResponse> resetPassword(@PathVariable("email")String email) throws EmailNotFoundException, MessagingException{
		userService.resetPassword(email);
		return response(OK, "Email sent to: " + email);
	}
	
	@DeleteMapping("/delete/{username}")
	@PreAuthorize("hasAnyAuthority('user:delete')")
	public ResponseEntity<HttpResponse> delete(@PathVariable("username")String username) throws IOException{
		userService.deleteuser(username);
		return response(OK, "User deleted succesfully");
	}
	
	@PostMapping("/updateProfileImage")
	public ResponseEntity<User> updateProfileImage(@RequestParam("username") String username,@RequestParam(value = "profileImage") MultipartFile profileImage)
			throws NumberFormatException, UserNotFoundException, UsernameExistsException, EmailExistsException,
			IOException, NotAnImageFileException {
		User user = userService.updateProfileImage(username, profileImage);
		return new ResponseEntity<>(user, OK);

	}
	
	@GetMapping(path = "/image/{username}/{fileName}", produces = IMAGE_JPEG_VALUE)
	public byte[] getProfileImage(@PathVariable("username")String username,@PathVariable("fileName")String fileName) throws IOException {
		return Files.readAllBytes(Paths.get(USER_FOLDER + username + FORWARD_SLASH + fileName));
	}
	
	@GetMapping(path = "/image/profile/{username}", produces = IMAGE_JPEG_VALUE)
	public byte[] getTempProfileImage(@PathVariable("username")String username) throws IOException {
		URL url = new URL(TEMP_PROFILE_IMAGE_BASE_URL + username);
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		try(InputStream inputStream = url.openStream()){
			int bytesRead;
			byte[]chunk = new byte[1024];
			while((bytesRead = inputStream.read(chunk)) > 0) {
				byteArrayOutputStream.write(chunk,0,bytesRead);
			}
		}
		return byteArrayOutputStream.toByteArray();
	}

	private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
		HttpResponse body = new HttpResponse(httpStatus.value(),httpStatus,httpStatus.getReasonPhrase().toLowerCase(),message.toLowerCase());
		return new ResponseEntity<>(body,httpStatus);
	}

	private HttpHeaders getJwtHeader(UserPrincipal user) {
		HttpHeaders headers = new HttpHeaders();
		headers.add(JWT_TOKEN_HEADER, jwtTokenProvider.generateJwtToken(user));
		return headers;
	}

	private void authenticate(String username, String password) {
		authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
	}

}
