# Housing Coop project

## ToDo
* Modify how the users are initialized in the `UserDetailsManager`
* Come up with a definition of a Minimal Viable Product

## Deployments

    ### Prod
    --------
    1. Run `gradle bootjar`
    2. Open CMD at root level and run `fly deploy`

    ### Local - From IntelliJ
    -------------------------
    Run `gradle bootRun` (if that fails, make sure to add the email secret as 'VM options')
    Note: IntelliJ setup config changes to facilitate local dev:
     - We enabled `Build Project Automatically`
     - We enabled `Allow auto-make to start ...` in advanced settings
     - `spring-boot-devtools` dependency
     - `bootRun` Gradle configs specifies the `sourceResources` (faster updates on UI)
     - We added an extension on Edge called `LiveReload` to see the changes more quickly

    ### Local - From terminal
    -------------------------
    Open the `src` folder in CMD
    `set POSTMARK_API_TOKEN=55c462...` (See the exact value one folder up than `src` (not version controlled)
    `gradlew bootRun

## Useful Fly Commands

    ### Login into the host and read the logs
    -----------------------------------------
    Open the Windows terminal at the root level of this project and run `fly ssh console`

    ### List the secrets
    --------------------
    `fly secrets list`
    `fly secrets set MY_SECRET_KEY=supersecretvalue`
    `fly secrets unset MY_SECRET_KEY`

    ### To get the exact value of a secret
    --------------------
    `fly ssh console`
    `echo $YOUR_SECRET_NAME`

## H2 Database in localhost
After launching the app, navigate to `http://localhost:8080/h2-console`

## PostgreSQL Database in Prod
Steps: 
1. Open a terminal in the `src` folder of this project
2. Start a local proxy to Fly Postgres app:
   `fly proxy 5432 -a housing-coop-db`
   or use another local port if 5432 is already busy:
   `fly proxy 5433:5432 -a housing-coop-db` 
   This forwards your local port to the database so DBeaver can connect to localhost.
3. In DBeaver, create a new PostgreSQL connection with:
   **Host**: localhost
   **Port**: 5432 or 5433 depending on what you used
   **Database**: usually postgres, unless your app uses a different database name
   **Username**: usually postgres
   **Password**: your Fly Postgres password / OPERATOR_PASSWORD if that is the one you have access to.












## History

### Date 2026-09-27
Migration towards Tailwind CSS

Generally, I didn't enjoy working with AI to generate all my frontend...
So, I'm restarting it with Tailwind CSS.

Change 1: Added a few files to help the work on the Frontend:
* package-lock.json
* package.json
* .prettierrc.json
* .prettierignore
  Change 2: Reworked the common.css and added very few common styles
  Change 3: Reworked the `login.html` and started to work on `create-account.html`

Overall, this leaves the project kind of broken at the moment. I'll need to work a lot to put it back together, but that's okay. Here's the first commit to this goal.

### Date: 2026-07-24
Presentation of the website to the CA and agreement to eventually go live.

### Date: 2026-07-20
 * Change 0: Removing the historic "testuser" as it isn't safe and I started to add PII info
 * Change 1: Translating..
   * the content of the pages "forgot-password", "login.html", "reset-password" into French
   * the email sent to reset passwords
 * Change 2: Adding a new page "create-account" where users can create their account:
   * Changes to SecurityConfigs chain
   * Added the endpoints to PasswordResetController
 * Change 3: Added logs when user tries to reset their email with a wrong email.
 * Change 4: Added a logout feature
 * Change 5: Added some README.md details to connect to DB from local

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
