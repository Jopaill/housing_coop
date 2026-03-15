# ToDo
1. Create a simple GetMapping Spring-boot project that displays Hello-World in the browser.
2. Dockerize it.
3. Find somewhere to host it and how to establish HTTPS.
4. Host it.
5. Establish DevOps/CI/SDLC for the next iterations
6. Iterate (add new functionalities, push)

In particular, add a database layer (container)


# History

## Date: 2026-02-07**
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