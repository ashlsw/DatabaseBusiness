> Directions for Running the Program:

1. Open the project in IntelliJ IDEA. Make sure it is the Ultimate Edition.
2. Configure the project to use the specified JDK version 21
3. Set up the MySQL database and configure the database connection in the application.

> To set Database connection
- Update config.properties to set database connection credentials. 
- Input your personal username and password for your MySQLWorkbench.
```
USERNAME=<DATABASE_USERNAME>
PASSWORD=<DATABASE_PASSWORD>
```
- Create `business_management` database



4. Run the `DatabaseSetup.java`. This will create the database tables and populate it test data.
5. Run the `Main.java.` This will allow your GUI to be popped on your screen.
- [./sql/create.sql](./sql/create.sql) File will be generated. This file contains a dump of sql commands used to create the database table
