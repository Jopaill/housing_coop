# Deployments
1. Run `gradle bootjar`
2. Open CMD at root level and run `fly deploy`

# ToDo
- Add a database layer (container) and store users there. 
- Modify the UserService to fetch from DB


# History

## Date: 2026-04-18
 - SecurityConfigs: Set up my SecurityFilterChain so that all requests to "/login" are permitted, but all others require JWT authentication
 - Added the LoginController to segregate the logic
 - Added the simple login.html


## Date: 2026-02-07
  - Logged back into GitHub (first time in a while)
  - Cleaned up my GitHub (removed all old repos)
  - Created the `housing_coop` project with an initial commit. Chose the MIT license
  - Deleted Notepad++, Git, IntelliJ, jdk. Then redownloaded a fresher versions:
    - Notepad++ --- 8.9.1
    - Git       --- v2.53.0
    - IntelliJ  --- 2025.03.2
    - openjdk   --- "25.0.2" 2026-01-20
  - Removed the Organizations access "The-Software-Guild"
  - Pulled the code in my local
  - Pushed local changes (README.md) GitHub online repo
  - Downloaded PostgreSQL
  
## Date: 2026-03-13
 - Installed Docker Desktop 4.64.0" (which installed also `Windows Subsystem for Linux` (WSL))

## Date: 2026-03-15
I used the Spring initializer to add a basic structure to my project:
* Gradle wrapper 9.3.1 with all standard Gradle files
* `build.gradle.kts` file
* The folders (packages) created

Then,
* I added the `spring-boot-starter-web` dependency
* Added some html (index.html)
* Launched the Spring app and reached the index.html on my local

## Date: 2026-03-28
* Bought the domain coopjm.ca on Cloudflare
* Launched my application in fly.io and configured the ipv4/6 records and https to reroute to my docker app on fly.io
* Added a new homepage (under construction)
* Added `spring-boot-starter-security`, `lombok`, `logback`
* Made sure the tests ran fine
* Configured my `logback.xml` for main and test
* Added `spring-boot-starter-security` configurations such as the `PasswordEncoder` and the `UserDetailsManager`
