package ma.najid.annotationapp.Controller;

import ma.najid.annotationapp.Model.UserAccount;
import ma.najid.annotationapp.service.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/useraccounts")
public class UserAccountController {
    private final UserAccountService userAccountService;

    @Autowired
    public UserAccountController(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }


    @GetMapping
    public List<UserAccount> getAllUserAccounts() {
        return userAccountService.getAllUserAccounts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserAccount> getUserAccountById(@PathVariable Long id) {
        Optional<UserAccount> userOpt = userAccountService.getUserAccountById(id);
        return userOpt.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<UserAccount> createUserAccount(@RequestBody UserAccount userAccount) {
        UserAccount created = userAccountService.saveUserAccount(userAccount);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserAccount> updateUserAccount(@PathVariable Long id, @RequestBody UserAccount userAccount) {
        Optional<UserAccount> existing = userAccountService.getUserAccountById(id);
        if (existing.isPresent()) {
            userAccount.setIdUser(id);
            UserAccount updated = userAccountService.saveUserAccount(userAccount);
            return ResponseEntity.ok(updated);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserAccount(@PathVariable Long id) {
        userAccountService.deleteUserAccount(id);
        return ResponseEntity.noContent().build();
    }
} 