CourseCalendar Notes
Author: Selim Belhaouane

I wanted to do/avoid several things:
	
	1. Avoid introducing dependencies by using additional packages like iCal4j or biweekly. 
	   While this would have helped shorten the code, I was not sure how much trouble it was to 
	   add something like that to an android application, on top of the licensing. 
	
	2. I wanted to keep everything in one file/class to improve readability. 
	   I usually don't do this, but I also wrote the class such that splitting it would not 
	   be too much work. 

	3. I stick to a 80 character limit. 

I checked the output ics files on http://icalvalid.cloudapp.net/

I tried to write the class in a top-down format, such that the methods at the top call on the methods below, but
not the opposite. 