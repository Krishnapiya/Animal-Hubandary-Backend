package com.keltron.admin.services.impl;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.keltron.admin.entity.UserRoleMap;
import com.keltron.admin.repository.OfficeRepository;
import com.keltron.admin.repository.RoleMasterRepository;
import com.keltron.admin.repository.UserRoleMapRepository;
import com.keltron.admin.repository.UsersRepository;
import com.keltron.admin.request.AssignRolesRequest;
import com.keltron.admin.request.OfficeRoleGroup;
import com.keltron.utility.annotations.WriteTransactional;
import com.keltron.utility.beans.dto.UsersDto;
import com.keltron.utility.jpa.entity.Office;
import com.keltron.utility.jpa.entity.RoleMaster;
import com.keltron.utility.jpa.entity.Users;
import com.keltron.utility.manage.service.abs.AbstractJpaService;
import com.keltron.utility.manage.service.abs.ExcelExportUtil;
import com.keltron.utility.requests.ExcelExportRequest;
import com.keltron.utility.responses.RestException;

import jakarta.persistence.EntityNotFoundException;

@Service
public class UsersServiceImpl extends AbstractJpaService<UsersDto, Long, UsersRepository, Users> {

    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private UserRoleMapRepository userRoleMapRepository;
    @Autowired
    private RoleMasterRepository roleMasterRepository;
    @Autowired
    private OfficeRepository officeRepository;

    @Transactional(readOnly = true)
    public ByteArrayOutputStream generateExcel(ExcelExportRequest request) {

        List<Users> users = usersRepository.findAll();

        List<UsersDto> dtos = users.stream()
            .map(Users::toDTO)
            .toList();

        return ExcelExportUtil.generateExcel(dtos, request.getXls_config());
    }

    @Override
    @WriteTransactional
    public Users save(UsersDto dto) {
        return super.save(dto);
    }

    @Override
    @WriteTransactional
    public Users update(Long id, UsersDto dto) {
        return super.update(id, dto);
    }

    @Transactional
    public Map<String, Object> assignRoles(Long userId, AssignRolesRequest request) {
        Users user = usersRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));

        userRoleMapRepository.deleteAllByUserId(userId);

        List<Integer> flat = new ArrayList<>();
        if (request.getOfficeRoleGroups() != null && !request.getOfficeRoleGroups().isEmpty()) {
            for (OfficeRoleGroup g : request.getOfficeRoleGroups()) {
                if (g.getOfficeId() == null) {
                    throw new RestException("Each role group must include officeId", HttpStatus.BAD_REQUEST);
                }
                Office officeEntity = officeRepository.getReferenceById(g.getOfficeId());
                List<Integer> rids = g.getRoleIds() == null ? List.of() : g.getRoleIds();
                for (Integer roleId : rids.stream().distinct().toList()) {
                    RoleMaster role = roleMasterRepository.findById(roleId)
                        .orElseThrow(() -> new EntityNotFoundException("Role not found: " + roleId));
                    UserRoleMap map = new UserRoleMap();
                    map.setUser(user);
                    map.setRole(role);
                    map.setOffice(officeEntity);
                    userRoleMapRepository.save(map);
                    flat.add(roleId);
                }
            }
        } else if (request.getRoleIds() != null && !request.getRoleIds().isEmpty()) {
            throw new RestException(
                "Use officeRoleGroups with officeId for each entry; global roles are not supported",
                HttpStatus.BAD_REQUEST);
        }

        if (!flat.isEmpty()) {
            usersRepository.updatePrimaryRole(userId, flat.get(0));
        }

        return Map.of("userId", userId, "roleIds", flat);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getAssignedRoles(Long userId) {
        List<UserRoleMap> maps = userRoleMapRepository.findAllByUserIdWithRelations(userId);
        Map<String, List<Integer>> grouped = new LinkedHashMap<>();
        Map<String, String> groupOfficeName = new LinkedHashMap<>();
        for (UserRoleMap m : maps) {
            Integer oid = m.getOffice() != null ? m.getOffice().getId() : null;
            String key = oid == null ? "null" : ("id:" + oid);
            grouped.computeIfAbsent(key, k -> new ArrayList<>()).add(m.getRole().getId());
            if (m.getOffice() != null && m.getOffice().getName() != null) {
                groupOfficeName.putIfAbsent(key, m.getOffice().getName());
            }
        }
        List<Map<String, Object>> officeRoleGroups = new ArrayList<>();
        for (Map.Entry<String, List<Integer>> e : grouped.entrySet()) {
            if ("null".equals(e.getKey())) {
                continue;
            }
            Map<String, Object> row = new LinkedHashMap<>();
            int id = Integer.parseInt(e.getKey().replace("id:", ""));
            row.put("officeId", id);
            row.put("officeName", groupOfficeName.get(e.getKey()));
            row.put("roleIds", e.getValue().stream().distinct().toList());
            officeRoleGroups.add(row);
        }
        List<Integer> flatRoleIds = maps.stream().map(m -> m.getRole().getId()).distinct().toList();
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("userId", userId);
        out.put("officeRoleGroups", officeRoleGroups);
        out.put("roleIds", flatRoleIds);
        return out;
    }

    @Transactional(readOnly = true)
    public Map<Long, List<String>> getUserRoleAssignments() {
        Map<Long, List<String>> roleMap = new java.util.HashMap<>();
        for (Object[] pair : usersRepository.findPrimaryRolePairs()) {
            Long uid = ((Number) pair[0]).longValue();
            String roleName = pair[1] != null ? String.valueOf(pair[1]) : null;
            roleMap.putIfAbsent(uid, new ArrayList<>());
            if (roleName != null && !roleName.isBlank()) {
                roleMap.get(uid).add(roleName);
            }
        }
        for (Object[] pair : userRoleMapRepository.findAllUserRolePairs()) {
            Long uid = ((Number) pair[0]).longValue();
            String label = String.valueOf(pair[1]);
            roleMap.putIfAbsent(uid, new ArrayList<>());
            if (!roleMap.get(uid).contains(label)) {
                roleMap.get(uid).add(label);
            }
        }
        return roleMap;
    }
}
