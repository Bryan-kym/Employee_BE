package com.bryan.staff.filter;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.springframework.http.HttpStatus.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.stereotype.Component;

import static com.bryan.staff.Constant.SecurityConstant.*;
import com.bryan.staff.domain.HttpResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Component
public class JwtAuthentificationEntryPoint extends Http403ForbiddenEntryPoint {
	
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)throws IOException{
		HttpResponse httpResponse = new HttpResponse(FORBIDDEN.value(),FORBIDDEN,FORBIDDEN.getReasonPhrase().toUpperCase(),FORBIDDEN_MESSAGE);
		response.setContentType(APPLICATION_JSON_VALUE);
		response.setStatus(FORBIDDEN.value());
		OutputStream outputStream = response.getOutputStream();
		ObjectMapper mapper = new ObjectMapper();
		mapper.writeValue(outputStream, httpResponse);
		outputStream.flush();
	}

}
