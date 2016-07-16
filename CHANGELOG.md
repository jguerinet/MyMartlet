# Change Log

## Version 2.3.0 (2016-07-16)
* New: Support for Android Nougat
* Fixed course search
* Fixed bug when removing favorite place

## Version 2.2.5 (2016-04-05)
* Fix: Fix login issue with regards to the new proxy cookie Minerva sets 

## Version 2.2.4 (2016-03-10)
* Fix: Reverted Instabug to 1.x to fix the multiple crashes that 2.x was causing 

## Version 2.2.3 (2016-03-08)
* Fix: Fixed crash when parsing Sunday as the day of the week a course is offered (character was recently changed)
* Fix: Fixed crash because of wrong order of email and LinkedIn info in the About section 
* Fix: Fixed crash where list converters would not be found on Android 4.x
* Update: Started ignoring some unnecessary exceptions when sending crashes to Crashlytics

## Version 2.2.2 (2016-03-08)
* Fix: Fixed crash that happened when trying to update a semester on courses that haven't been loaded from storage yet 

## Version 2.2.1 (2016-03-08)
* Fix: Attempt to fix the missing converter for the schedule on some devices
* Update: Updated to support libraries v23.2.0, now using VectorDrawables everywhere

## Version 2.2.0 (2016-03-02)
* Completely refactored the downloading and parsing of the info to be quicker and more efficient
* Made the loading screen much faster 
* Stopped using Gson, starting using Moshi
* Added button to become a beta tester in the app
* Added GitHub button 
* Now redirecting to the McGill walkthrough for setting up the email 
* Switched to Instabug 2.0
* Added some managers for some of the data to make it more synchronous and less error prone
* Added option to display the schedule in 24 hour format 
* Removed the user info that was unused 
* Unified app icon
* A ton of small bug fixes

## Version 2.1.0 (2016-02-06)
* Started using Dagger for dependency injection almost everywhere
* Removed Hungarian notation from all of the models
* Replaced Joda Time with Android Three Ten 
* Removed unused fields on some models
* Temporarily disabled the landscape orientation fo the schedule because it is not rendering correctly since the activity switch

## Version 2.0.3 (2016-01-28)
* Switched from fragments to activities
* Set up dependency to the Android Utils library 

## Version 2.0.2 (2016-01-12)
* Fixed some reported crashes
* Fixed bug where about page would not load
* Fixed bug where transcript would not load on pre-Lollipop devices
* Removed unnecessary color state lists 
* Refreshed the Settings UI

## Version 2.0.1 (2015-11-20)
* Open sourced the whole project
* Switched to min SDK 15
* Switched to Gradle
* Removed FontAwesome and switched to material design VectorDrawables
* Fixed Facebook sharing
* Fixed Twitter sharing
* Added Crashlytics
* Added Instabug
* Significantly optimized the Connection class 
* Fixed bug where app would crash if ebill was empty
* Set up the Marshmallow permissions
* Refactored the whole code base
* Minor UI tweaks

## Version 2.0.0 (2015-02-10)
* Fixed dev mode

## Version 2.0.0 (2015-02-10)
* Removed wishlist option for semesters that you can't currently register for

## Version 2.0.0 (2015-02-09)
* Fixed NPE crash reported on Google Play (NPE on class list)

## Version 2.0.0 (2015-02-08)
* Bug fixes
* Material design
* Added a Search Functionality to the Map
* Added new categories to filter by on the map
* Added an End User License Agreement (EULA)

## Version 1.0.1 (2014-09-24)
* Fixed bug where the current date would be shown even if you chose a semester in the past or future

## Version 1.0.1 (2014-09-23)
* Loading screen only updates the essentials (current/future semesters + transcript + ebill)
* Added skip button on loading screen
* Date is now taken into account for classes
* Fixed some parsing bugs
* Added close button to walkthrough
* Removed the default faculty in walkthrough
* Can now download files from MyCourses
* Fixed bug where logging out would not log you out of MyCourses

## Version 1.0.0 (2014-09-08)
* Initial Release