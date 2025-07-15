package vubq.warehouse_management.VT_EcoStorage.services;

import org.springframework.data.domain.Page;
import vubq.warehouse_management.VT_EcoStorage.dtos.UserDto;
import vubq.warehouse_management.VT_EcoStorage.entities.User;
import vubq.warehouse_management.VT_EcoStorage.utils.https.DataTableRequest;

public interface UserService {

    UserDto createOrUpdateUser(UserDto userDto);

    UserDto getUser(String userId);

    Page<User> getListUser(DataTableRequest dataTableRequest, String status);
}
