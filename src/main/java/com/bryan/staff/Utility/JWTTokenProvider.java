package com.bryan.staff.Utility;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;
import static com.bryan.staff.Constant.SecurityConstant.AUTHORITIES;
import static com.bryan.staff.Constant.SecurityConstant.EXPIRATION_TIME;
import static com.bryan.staff.Constant.SecurityConstant.GET_BRYAN;
import static com.bryan.staff.Constant.SecurityConstant.GET_BRYAN_ADMINISTRATION;
import static com.bryan.staff.Constant.SecurityConstant.TOKEN_CANNOT_BE_VERIFIED;
import static java.util.Arrays.stream;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.bryan.staff.domain.UserPrincipal;

@Component
public class JWTTokenProvider {
	
	@Value("${jwt.secret}")
	private String secret;

	public String generateJwtToken(UserPrincipal userPrincipal) {
		String[] claims = getClaimsFromUser(userPrincipal);
		return JWT.create().withIssuer(GET_BRYAN).withAudience(GET_BRYAN_ADMINISTRATION).withIssuedAt(new Date())
				.withSubject(userPrincipal.getUsername()).withArrayClaim(AUTHORITIES, claims)
				.withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME)).sign(HMAC512(secret.getBytes()));

	}

	public List<GrantedAuthority> getAuthorities(String token) {
		String[] claims = getClaimsFromToken(token);
		return stream(claims).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
	}

	private String[] getClaimsFromToken(String token) {
		JWTVerifier verifier = getJWTVerifier();
		return verifier.verify(token).getClaim(AUTHORITIES).asArray(String.class);
	}

	private JWTVerifier getJWTVerifier() {
		JWTVerifier verifier;
		try {
			Algorithm algorithm = HMAC512(secret);
			verifier = JWT.require(algorithm).withIssuer(GET_BRYAN).build();
		} catch (JWTVerificationException exception) {
			throw new JWTVerificationException(TOKEN_CANNOT_BE_VERIFIED);
		}
		return verifier;
	}

	private String[] getClaimsFromUser(UserPrincipal user) {
		List<String> authorities = new ArrayList<>();
		for (GrantedAuthority grantedAuthority : user.getAuthorities()) {
			authorities.add(grantedAuthority.getAuthority());
		}
		return authorities.toArray(new String[0]);
	}

	public Authentication getAuthentication(String username, List<GrantedAuthority> authorities,
			HttpServletRequest request) {
		UsernamePasswordAuthenticationToken userpasswordAuthToken = new UsernamePasswordAuthenticationToken(username,
				null, authorities);
		userpasswordAuthToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
		return userpasswordAuthToken;
	}

	public boolean isTokenValid(String username, String token) {
		JWTVerifier verifier = getJWTVerifier();
		return StringUtils.isNotBlank(username) && !isTokenExpired(verifier, token);
	}

	public boolean isTokenExpired(JWTVerifier verifier, String token) {
		Date expiration = verifier.verify(token).getExpiresAt();
		return expiration.before(new Date());
	}

	public String getSubject(String token) {
		JWTVerifier verifier = getJWTVerifier();
		return verifier.verify(token).getSubject();
	}
}
