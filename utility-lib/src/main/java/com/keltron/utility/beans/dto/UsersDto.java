package com.keltron.utility.beans.dto;

import org.springframework.http.HttpMethod;
import com.keltron.utility.ValidationUtils;
import com.keltron.utility.beans.abs.AbstractDto;
import com.keltron.utility.jpa.entity.Office;
import com.keltron.utility.jpa.entity.RoleMaster;
import com.keltron.utility.jpa.entity.Users;
import com.keltron.utility.responses.payload.DropdownPayload;
import com.keltron.utility.security.PasswordHasher;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsersDto extends AbstractDto {

	private static final long serialVersionUID = 1L;
	private Long id;
	private String fname;
	private String lname;
	private String email;
//	private Integer genderId;
	private String mobileNo;
//	private String employeeCode;
	private String username;
	private String password;
//	private String genderName;
	private DropdownPayload<Integer> role;
	private DropdownPayload<Integer> office;

	@SuppressWarnings("unchecked")
	@Override
	public Users toEntity() {
		// TODO Auto-generated method stub
		Users entity = new Users();
		entity.setId(id);
		entity.setFname(fname);
		entity.setLname(lname);
		entity.setEmail(email);
		entity.setMobileNo(mobileNo);
//		entity.setGenderId(new IndexGender(genderId));
//		entity.setEmployeeCode(employeeCode);
		entity.setUsername(username);
		entity.setPassword(PasswordHasher.bcrypt(password));
		if (role != null && role.getId() != null) {
			entity.setRole(new RoleMaster(role.getId()));
		}
		if (office != null && office.getId() != null) {
			Office o = new Office();
			o.setId(office.getId());
			entity.setOffice(o);
		} else {
			entity.setOffice(null);
		}
		return entity;
	}

	@Override
	public boolean isValid(HttpMethod httpMethod) {
		if (httpMethod == null)
			return false; // Prevent null errors

		if (httpMethod.equals(HttpMethod.POST)) {
			if (!ValidationUtils.isValid(fname)) {
				addError("fname", fname);
			}
			if (!ValidationUtils.isValid(lname)) {
				addError("lname", lname);
			}
			if (!ValidationUtils.isValid(email)) {
				addError("email", email);
			}
//			if (!ValidationUtils.isValid(employeeCode)) {
//				addError("employee_code", employeeCode);
//			}
			if (!ValidationUtils.isValid(mobileNo)) {
				addError("mobile_no", mobileNo);
			}
//			if (!ValidationUtils.isValid(genderId)) {
//				addError("genderId", genderId);
//			}
			if (!ValidationUtils.isValid(username)) {
				addError("username", username);
			}
			if (!ValidationUtils.isValid(password)) {
				addError("password", password);
			}
			if (!ValidationUtils.isValid(role)) {
				addError("role_id", role);
			}
			if (office == null || office.getId() == null) {
				addError("office", office);
			}
		} else if (httpMethod.equals(HttpMethod.PATCH)) {
			if (!ValidationUtils.isValid(id)) {
				addError("id", id);
			}
			if (office == null || office.getId() == null) {
				addError("office", office);
			}
		}

		return getErrors() == null || getErrors().isEmpty();
	}

	

}

