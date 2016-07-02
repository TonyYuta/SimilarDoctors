# SimilarDoctors

Similar Doctors

Problem Statement
A site contains a listing of doctors. Users can browse for doctors given a specific specialty, 
area, review score etc. 
Write a class which when given a doctor, provides a list of similar doctors, in 
a prioritized order. 
You define what similar means and the result ordering, but clearly document any 
assumptions in your code. 
Please, include unit tests. 
You can assume the entire directory of doctors fits into memory, and write in whatever 
language you are most comfortable with. 


 * ---------- SOLUTION -------------------

 Creating data base with fields:
	id,	fName,	lName,	specialty,	city,	reviewScore
 Populating data base using csv file
 Choosing doctor by id and printing out doctor's info
 Finding similar doctors using 4 rulers:
 	1. getting all doctors within specified city, of specified specialty and same or better rating
 	2. getting all doctors of specified specialty and same or better rating but not in the specified city
 	3. getting all doctors of specified specialty and in the same city but worse rating
 	4. getting all doctors of specified specialty but not in the same city and with the worse rating
 All chosen doctors putting in list no longer than 100 doctors and printing out
