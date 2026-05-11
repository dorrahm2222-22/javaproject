📋 Features

🔐 Authentication — Secure login for admins, teachers, and students
👨‍🎓 Student Management — Add, update, and delete student records
👨‍🏫 Teacher Management — Manage teacher profiles and assignments
📚 Subject Management — Create and manage school subjects (matières)
📝 Grade Management — Enter and track student grades (notes)
📊 Average Calculation — Automatically compute student averages (moyennes)
👤 User Profiles — View and edit user profile information

information


🗂️ Project Structure
JAVAPROJECT/
├── lib/
│   └── mysql-connector-j-9.5.0.jar   # MySQL JDBC driver
├── src/
│   ├── controleur/                    # Controllers (MVC logic layer)
│   │   ├── AuthControleur.java
│   │   ├── EnseignantControleur.java
│   │   ├── EtudiantControleur.java
│   │   ├── MatiereControleur.java
│   │   ├── MoyenneControleur.java
│   │   └── NoteControleur.java
│   ├── DAO/                           # Data Access Objects (database queries)
│   │   ├── AdminDAO.java
│   │   ├── EnseignantDAO.java
│   │   ├── EtudiantDAO.java
│   │   ├── MatiereDAO.java
│   │   ├── MoyennegDAO.java
│   │   └── NoteDAO.java
│   ├── modele/                        # Model classes (data entities)
│   │   ├── Admin.java
│   │   ├── Enseignant.java
│   │   ├── Etudiant.java
│   │   ├── Matiere.java
│   │   ├── Moyenneg.java
│   │   ├── Note.java
│   │   └── Utilisateur.java
│   ├── sql/                           # SQL scripts for database setup
│   │   ├── admin.sql
│   │   ├── enseingnant.sql
│   │   ├── etudiant.sql
│   │   ├── matiere.sql
│   │   ├── moyenneg.sql
│   │   ├── note.sql
│   │   └── trigger.sql
│   ├── util/
│   │   └── DBConnection.java          # Database connection utility
│   ├── vue/                           # GUI views (Swing panels & frames)
│   │   ├── LoginFrame.java
│   │   ├── MainFrame.java
│   │   ├── EnseignantPanel.java
│   │   ├── EtudiantPanel.java
│   │   ├── MatierePanel.java
│   │   ├── MoyennePanel.java
│   │   ├── NotePanel.java
│   │   └── ProfilPanel.java
│   └── App.java                       # Application entry point
└── .gitignore

🛠️ Prerequisites

Java JDK 11 or higher
MySQL 8.0 or higher
VS Code with the Java Extension Pack (or any Java IDE)
MySQL Connector/J 9.5.0 (already included in /lib)


⚙️ Setup & Installation
1. Clone the repository
bashgit clone https://github.com/dorrahm2222-22/javaproject.git
cd javaproject
2. Set up the database
Open MySQL and run the SQL scripts in the following order:
sqlsource src/sql/admin.sql
source src/sql/enseingnant.sql
source src/sql/etudiant.sql
source src/sql/matiere.sql
source src/sql/note.sql
source src/sql/moyenneg.sql
source src/sql/trigger.sql
3. Configure the database connection
Open src/util/DBConnection.java and update the credentials to match your MySQL setup:
javaString url = "jdbc:mysql://localhost:3306/systemedegestiondelecole";
String user = "root";
String password = "123456789";
4. Add the JDBC driver to your classpath
Make sure lib/mysql-connector-j-9.5.0.jar is included in your project's build path. In VS Code, this is handled via .vscode/settings.json.
5. Run the application
Compile and run App.java:
bashjavac -cp lib/mysql-connector-j-9.5.0.jar -d out src/**/*.java
java -cp out:lib/mysql-connector-j-9.5.0.jar App
Or simply press Run in VS Code.

🖥️ Usage

Launch the application — the Login screen will appear.
Sign in with your admin, teacher, or student credentials.
Use the navigation panel to access different modules:

Manage students, teachers, and subjects
Enter or view grades
Check computed averages
Edit your profile




🏗️ Architecture
This project follows the MVC (Model-View-Controller) pattern:
LayerPackageRoleModelmodele/Data entities (Etudiant, Note, etc.)Viewvue/Java Swing GUI panels and framesControllercontroleur/Business logic, bridges View and DAODAODAO/All SQL queries and database operationsUtilityutil/Shared helpers (DB connection)



