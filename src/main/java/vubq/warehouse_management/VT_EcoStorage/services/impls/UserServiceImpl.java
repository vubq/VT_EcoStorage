package vubq.warehouse_management.VT_EcoStorage.services.impls;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vubq.warehouse_management.VT_EcoStorage.dtos.UserDto;
import vubq.warehouse_management.VT_EcoStorage.entities.*;
import vubq.warehouse_management.VT_EcoStorage.repositories.SystemPermissionGroupRepository;
import vubq.warehouse_management.VT_EcoStorage.repositories.UserPermissionGroupRepository;
import vubq.warehouse_management.VT_EcoStorage.repositories.UserPermissionRepository;
import vubq.warehouse_management.VT_EcoStorage.repositories.UserRepository;
import vubq.warehouse_management.VT_EcoStorage.services.UserService;
import vubq.warehouse_management.VT_EcoStorage.utils.https.DataTableRequest;
import vubq.warehouse_management.VT_EcoStorage.utils.specifications.BaseSpecification;
import vubq.warehouse_management.VT_EcoStorage.utils.specifications.SearchCriteria;
import vubq.warehouse_management.VT_EcoStorage.utils.specifications.SearchOperation;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserPermissionRepository userPermissionRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserPermissionGroupRepository userPermissionGroupRepository;
    private final SystemPermissionGroupRepository systemPermissionGroupRepository;

    @Override
    public UserDto getUser(String userId) {
        UserDto userDto = new UserDto();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        if (user != null) {
            userDto = UserDto.toDto(user);
            List<UserPermission> userPermissions = this.userPermissionRepository
                    .findAllById_UserId(userId);
            List<String> permissionIds = userPermissions.stream()
                    .map(detail -> detail.getSystemPermission().getId())
                    .toList();
            userDto.setPermissions(permissionIds);

            userDto.setPermissionGroups(userPermissionGroupRepository.findSystemPermissionGroupIdsByUserId(userId));
        }
        return userDto;
    }

    @Override
    public UserDto createOrUpdateUser(UserDto userDto) {
        User user;
        if (StringUtils.isEmpty(userDto.getId())) {
            user = new User();
            user.setStatus(User.Status.ACTIVE);
        } else {
            user = userRepository.findById(userDto.getId())
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + userDto.getId()));
        }
        user.setUsername(userDto.getUsername());
        user.setPassword(StringUtils.isEmpty(userDto.getPassword()) ? user.getPassword() : passwordEncoder.encode(userDto.getPassword()));
        user.setEmail(userDto.getEmail());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setPhoneNumber(userDto.getPhoneNumber());
        user.setNote(userDto.getNote());

        user = userRepository.saveAndFlush(user);

        userPermissionRepository.deleteByUserId(user.getId());
        userPermissionRepository.flush();

        User finalUser = user;
        List<UserPermission> userPermissions = userDto.getPermissions().stream()
                .map(permission -> UserPermission.builder()
                        .id(new UserPermissionId(finalUser.getId(), permission))
                        .user(finalUser)
                        .systemPermission(SystemPermission.builder().id(permission).build())
                        .build())
                .toList();

        this.userPermissionRepository.saveAllAndFlush(userPermissions);

        userPermissionGroupRepository.deleteAllByUserId(userDto.getId());
        List<UserPermissionGroup> newGroups = userDto.getPermissionGroups().stream()
                .map(groupId -> {
                    SystemPermissionGroup group = systemPermissionGroupRepository.findById(groupId)
                            .orElseThrow(() -> new RuntimeException("Nhom quyen khong ton tai"));

                    return UserPermissionGroup.builder()
                            .id(new UserPermissionGroupId(userDto.getId(), groupId))
                            .user(finalUser)
                            .systemPermissionGroup(group)
                            .build();
                }).toList();

        if (!newGroups.isEmpty()) {
            userPermissionGroupRepository.saveAll(newGroups);
        }
        return UserDto.toDto(user);
    }

    @Override
    public Page<User> getListUser(DataTableRequest dataTableRequest) {
        PageRequest pageable = dataTableRequest.toPageable();
        BaseSpecification<User> specUsernameContains = new BaseSpecification<>(
                SearchCriteria.builder()
                        .keys(new String[]{User.Fields.username})
                        .operation(SearchOperation.CONTAINS)
                        .value(dataTableRequest.getFilter().trim().toUpperCase())
                        .build()
        );
        return userRepository.findAll(Specification.where(specUsernameContains), pageable);
    }
}
