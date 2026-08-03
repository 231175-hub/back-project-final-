package com.epiis.finalproject.helper;

import com.epiis.finalproject.entity.EntityUser;
import com.epiis.finalproject.generic.ResponseGeneric;
import com.epiis.finalproject.repository.RepositoryUser;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Centralizes the find-update-save and delete boilerplate that was duplicated
 * across BusinessStudent, BusinessProfessor and BusinessUser.
 */
public final class UserMutationHelper {

    private UserMutationHelper() {
        // utility class
    }

    /**
     * Creates and populates a new {@link EntityUser} with the common person fields,
     * an auto-generated id and current timestamps.
     */
    public static EntityUser buildEntityUser(String firstName, String surName, String email, String password,
            PasswordEncoder passwordEncoder) {
        EntityUser entityUser = new EntityUser();
        entityUser.setIdUser(UUID.randomUUID().toString());
        entityUser.setFirstName(firstName);
        entityUser.setSurName(surName);
        entityUser.setEmail(email);
        entityUser.setPassword(passwordEncoder.encode(password));
        entityUser.setCreatedAt(DateUtil.currentSqlDate());
        entityUser.setUpdatedAt(entityUser.getCreatedAt());
        return entityUser;
    }

    /**
     * Finds the user by id and applies the given changes to it. The changes may
     * persist related entities (keeping the original save order). The response is
     * populated with the given success or error message.
     *
     * @param response        the generic response object to mutate
     * @param applyChanges    callback that receives the managed user and performs
     *                        the field updates (and any repository saves)
     * @return the populated response
     */
    public static <T extends ResponseGeneric> T updateUser(
            RepositoryUser repositoryUser,
            T response,
            String idUser,
            Consumer<EntityUser> applyChanges,
            String successMessage,
            String errorMessage) {

        Optional<EntityUser> optional = repositoryUser.findById(idUser);

        if (optional.isPresent()) {
            applyChanges.accept(optional.get());

            response.success();
            response.getListMessage().add(successMessage);
            return response;
        }

        response.error();
        response.getListMessage().add(errorMessage);
        return response;
    }

    /**
     * Executes the given changes only when the entity exists, populating the
     * response with the given success or error message otherwise.
     *
     * @param response       the generic response object to mutate
     * @param existing       the entity lookup result to test
     * @param applyChanges   callback that receives the lookup result and performs
     *                       the updates when it is present
     * @return the populated response
     */
    public static <T extends ResponseGeneric, E> T updateExisting(
            T response,
            Optional<E> existing,
            Consumer<Optional<E>> applyChanges,
            String successMessage,
            String errorMessage) {

        if (existing.isPresent()) {
            applyChanges.accept(existing);

            response.success();
            response.getListMessage().add(successMessage);
            return response;
        }

        response.error();
        response.getListMessage().add(errorMessage);
        return response;
    }

    /**
     * Executes a delete and populates a success response.
     */
    public static <T extends ResponseGeneric> T deleteById(
            T response,
            String id,
            Consumer<String> deleter,
            String successMessage) {

        deleter.accept(id);
        response.success();
        response.getListMessage().add(successMessage);
        return response;
    }
}
