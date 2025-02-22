function togglePasswordVisibility(button) {
    const passwordInput = button.previousElementSibling;

    if (passwordInput.type === "password") {
        passwordInput.type = "text";
        button.textContent = "Hide"; // Change button text to "Hide"
    } else {
        passwordInput.type = "password";
        button.textContent = "Show"; // Change button text to "Show"
    }
}