package com.keltron.admin.controller;

import java.io.ByteArrayOutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import com.keltron.admin.services.impl.UsersServiceImpl;
import com.keltron.admin.rbac.security.RequirePermission;
import com.keltron.admin.request.AssignRolesRequest;
import com.keltron.utility.ResponseBuilder;
import com.keltron.utility.beans.dto.UsersDto;
import com.keltron.utility.beans.searchbean.UsersSearchBean;
import com.keltron.utility.jpa.predicates.UsersPredicates;
import com.keltron.utility.requests.ExcelExportRequest;
import com.keltron.utility.requests.Request;
import com.keltron.utility.responses.AbstractResponse;
import com.keltron.utility.web.controller.abs.AbstractController;

import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "admin/auth/master/users")
public class UsersController extends AbstractController {

	@Autowired
	private UsersServiceImpl serviceImpl;

	@PostMapping("/save")
	@RequirePermission(menu = "test-page", action = "save")
	public ResponseEntity<AbstractResponse> save(@Valid @RequestBody Request<UsersDto> request) {
		if (!(request.isValid() && request.getPayLoad().isValid(HttpMethod.POST))) {
			return new ResponseBuilder().withError(HttpStatus.BAD_REQUEST, request.getPayLoad().getErrors()).build();
		}
		return new ResponseBuilder().withData(serviceImpl.save(request.getPayLoad()).toDTO()).build();
	}

	@PatchMapping("/save")
	@RequirePermission(menu = "test-page", action = "edit")
	public ResponseEntity<AbstractResponse> update(@Valid @RequestBody Request<UsersDto> request) {
		if (!(request.isValid() && request.getPayLoad().isValid(HttpMethod.PATCH))) {
			return new ResponseBuilder().withError(HttpStatus.BAD_REQUEST).build();
		}
		return new ResponseBuilder()
				.withData(serviceImpl.update(request.getPayLoad().getId(), request.getPayLoad()).toDTO()).build();
	}

	@GetMapping("/list/all")
	@RequirePermission(menu = "test-page", action = "list")
	public ResponseEntity<AbstractResponse> findByCriteria(

			@RequestParam(name = "dropDown", required = false, defaultValue = "false") boolean asDropdown,

			@Valid UsersSearchBean searchBean) {
		return asDropdown
				? new ResponseBuilder()
						.withData(serviceImpl.findByCriteria(UsersPredicates.createPredicate(searchBean),
								searchBean.getDataSort(), asDropdown, searchBean.getPageNo(), searchBean.getPageSize()))
						.build()
				: new ResponseBuilder().withData(serviceImpl.findByCriteria(UsersPredicates.createPredicate(searchBean),
						searchBean.getDataSort(), searchBean.getPageNo(), searchBean.getPageSize())).build();
	}

	/**
	 *
	 * @param ID
	 * @return
	 */
	@DeleteMapping("/delete/{id}")
	@RequirePermission(menu = "test-page", action = "delete")
	public ResponseEntity<AbstractResponse> delete(@Valid @PathVariable Long id) {
		return new ResponseBuilder().withData(serviceImpl.delete(id)).build();
	}

  @PostMapping("/{id}/assign-roles")
  @RequirePermission(menu = "test-page", action = "assign-roles")
  public ResponseEntity<AbstractResponse> assignRoles(@PathVariable Long id, @RequestBody AssignRolesRequest request) {
		return new ResponseBuilder().withData(serviceImpl.assignRoles(id, request)).build();
	}

  @GetMapping("/{id}/assigned-roles")
  @RequirePermission(menu = "test-page", action = "assign-roles")
  public ResponseEntity<AbstractResponse> getAssignedRoles(@PathVariable Long id) {
		return new ResponseBuilder().withData(serviceImpl.getAssignedRoles(id)).build();
	}

	@GetMapping("/role-assignments")
	@RequirePermission(menu = "test-page", action = "list")
	public ResponseEntity<AbstractResponse> getRoleAssignments() {
		return new ResponseBuilder().withData(serviceImpl.getUserRoleAssignments()).build();
	}

	@PostMapping("/download-excel")
	@RequirePermission(menu = "test-page", action = "export")
	public ResponseEntity<ByteArrayResource> downloadExcel(@RequestBody ExcelExportRequest request) {
	    // Generate the Excel file
	    ByteArrayOutputStream out = serviceImpl.generateExcel(request);

	    // Convert the byte array to a resource
	    ByteArrayResource resource = new ByteArrayResource(out.toByteArray());
	 
	    System.out.println("Byte array length: " + out.size());  // Log the length to confirm data

	    // Return the Excel file as a downloadable response
	    return ResponseEntity.ok()
	            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=report.xlsx")
	            .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
	            .contentLength(resource.contentLength())
	            .body(resource);
	}

}
