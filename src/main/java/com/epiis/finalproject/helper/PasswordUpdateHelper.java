package com.epiis.finalproject.helper;

import com.epiis.finalproject.dto.request.user.RequestUserUpdatePassword;
import com.epiis.finalproject.dto.response.user.ResponseUserUpdatePassword;
import com.epiis.finalproject.entity.EntityUser;
import com.epiis.finalproject.helper.DateUtil;
import com.epiis.finalproject.repository.RepositoryUser;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Centralizes the password-update logic that was duplicated across
 * BusinessStudent, BusinessProfessor and BusinessUser.
 */
@Component
public class PasswordUpdateHelper {

    private final RepositoryUser repositoryUser;
    private final PasswordEncoder passwordEncoder;

    public PasswordUpdateHelper(RepositoryUser repositoryUser, PasswordEncoder passwordEncoder) {
        this.repositoryUser = repositoryUser;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Updates the password for the user identified by the given email.
     *
     * @param email      the email of the user whose password will be changed
     * @param request    DTO containing the new plain-text password
     * @param successMsg message to include on success
     * @param errorMsg   message to include when the user is not found
     * @return a populated ResponseUserUpdatePassword
     */
    public ResponseUserUpdatePassword updatePassword(
            String email,
            RequestUserUpdatePassword request,
            String successMsg,
            String errorMsg) {

        ResponseUserUpdatePassword response = new ResponseUserUpdatePassword();

        Optional<EntityUser> optional = repositoryUser.findByEmail(email);

        if (optional.isPresent()) {
            EntityUser entityUser = optional.get();
            entityUser.setPassword(passwordEncoder.encode(request.getPassword()));
            entityUser.setUpdatedAt(DateUtil.currentSqlDate());
            repositoryUser.save(entityUser);

            response.success();
            response.getListMessage().add(successMsg);
            return response;
        }

        response.error();
        response.getListMessage().add(errorMsg);
        return response;
    }
}
