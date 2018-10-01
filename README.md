# GTUMerkutCansat
Gebze Technical University Kocaeli / Turkey Ground Station Software used for CanSat 18' && Turksat Model Uydu #4 2018 Teknofest
![alt text](https://raw.githubusercontent.com/ugurkanates/GTUMerkutCansat/master/gcstest.png)
This software consist two parts.

1- Electronical System Software  aka Flight Software.
   Used Arduino Uno for subsystems.BMP 180 as pressure sensor(altitude,tempeature,air pressure)
   Used FPV for live cam of Satellite
   Used GPS  for tracking data.
   XBee for 2 way communication. ( Can also be recevier from satellite when manuel fire command reach)
Electronical system starts when switch is on. It initializes sensor,gps etc. Fires up FPV communication. Sends data to Ground Control Station less than 1 hz(1second). Around 500 meter it turns or 750 (CanSat) open parachutes.
Purpose is sending data on all sitations of flight. From rise to top while sending telemetry fully , saving camera data, saving logs as .csv to SD Card. Also communicating with Ground Control Software to listen if any manual trigger happened if such happened open chutes.
C,C++.

Written by Furkan Karagoz && Ugurkan Ates. 

2- Ground Control Software aka Ground Station
 Used JavaFX for creating and plotting telemetry data arrived from SerialPort. It gets all data such as GPS,FPV Livefeed,BMP data,DATA PACK,Date,Mission Time,Current situtation. While turning if necessary plot graphics needed. FPV cam can be shown live as right corner all happening at same screen. Also saves all data to csv and mp4 video. Necessary .JAR's are included
Serial Port class imported. works perfectly.
Used WebcamView by sarxos to implement FPV livefeed data.

Written by Ugurkan Ates



![alt text](https://raw.githubusercontent.com/ugurkanates/GTUMerkutCansat/master/web.telegram.org%20%23%2034016dbe-9c37-4225-a701-e67e3cc43d3c%20.jpg)
