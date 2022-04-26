package com.bryan.staff.filter;

import static com.bryan.staff.Constant.SecurityConstant.*;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.bryan.staff.domain.HttpResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler{
	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException exception) throws IOException, ServletException{
		HttpResponse httpResponse = new HttpResponse(UNAUTHORIZED.value(),UNAUTHORIZED,UNAUTHORIZED.getReasonPhrase(),ACCESS_DENIED_MESSAGE);
		response.setContentType(APPLICATION_JSON_VALUE);
		response.setStatus(UNAUTHORIZED.value());
		OutputStream outputStream = response.getOutputStream();
		ObjectMapper mapper = new ObjectMapper();
		mapper.writeValue(outputStream, httpResponse);
		outputStream.flush();
	}

}
