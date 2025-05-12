package ma.najid.annotationapp.service.impl;

import ma.najid.annotationapp.Model.TYPES.TypeRole;
import ma.najid.annotationapp.Model.UserAccount;
import ma.najid.annotationapp.repository.UserAccountRepository;
import ma.najid.annotationapp.service.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserAccountServiceImpl implements UserAccountService {
    private final UserAccountRepository userAccountRepository;

    @Autowired
    public UserAccountServiceImpl(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    @Override
    public UserAccount saveUserAccount(UserAccount userAccount) {
        return userAccountRepository.save(userAccount);
    }

    @Override
    public Optional<UserAccount> getUserAccountById(Long id) {
        return userAccountRepository.findById(id);
    }

    @Override
    public Optional<UserAccount> getUserAccountByEmail(String email) {
        return userAccountRepository.findByEmail(email);
    }

    @Override
    public List<UserAccount> getAllUserAccounts() {
        return userAccountRepository.findAll();
    }

    @Override
    public void deleteUserAccount(Long id) {
        userAccountRepository.deleteById(id);
    }

    @Override
    public  List<UserAccount> getAdmins() {
        return userAccountRepository.findAllByRole_NomRole(TypeRole.ADMIN_ROLE);
    }

    @Override
    public Optional<UserAccount> getAdmin() {
        return userAccountRepository.findAll().stream()
            .filter(u -> u.getRole() != null && u.getRole().getIdRole() != null && u.getRole().getIdRole().equals(1L))
            .findFirst();
    }
} 