Table of contents
===
* [Helium-Beta](#helium-beta)
    * [Latest Build Status](https://hellium.me:8443/job/Helium-Beta/)
    * [Build Status Information](#build-status)
* [Background](#background)
* [Android Support](#android-support)
* [Setup Instructions](#setup-instructions)
    * [Building and Exporting](#building-and-exporting)
    * [Testing](#testing)
* [Usage](#usage)
* [Features](#features)
    * [Friend Location Map](#friend-location-map)
    * [Chatting](#chatting)
    * [Augmented Reality](#augmented-reality)
    * [Facebook Login](#facebook-login)
    * [Importing Facebook Friends](#importing-friends-from-facebook)
    * [Disabling tracking]()
* [Future Features](#future-features)
    * [Account Creation](#account-creation)
    * [Username Login](#username-login)
    * [Connecting account to Facebook](#connecting-account-to-facebook)
    * [Adding Friends via username](#adding-friends-via-username)
    * [Group Chat](#group-chat)
    * [Machine Learning - Automated meet-up location](#machine-learning---automated-meet-up-location)
    * [Avatar selection](#avatar-selection)
    * [Reset password](#reset-password)


# Helium-Beta
This is a **client-side _Android_** application maintained by Team Helium.

Our application is aptly named **Peachy Strawberry**.

###### Build Status
Check out our (most probably passing) build status [here](https://hellium.me:8443/job/Helium-Beta/)!

We ran [Jenkins](https://jenkins.io/) as our personal  CI/CD ~~butler~~. It is hosted on a free
VM provided to University of Melbourne students in 
[Nectar Cloud](https://nectar.org.au/research-cloud/). For reasons beyond my comprehension, Firefox
seems to think our SSL cert is unsafe. If that's the case for you, try using Chrome instead. I
assure you nothing dodgy is going on.

There, a build badge shows the current build status and a dedicated HTML report will show
all passed/failed tests should you click on it.


<!-- ===============================  section one   ====================================== -->
# Background
This repository is in charge of the mobile application, 
ranging from services like chat, augmented reality, and location services.
##### Motivation
Google Maps is a great way to find where to go, but when meeting friends it only tells us if 
we are in the right place and not how far from our friends we actually are. We could be 
going to a concert and be at the venue, but it’ll be extremely hard to find 
your friend at the venue. With this application we hope to be able to not only help users 
get to the venue but also to find friends at the venue.





<!-- ===============================  section two ====================================== -->
# Android Support
TODO: what version we support, what caveats and what quirks to be aware of.







<!-- ===============================  section three ====================================== -->
# Setup Instructions
This is your run of the mill android studio project. Simply clone this repository and open it as
an android project from within [Android Studio](https://developer.android.com/studio/index.html).
#### Building and Exporting
To build this project, simply click the build button in Android Studio and the application will be
built into any connected Android device(or emulator).
#### Testing
The testing suite is separated into _unit_ and _instrumented_ testing.

To run either one, simply choose from the dropdown menu between the hammer and play icon on
Android Studio's top menu bar, and select the appropriate test suite.

Alternatively, unit tests can be run from the command-line using the 
```
$ ./gradlew test
```
 command at Helium-Beta's root directory.
 
 
<!-- ===============================  section four ====================================== -->
# Usage
Before stepping in-depth into the [features](#features) we have, this section explores the overview
of how a user can/should use **Peachy Strawberry**.

1. Upon entering the app, first time users (or logged out users) will be prompted to login into their
facebook accounts. First time users will also be prompted to accept location and camera permissions.
2. You're greeted by a (suspiciously-google-map-lookalike) map that displays all your friends. They
will appear as their facebook profile picture in the map view.
    * If you're trying to find a friend that's too far away, you can use the search bar to find him/her
    using his/her facebook profile name. You will hone in their direction immediately upon selection.
3. By tapping on a user's profile picture or searching (see above), the camera shifts focus onto
that user and displays a route to him/her with a particular mode of transport. You're free to change
the default mode. The ETA and distance will be calculated shortly.
4. By tapping a user, you also bring up a small bubble that shows 2 buttons. The leftmost being the
chat button. The rightmost button re-directs you to our AR with this user as its target.
5. If you tap the chat button, you get to chat with the selected user. Surprise surprise!
6. Assuming you're close enough to the user that you're itching to use our fabulous AR, tap on the user
again to bring out the bubble, then tap on the directional (rightmost) icon.
7. In the AR mode, you'll see (at most) 5 avatars on-screen. The biggest avatar is the currently tracked
friend. The top HUD shows how far away you are to that person. The smaller avatars are friends in that
general direction, but since you did not they're not your target of interest, they appear smaller.
    * A useful feature is to tap the user's profile in AR mode again to enter FOCUS mode. In this mode,
    all other (smaller avatar) friends will disappear, leaving the actively tracked user in AR only.
    You should receive a vibration when this happens to notify you that you're in FOCUS mode.
    * Tap the FOCUSED user again to bring your (smaller avatar) friends back out.
    * Keep in mind that the AR depends heavily on your GPS location and may produce unreliable result
    at times. If your device's sensors are poor, it will also display a message prompting you to calibrate it.
8. Tap the back button on your android device to exit AR mode anytime.

    
 
 


<!-- ===============================  section five ====================================== -->
# Features
Here, we go in (a little more) detail of how each feature works, with some diagrams.

#### Friend Location Map
 TODO: describe location map eg (pictures?)
Provides a bird's eye view of all friends in a given location.
* Path to friend's location
* Transportation options
* ETA
* Search far away friends
* etc..

#### Chatting
TODO: describe chatting functionality. (pictures?)
* Opaque chat over map
* Intuitive swipe away chat

#### Augmented Reality
TODO: insert pictures here after new pull request is merged. (AR-circular-bg)

#### Facebook Login
TODO: describe facebook login mechanism (pictures?)

#### Importing Friends from Facebook
TODO: Describe automated process of importing facebook friends

#### Disappear from map
TODO: Disappearing from map (new feature to prevent stalkers)





<!-- ===============================  section six ====================================== -->
# Future features
This section describes our stretch goals.

#### Account Creation
TODO: Describe what this means

#### Username Login
TODO: Describe what this means

#### Connecting account to Facebook
TODO: Describe what this means

#### Adding Friends via username
TODO: Describe what this means

#### Group Chat
TODO: Describe what this means

#### Machine Learning - Automated meet-up location
TODO: Describe what this means

#### Avatar Selection
TODO: Describe what this means

#### Reset password
TODO: Describe what this means
