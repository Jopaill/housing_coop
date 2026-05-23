# Housing Coop project

## Deployments
1. Run `gradle bootjar`
2. Open CMD at root level and run `fly deploy`

## Useful Fly Commands

  ### Login into the host and read the logs
Open the Windows terminal at the root level of this project and run `fly ssh console`

  ### List the secrets
`fly secrets list`
`fly secrets set MY_SECRET_KEY=supersecretvalue`
`fly secrets unset MY_SECRET_KEY`

  ### To get the exact value of a secret
```
fly ssh console
echo $YOUR_SECRET_NAME
```

## H2 Database in localhost
After launching the app, navigate to `http://localhost:8080/h2-console`

## PostgreSQL Database in Prod
(Detail how to connect to it!)

## ToDo
 * Modify how the users are initialized in the `UserDetailsManager`
 * Come up with a definition of a Minimal Viable Product
 * Come up with a Data Model for the coop assuming this MVP 


## History

### Date: 2026-05-22
 * In `SecurityConfigs.java`, to not add "testuser" if he's already been added
 * Removal of `EmailServiceImplMailerSend` and replacing it with `EmailServiceImplPostmark`
 * Adding Postmark configs
 * Changing the Rolling policy to monthly and lowered the amount of generated logs
 * Replaced the mail dependency
 * Changed the `restart` policy for fly.io to never restart on failure.
 * Added README details

### Date: 2026-05-17
Adding EmailService interface and fixing teh schema-dev.sql
 * Creation of an EmailService interface. (o eventually migrate EmailService)
 * Fixing the schema-dev.sql
 * Editing the README.markdown 

### Date: 2026-05-15 (part 2)
Implementation of the forgot-password functionality
 * Fixed my SecurityChain to allow /forgot-password api
 * Added PasswordResetController
 * Added PasswordResetDao
 * Added PasswordResetToken
 * Created a proper EmailService
 * Modified the UserService to allow reset-password functionality
 * Added 2 html: forgot-password.html, reset-password.html
 * Added a button "forgot password" on login.html
 * Divided the schemas `schema-dev` and `schema-prod` because the H2 and PostgreSQL
 * Added more properties to dev and prod
 * Added a bit more details to the README.markdown

### Date: 2026-05-15
 * Using the JdbcUserDetailsManager as a UserDetailsManager.
 * Modified the SecurityConfigs
 * Added application-dev.properties configs
 * Added schema-dev.sql and deleted the content of data-dev.sql


### Date: 2026-05-14
 * Modified the `UserDetailsManager` to get the users from the db using a new userService
 * Added a simple UserService
 * Added a `UserDao` that prints all the users during its construction
 * Added a `TestingSendingMail` that test the email sending functionality (tested it and it worked)
 * Migrated from `logback.xml` to `logback-spring.xml` so I can use `springProfile`
 * Added 2 profiles `dev` and `prod` via `application-dev.properties` and `application-prod.properties`
 * Added a `data-dev.sql` to initialize data in my `dev` environment
 * Added db and mail dependencies
 * Added the Spring profile to my Dockerfile
 * Modified my fly.toml to only have 512 mb of ram

### Date: 2026-04-30
 * Deleted the previous application in fly.io and recreated it. The name was previously automatically generated, now it's "housing-coop"
 * Set up the cloudflare DNS records accordingly
 * Changed the logs from ConsoleAppender to FileAppender
 * Changing the fly.toml to always have the app running
 * Using Spring's ResponseCookie to create a cookie string
 * Created a new "https://www.mailersend.com/" account and started using it to send emails

### Date: 2026-04-18
 * SecurityConfigs: Set up my SecurityFilterChain so that all requests to "/login" are permitted, but all others require JWT authentication
 * Added the LoginController to segregate the logic
 * Added the simple login.html


### Date: 2026-02-07
  * Logged back into GitHub (first time in a while)
  * Cleaned up my GitHub (removed all old repos)
  * Created the `housing_coop` project with an initial commit. Chose the MIT license
  * Deleted Notepad++, Git, IntelliJ, jdk. Then redownloaded a fresher versions:
    * Notepad++ --- 8.9.1
    * Git       --- v2.53.0
    * IntelliJ  --- 2025.03.2
    * openjdk   --- "25.0.2" 2026-01-20
  * Removed the Organizations access "The-Software-Guild"
  * Pulled the code in my local
  * Pushed local changes (README.md) GitHub online repo
  * Downloaded PostgreSQL
  
### Date: 2026-03-13
 * Installed Docker Desktop 4.64.0" (which installed also `Windows Subsystem for Linux` (WSL))

### Date: 2026-03-15
I used the Spring initializer to add a basic structure to my project:
* Gradle wrapper 9.3.1 with all standard Gradle files
* `build.gradle.kts` file
* The folders (packages) created
* I added the `spring-boot-starter-web` dependency
* Added some html (index.html)
* Launched the Spring app and reached the index.html on my local

### Date: 2026-03-28
* Bought the domain coopjm.ca on Cloudflare
* Launched my application in fly.io and configured the ipv4/6 records and https to reroute to my docker app on fly.io
* Added a new homepage (under construction)
* Added `spring-boot-starter-security`, `lombok`, `logback`
* Made sure the tests ran fine
* Configured my `logback.xml` for main and test
* Added `spring-boot-starter-security` configurations such as the `PasswordEncoder` and the `UserDetailsManager`
