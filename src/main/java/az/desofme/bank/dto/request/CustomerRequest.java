package az.desofme.bank.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerRequest {
    @NotBlank(message = "Name can't be empty")
    @Size(min = 3, message = "Name can't be empty 3 characters")
    private String name;

    @NotBlank(message = "Surname can't be empty")
    @Size(min = 3, message = "Surname can't be empty 3 characters")
    private String surname;

    @NotBlank(message = "Email can't be empty")
    @Email(regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}", message = "Invalid email format")
    @Size(min = 6, message = "Email can't be empty 6 characters")
    private String email;

    @NotBlank(message = "Password can't be empty")
    @Size(min = 8, message = "Password can't be empty 3 characters")
    private String password;

    @NotBlank(message = "Password can't be empty")
    @Size(min = 7, max = 7, message = "Password must be 7 characters")
    private String pin;
}
