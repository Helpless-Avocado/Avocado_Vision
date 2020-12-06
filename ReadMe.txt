This is the EC327 Project Submission for Team Helpless Avocado.
This Project was completed on 12/07/2020 for Professor Coskun.

The Team Members are:
	Luca Guidi
	Linglong Le
	Alexander Mackanic
	Aidan McCall

Regarding External Libraries:

    For this Project, 2 External Libraries are required:
        OpenCV 3.4.12 which can be found here: https://opencv.org/releases/
        FFMPEG Mobile, which can be found here: https://github.com/tanersener/mobile-ffmpeg

    FFMPEG Mobile was not altered in any way, but in OpenCV 3.4.12 the functions JavaCameraView.java
    and CameraBridgeViewBase.java found within this library were altered for this project, and have
    been included within this .zip submission.

    For enabling FFMPEG, simply add the line:
        implementation 'com.arthenica:mobile-ffmpeg-video:3.0'
    Into the build.gradle underneath dependencies. Version 3.0 was used as it supported a minSDK
    of 23, but higher versions will also work with this project

Running this Project:

    To run this project simply extract this zip file to any location on your computer.
    Then open up Android studio, and select import project, before navigating to the
    folder that the contents were unzipped to. Select that folder for import, and allow it to
    import.

    This Project relies on File Storage and Video implementations that cannot be implemented
    on an emulator, but work perfectly well on the actual android phone itself. Emulation
    will work regarding the Picture Functionality of this project, but taking and editting video,
    along with saving files to the phone will not work

