Step 1
Unzip the Test Directory and follow the readme instructions in the 
resulting folder for setting up the test file system.

Step 2
**USB components aren't used in the main program for this version**
**The code necessary for running the USB code is there though**
Unzip the USB codes and copy them to a USB device.

Step 3
Make sure the cryptoSystem.properties file is in the same directory
as the CryptoSystem.jar or the source code. Modify the 
cryptoSystem.properties file to match the directory setup.
Be sure to modify the clean.bat in /SeniorDesign/sys/ directory if
you didn't do that in step 1.


Modify path in Server.java 
D:/iDataFiles/iFiles/Shared-Files/StudentReports/SeniorDesign/S_14/Final delivery/SeniorDesign_S14/Sever and PC/data/challengePassword.txt
Modify path in client.java
D:/iDataFiles/iFiles/Shared-Files/StudentReports/SeniorDesign/S_14/Final delivery/SeniorDesign_S14/Sever and PC/data/challengePassword.txt
Server
javac Server.java
java Server

PC = Laptop
copy D:\iDataFiles\iFiles\Shared-Files\StudentReports\SeniorDesign\S_14\Final delivery\SeniorDesign_S14\Test Directory\SeniorDesign and D:\iDataFiles\iFiles\Shared-Files\StudentReports\SeniorDesign\S_14\Final delivery\SeniorDesign_S14\Test Directory\encryptThese to laptop

Edit D:\iDataFiles\iFiles\Shared-Files\StudentReports\SeniorDesign\S_14\Final delivery\SeniorDesign_S14\Sever and PC\src\cryptoSystem.properties
to reflect the Laptop file locations

Edit D:\iDataFiles\iFiles\Shared-Files\StudentReports\SeniorDesign\S_14\Final delivery\SeniorDesign_S14\Test Directory\SeniorDesign\sys\clean.bat to reflect the locations

Transfer the files to Eclispse and 
import the PC & server folder, run GUI, export as executable jar

Double click the Client.jar at laptop

Android
Import zipped android code to Eclipse
About device" in Settings and tap on the "Build number" entry seven times, which will unlock "Developer Options"
Toggle on "USB Debugging" in the "Developer Options" area of Settings

Click Andorid arrow icon (SDK manager) to select and update drivers
Install Windows driver using device manager existing in the C:\iPrograms\adt-bundle-windows-x86-20131030\sdk\extras\google\usb_driver
http://stackoverflow.com/questions/11974700/nexus-7-not-visible-over-usb-via-adb-devices-from-windows-7-x64
http://zacktutorials.blogspot.ca/2012/08/nexus7-android-development.html
select Stay awake, Allow mock locations, 

***to run in eclipse
Step 4
In eclipse select import and import existing java project. 
Then select SD2014 4_7.zip and SD2014_Android.zip. 

The PC code is started
from GUI.java, the server from Server.java (run in command line), and
the Android from Login.java. 

To demo
1. Start server
java Server

2. Laptop click Client.Jar
enter ip address for Server and for Laptop PC
Type username Michael using keyboard
Type "War_" (wothout quotes and there is a _) using mouse click 
Then type "Eagl" (wothout quotes) using the list display on Laptop
Finally Type  "e" (wothout quotes) using mouse click 
Hit Finish on Android
Hit submission on Laptop
Hit protect, browse to encryptThese, select folder(s)
Hit unprotect: click the file list and type page number
-> view the unprotected page in Wu\Desktop\SeniorDesign_S14\Test Directory\SeniorDesign\sys\decrypted
do not click OK in the Message Window
close the page 
click OK in the Message Window
click Reset
close performance Windows

3. Android
Force a time sink with NTP server before starting the demo
click TPMlOGIN
type server ip address hit back arrow, ok
Start the process of typing username and password
Hit Finish on Android
Hit submission on Laptop
