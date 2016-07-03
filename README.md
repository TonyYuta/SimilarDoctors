# SimilarDoctors

Upon execution, the program will generate an internal database, and populate said database with the information given to me in the CSV file.

The program will use the variables below to read and communicate information to the user:

int id = doctors id from the CSV file

String fName = doctors first name

String lName = doctors last name

String specialty = doctors specialty

String city = the city of the doctor

float reviewScore = doctors score


After generating and populating the internal database, the user will be prompted to enter the id of the doctor he is looking for. The program will then print the particular doctors details as well as provide a listing of similar doctors based on the following rules:

 	1. getting all doctors within specified city, of specified specialty and same or better rating

 	2. getting all doctors of specified specialty and same or better rating but not in the specified city

 	3. getting all doctors of specified specialty and in the same city but worse rating

 	4. getting all doctors of specified specialty but not in the same city and with the worse rating


Limiting the output to 100 doctors, can be changed later to fit pm needs.

Program can be executed from main method, while Unit tests are executed with testNG
