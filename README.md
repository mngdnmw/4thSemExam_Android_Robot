# 4thSemExam_Android_Robot
This project includes two independent Android applications, [one](https://github.com/mngdnmw/4thSemExam_Android_Robot)  serving as the client in the robot client-server relationship between the phone and the EV3 [robot](https://github.com/mngdnmw/4thSemExam_AA); the other being a [mobile application](https://github.com/mngdnmw/4thSemExam_Android) that displays images from pictures taken by the client. Although together the applications provide a richer experience of the team's intended vision, all applications can be run independently of each other (with the exception of the client and server). There is also a [web application](https://github.com/mngdnmw/4thSemExam_Web), with uses similar to the mobile application to view the images.

The initial vision of the team was to create an autonomous robot that can roam and identify items of interest - for example, birds - take a photo of that item, upload it to the database (Firebase) and then make available to view for others using the mobile application or web application. This repository pertains to the client/Android application that does the image processing, sending commands to the EV3 robot and seinding images to the database.

## Dependencies
### OpenCv
* Download instructions: https://stackoverflow.com/questions/27406303/opencv-in-android-studio
### Firebase
* How to set up: https://firebase.google.com/docs/admin/setup
