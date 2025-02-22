using System.ComponentModel.DataAnnotations;

namespace MicroSocial_Platform.Models
{
    public class UserProfileFormViewModel
    {
        [Required]
        [StringLength(50, ErrorMessage = "Username must be between 3 and 50 characters.", MinimumLength = 3)]
        public required string UserName { get; set; }

        [Required]
        [StringLength(200, ErrorMessage = "Bio must be less than 200 characters.")]
        public required string Bio { get; set; }

        [Required]
        public bool PrivateAccount { get; set; }

        // https://learn.microsoft.com/en-us/dotnet/api/microsoft.aspnetcore.http.iformfile?view=aspnetcore-8.0
        [Required]
        public required IFormFile? ProfilePicture { get; set; }

        // Documentatie pentru codul de mai jos:
        // https://learn.microsoft.com/en-us/dotnet/api/system.componentmodel.dataannotations.validationresult?view=net-9.0

        [Required(ErrorMessage = "Date of Birth is required.")]
        [DataType(DataType.Date)]
        // Creez o validare astfel incat un user sa nu aiba voie sa-si creeze cont daca are sub 16 ani
        [CustomValidation( typeof(UserProfileFormViewModel), nameof(ValidateAge)) ]
        public DateTime DateOfBirth { get; set; }

        public static ValidationResult? ValidateAge(DateTime dateOfBirth, UserProfileFormViewModel model)
        {
            int age = DateTime.Now.Year - dateOfBirth.Year;
            // Daca utilizator si-a sarbatorit deja ziua de nastere anul acesta
            if (dateOfBirth > DateTime.Now.AddYears(-age))
            {
                --age;
            }
            if(age < 16)
            {
                return new ValidationResult("You must be at least 16 years old in order to create an account!");
            }
            return ValidationResult.Success;
        }
    }
}
