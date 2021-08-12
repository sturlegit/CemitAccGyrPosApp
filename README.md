# CemitAccGyrPosApp
#Legg disse to linjene[1] inn in build.gradle (.app) under "dependencies"
[1] implementation 'com.sun.mail:android-mail:1.6.0'
    implementation 'com.sun.mail:android-activation:1.6.0'

Legg denne linjen[2] etter linje 3 package="com.example.appname"> i AndroidManifest.xml
[2] <uses-permission android:name="android.permission.INTERNET"></uses-permission>


