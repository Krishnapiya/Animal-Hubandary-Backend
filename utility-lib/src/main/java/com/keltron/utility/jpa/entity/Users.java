package com.keltron.utility.jpa.entity;

import com.keltron.utility.ValidationUtils;
import com.keltron.utility.beans.abs.AbstractDto;
import com.keltron.utility.beans.dto.UsersDto;
import com.keltron.utility.responses.payload.DropdownPayload;
import com.keltron.utility.security.PasswordHasher;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Table(name = "users", schema = "master")
@Entity
@ToString
@NoArgsConstructor
public class Users extends AbstractEntity {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false)
	private Long id;

	@Column(name = "fname", nullable = false)
	private String fname;

	
	@Column(name = "lname", nullable = false)
	private String lname;

	@Column(name = "email", nullable = false)
	private String email;

	@Column(name = "mobile_no", nullable = false)
	private String mobileNo;

//	@JoinColumn(name = "gender_id", referencedColumnName = "id", nullable = false)
//	@ManyToOne(fetch = FetchType.EAGER)
//	private IndexGender genderId;
//
//	@Column(name = "employee_code", nullable = false)
//	private String employeeCode;
//
	@Column(name = "username", nullable = false)
	private String username;

	@Column(name = "password", nullable = false)
	private String password;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "role_id",referencedColumnName = "id") // the FK column in users table
	private RoleMaster role;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "office_id")
	private Office office;

	@Override
	public <K extends AbstractDto> void copyFromDTO(K dto) {
		UsersDto usersDto = (UsersDto) dto;
		if (ValidationUtils.isValid(usersDto.getId()))
			id = usersDto.getId();
		if (ValidationUtils.isValid(usersDto.getFname()))
			fname = usersDto.getFname();
		if (ValidationUtils.isValid(usersDto.getLname()))
			lname = usersDto.getLname();
		if (ValidationUtils.isValid(usersDto.getEmail()))
			email = usersDto.getEmail();
		if (ValidationUtils.isValid(usersDto.getMobileNo()))
			mobileNo = usersDto.getMobileNo();
//		if (ValidationUtils.isValid(usersDto.getGenderId())) {
//			genderId = new IndexGender(usersDto.getGenderId());
//		}
//		if (ValidationUtils.isValid(usersDto.getEmployeeCode()))
//			employeeCode = usersDto.getEmployeeCode();
		if (ValidationUtils.isValid(usersDto.getUsername()))
			username = usersDto.getUsername();
		if (ValidationUtils.isValid(usersDto.getPassword()))
			password = hashPassword(usersDto.getPassword());
		if (usersDto.getRole() != null && usersDto.getRole().getId() != null) {
			role = new RoleMaster(usersDto.getRole().getId());
		} else {
			role = null;
		}
		if (usersDto.getOffice() != null && usersDto.getOffice().getId() != null) {
			office = new Office();
			office.setId(usersDto.getOffice().getId());
		} else {
			office = null;
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public UsersDto toDTO() {
		UsersDto dto = new UsersDto();
		dto.setId(id);
		dto.setFname(fname);
		dto.setLname(lname);
		dto.setEmail(email);
//		if (genderId != null) {
//			dto.setGenderId(genderId.getId());
//			dto.setGenderName(genderId.getName());
//		}
//		dto.setEmployeeCode(employeeCode);
		dto.setUsername(username);
		dto.setMobileNo(mobileNo);
		if (role != null) {
		    DropdownPayload<Integer> rolePayload = new DropdownPayload<>();
		    rolePayload.setId(role.getId());
		    rolePayload.setName(role.getRoleName());
		    dto.setRole(rolePayload);
		}
		if (office != null) {
		    DropdownPayload<Integer> off = new DropdownPayload<>();
		    off.setId(office.getId());
		    off.setName(office.getName());
		    dto.setOffice(off);
		}

		// dto.setPassword(password);
		return dto;

	}

	public String hashPassword(String password) {
		return PasswordHasher.bcrypt(password);
	}
	@Override
	public DropdownPayload<Long> toDropDownPayload() {
		// TODO Auto-generated method stub
		DropdownPayload<Long> payLoad = new DropdownPayload<Long>();
		payLoad.setId(id);
		payLoad.setName(fname);
		return payLoad;
	}
	
	

	public Users(Long id) {
		// TODO Auto-generated constructor stub
		this.id=id;
	}

}
