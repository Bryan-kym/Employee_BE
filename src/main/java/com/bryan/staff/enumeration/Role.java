package com.bryan.staff.enumeration;

import static com.bryan.staff.Constant.Authorities.*;

public enum Role {
	ROLE_USER(USER_AUTHORITIES),
	ROLE_HR(HR_AUTHORITIES),
	ROLE_FINANCE(FINANCE_AUTHORITIES),
	ROLE_MANAGER(MANAGER_AUTHORITIES),
	ROLE_ADMIN(ADMIN_AUTHORITIES);

	private String[] authorities;

	Role(String... authorities){
		this.authorities = authorities;
	}

	public String[] getAuthorities() {
		return authorities;
	}
}
