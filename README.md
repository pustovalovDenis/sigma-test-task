SCHOOL-SYSTEM-SERVICE
======================
## General
School administrative system, group management. 

### v1.0.0
- initial version

### How to test
For testing you can run SchoolTest.java -> checkSchoolStudentsAssignProcessor it's main test that describe main logic process
 it create all entities that we need and make relation between them and check that everything looks fine)

## My Assumptions
 I assume next:
  1) Every class has a list of levels
  2) Every group has a level.
  3) We can mapped group to class using level logic.
  4) Every student has a level.
  5) We can assign a user to a group using this level on the first step and on the second step we trying to find a group that has most interceptions by subjects with a student.
  
### TODO 
 - remove code duplications.
 - add swagger
 - add more tests 
 - add endpoints to run logic that assign students into groups and assign group into classes
 - increase tests coverage
 - add prometheus metric for check performance and health checks
 - add simple UI for creating entities
 - add message broker for notification a headmaster about all unplaced students
 
### Manually testing REST endpoints 
1. Start the backend with the intended environment.
2. Open url `http://localhost:8080/classToGroups` for create relation between class and groups.
3. Open url `http://localhost:8080/groups` CRUD for groups.
4. Open url `http://localhost:8080/groupToSubjects` for create relation between subjects and groups.
5. Open url `http://localhost:8080/studentsToGroup` for create relation between students and groups.
6. Open url `http://localhost:8080/subjectsToStudent` for create relation between students and students.
7. Open url `http://localhost:8080/classes` CRUD for classes.
8. Open url `http://localhost:8080/students` CRUD for students.
9. Open url `http://localhost:8080/subjects` CRUD for subjects.
10. Open url `http://localhost:8080/subjects` CRUD for subjects.

## Technologies
	 1. Spring boot
	 2. Spring WEB
	 3. Spring Data
	 4. MockMVC for testing
	 5. JUnit for testing
	 6. H2 (MsSQL dialect)
	 7. Lombok
	 8. mapstruct
	 9. Java 8
	 10. Maven


