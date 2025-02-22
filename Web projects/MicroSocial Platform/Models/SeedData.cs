using Microsoft.AspNetCore.Identity;
using Microsoft.EntityFrameworkCore;

namespace MicroSocial_Platform.Models
{
    public static class SeedData
    {
        public static void Initialize(IServiceProvider serviceProvider)
        {
            // Asemanator cu try with resources din java
            // Cand se termina blocul, se inchide conexiunea cu baza de date
            // Si se elibereaza resursele
            using (var appContext = new AppContext(serviceProvider.GetRequiredService< DbContextOptions <AppContext> >()))
            {
                // Nu suprascriem rolurile existente
                if(appContext.Roles.Any())
                {
                    return;
                }

                appContext.Roles.AddRange(
                    new IdentityRole { 
                        Id = "76D2B08F-33A5-4B6F-AD8D-74CB37B97C17", 
                        Name = "Guest",
                        NormalizedName = "Guest".ToUpper() 
                    },

                    new IdentityRole { 
                        Id = "37D017C2-09A8-48ED-B20B-DEE58C6D684C", 
                        Name = "User",
                        NormalizedName = "User".ToUpper() 
                    },

                    new IdentityRole { 
                        Id = "B7DE8B77-4DF3-41A8-82F3-7871B55355E3", 
                        Name = "Moderator",
                        NormalizedName = "Moderator".ToUpper() 
                    },

                    new IdentityRole
                    {
                        Id = "F665B68E-5CD3-4BE3-A3FE-35474C848D04",
                        Name = "Admin",
                        NormalizedName = "Admin".ToUpper()
                    }
                );

                // Folosesc implicit din .net https://en.wikipedia.org/wiki/PBKDF2
                var passwordHasher = new PasswordHasher<User>();
                appContext.Users.AddRange(
                    new User
                    {
                        Id = "3D7163EB-3C6F-4535-99BA-5F47CE480F10",
                        UserName = "Admin1",
                        EmailConfirmed = true,
                        NormalizedEmail = "ADMIN@ADMIN.COM",
                        Email = "admin@admin.com",
                        NormalizedUserName = "ADMIN1",
                        PasswordHash = passwordHasher.HashPassword(null, "Admin14325415531531"),
                        DateOfBirth = DateTime.Now,
                        ProfilePicture = [20, 20, 20, 20, 20],
                        Biography = "empty",
                        PrivateAccount = true
                    },
                    new User
                    {
                        Id = "2D7163EB-3B6F-4525-99BA-5247CE480F12",
                        UserName = "Admin2",
                        EmailConfirmed = true,
                        NormalizedEmail = "ADMIN2@ADMIN2.COM",
                        Email = "admin2@admin2.com",
                        NormalizedUserName = "ADMIN2",
                        PasswordHash = passwordHasher.HashPassword(null, "Admin14325415531531"),
                        DateOfBirth = DateTime.Now,
                        ProfilePicture = [20, 20, 20, 20, 20],
                        Biography = "empty",
                        PrivateAccount = true
                    }
                ) ;

                // Asociez admin-ul cu rolul sau
                appContext.UserRoles.AddRange(
                    new IdentityUserRole<string>
                    {
                        RoleId = "F665B68E-5CD3-4BE3-A3FE-35474C848D04",
                        UserId = "3D7163EB-3C6F-4535-99BA-5F47CE480F10"
                    }    
                );

                appContext.SaveChanges();
            }
        }
    }
}
