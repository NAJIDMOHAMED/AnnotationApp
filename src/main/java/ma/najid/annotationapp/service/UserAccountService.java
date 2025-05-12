package ma.najid.annotationapp.service;

import ma.najid.annotationapp.Model.UserAccount;
import java.util.List;
import java.util.Optional;

public interface UserAccountService {
    UserAccount saveUserAccount(UserAccount userAccount);
    Optional<UserAccount> getUserAccountById(Long id);
    Optional<UserAccount> getUserAccountByEmail(String email);
    List<UserAccount> getAllUserAccounts();
    void deleteUserAccount(Long id);

    List<UserAccount> getAdmins();

    Optional<UserAccount> getAdmin();
} 