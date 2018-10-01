/*This code is used to send the following data to GCS
 * Team no
 * Package no
 * GPSDateTime
 * Air Pressure
 * Altitude "GENERATED"
 * Descend speed "GENERATED"
 * Air Temperature
 * Battery Volt
 * GPSLat
 * GPSLon
 * GPSAlt
 * GPSSat
 *
 *  CREATED BY: FURKAN KARAGOZ
 *  DATE: 17.09.2018
 *
 *  Items to check when starting to use it:
 *  -Base sea line air pressure
 *  -Full battery voltage level
 *
 * Pin usage: A0:BATT A1:SER1 A2:SER2 A3:SER3 13:BMP 12:BMP 11:BMP 10:BMP 9:LEDPIN 8:BUZZER 6:XBEE 5:XBEE 3:GPS 2:GPS
 */

#include <TinyGPS++.h>
#include <SoftwareSerial.h>
//#include <Wire.h>
#include <SPI.h>
#include <Adafruit_Sensor.h>
#include <Adafruit_BMP280.h>
#include <String.h>
#include <Servo.h>

//BMP Connections
// Sensor board connections list in series: SCL ; CSB ; SDA ; SDO ; 5V ; 3.3V ; GND
#define BMP_SCK 13 //Connected to SCL
#define BMP_MISO 12 //Connected to SDO via regulator
#define BMP_MOSI 11 //Connected to SDA via regulator
#define BMP_CS 10 //Connected to CSB
//GPS connections
#define GPS_RX 2
#define GPS_TX 3
static const uint32_t GPSBaud = 4800;
static const float fullVoltageRead = 7.4; //CHANGE IT BY VOLTMETER READING
//Batt read connection
const int BATT_V = A0; //Arduino understands A0 as port
//Servo Connections
const int SERVO1 = A1;
const int SERVO2 = A2;
const int SERVO3 = A3;
//Xbee Connections
#define XBEE_RX 5
#define XBEE_TX 6
//BUZZER and LED Connections
#define BUZZER 10
#define LEDPIN 9

int battVRaw = 0;
float battV = 0.0;

float airPres = 0.0;
float airTemp = 0.0;
float airAlt = 0.0;

float GPSLat = 0.0;
float GPSLon = 0.0;
float GPSAlt = 0.0;
int year = 2018;
int month = 9;
int day = 15;
int hour = 0;
int min = 0;
int sec = 0;
int GPSSat = 0;

int packageNo = 1;
float desRate = 0.0;
float prevAlt = 0.0;
long prevTime = 0;
float firstAirPres = 0.0;
unsigned long time1 = 0;
unsigned long time2 = 0;
unsigned long telemetryTimer = 0.0;

//Servo objects. #1 is carrier, #2 is release, #3 is payload
Servo servo1;
Servo servo2;
Servo servo3;

// The TinyGPS++ object
TinyGPSPlus gps;
// The BMP object
Adafruit_BMP280 bme(BMP_CS); // hardware SPI

// The serial connection to the GPS device
SoftwareSerial GPSSerial(GPS_RX, GPS_TX);
// The serial connection to the Xbee
SoftwareSerial XbeeSerial(XBEE_RX, XBEE_TX);

//States for each operation step. 1=passed, 0=waiting.
//Stages: 1=1st parachute released, 2=payload released, 3=2nd parachute released
// 4=landed
bool state1 = 0;
bool state2 = 0;
bool state3 = 0;
bool state4 = 0;

String input = "";

void setup()
{
  pinMode(BATT_V, INPUT);
  pinMode(LEDPIN, OUTPUT);
  digitalWrite(LEDPIN, 1);
  Serial.begin(9600);
  GPSSerial.begin(GPSBaud);
  XbeeSerial.begin(57600);
  firstAirPres = bme.readPressure();
  servo1.attach(SERVO1);
  servo2.attach(SERVO2);
  servo3.attach(SERVO3);
  XbeeSerial.println("Starting up CANSAT...");
}

void loop()
{
  do {
    Telemetry();
  } while((airAlt>500 && desRate>8) || desRate<20); // 8-10 metre/sn
  servo1.write(100); //OPEN SERVO1
  do {
    Telemetry();
  } while(airAlt>400); // 400 +/-10 metrede ayrılma
  servo2.write(100); //OPEN SERVO2
  servo3.write(100); //OPEN SERVO3 //DELAY SAĞLA
  do {
    Telemetry();
  } while(airAlt>0 && desRate>2);
  unsigned int landedTimer = millis();
  do {
    tone(piezoPin, 480, 500);
    digitalWrite(LEDPIN, HIGH);
    delay(500);
    tone(piezoPin, 1000, 500);
    digitalWrite(LEDPIN, LOW);
    delay(500);
  } while(millis()-landedTimer())<1200000; //20 mins
  do {} while(1); //END OF OPERATION

/*
  if (airAlt>500 && desRate>5)
  {
    servo1.write(100); //OPEN SERVO1
    if (airAlt<400 && desRate>5)
    {
      servo2.write(100) //OPEN SERVO2
      if(state3 == 0)
      {
        unsigned long timer = millis();
        while ((millis()-timer)>400){}
        servo3.write(100); //OPEN SERVO3
      }
    }
  }
  else
  {
    Telemetry();
  }
*/
}

void CollectSetData ()
{
  while (GPSSerial.available())
  {
    gps.encode(GPSSerial.read());
    GPSLat = gps.location.lat();
    GPSLon = gps.location.lng();
    GPSAlt = gps.altitude.meters();
    GPSSat = gps.satellites.value();
    year = gps.date.year();
    month = gps.date.month();
    day = gps.date.day();
    hour = gps.time.hour();
    min = gps.time.minute();
    sec = gps.time.second();
  }
  airPres = bme.readPressure();
  airTemp = bme.readTemperature();
  airAlt = bme.readAltitude(firstAirPres);
  time1 = millis();
  battV = (analogRead(BATT_V)/1023)*5*2;
    //2 comes by 1/2 voltage divider and 5 comes from 5v logic level
  desRate = (prevAlt - airAlt) / ((time2 - time1)*1000);
  //meters per sec, descending is +, rising is -
}

String PrepareFeed ()
{
  String str_packageno(packageNo);
  switch (str_packageno.length()) {
    case 1: str_packageno = "0000" + str_packageno; break;
    case 2: str_packageno = "000" + str_packageno; break;
    case 3: str_packageno = "00" + str_packageno; break;
    case 4: str_packageno = "0" + str_packageno; break;
    case 5: break;
  }
  String str_month(month);
  switch (str_month.length()) {
    case 1: str_month = "0" + str_month; break;
    case 2: break;
  }
  String str_day(day);
  switch (str_day.length()) {
    case 1: str_day = "0" + str_day; break;
    case 2: break;
  }
  String str_hour(hour);
  switch (str_hour.length()) {
    case 1: str_hour = "0" + str_hour; break;
    case 2: break;
  }
  String str_min(min);
  switch (str_min.length()) {
    case 1: str_min = "0" + str_min; break;
    case 2: break;
  }
  String str_sec(sec);
  switch (str_sec.length()) {
    case 1: str_sec = "0" + str_sec; break;
    case 2: break;
  }
  String str_airpres(airPres,2);
  switch (str_airpres.length()) {
    case 8: str_airpres = "0" + str_airpres; break;
    case 9: break;
    default: str_airpres = "??????.??"; break;
  }
  String str_airalt(airAlt,1);
  switch (str_airalt.length()) {
    case 2: str_airalt = "000" +str_airalt; break;
    case 3: str_airalt = "00" + str_airalt; break;
    case 4: str_airalt = "0" + str_airalt; break;
    case 5: break;
    default: str_airalt = "???.?"; break;
  }
  String str_desrate(desRate,1);
  switch (str_desrate.length()) {
    case 2: str_desrate = "00" + str_desrate; break; //MAYBE BROKEN
    case 3: str_desrate = "0" + str_desrate; break;
    case 4: break;
    default: str_desrate = "??.?"; break;
  }
  String str_airtemp(airTemp,1);
  switch (str_airtemp.length()) {
    case 2: str_airtemp = "00" + str_airtemp; break;
    case 3: str_airtemp = "0" + str_airtemp; break;
    case 4: break;
  }
  String str_battv(battV,1);
  switch (str_battv.length()) {
    case 2: str_battv = "0" + str_battv; break;
    case 3: break;
  }
  String str_gpslat(GPSLat,5);
  switch (str_gpslat.length()) {
    case 7: str_gpslat = "0" + str_gpslat; break;
    case 8: break;
    default: str_gpslat = "??.?????"; break;
  }
  String str_gpslon(GPSLon,5);
  switch (str_gpslon.length()) {
    case 7: str_gpslon = "0" + str_gpslon; break;
    case 8: break;
    default: str_gpslon = "??.?????"; break;
  }
  String str_gpsalt(GPSAlt,1); //SET NUMBER
  switch (str_gpsalt.length()) {
    case 7: str_gpsalt = "0" + str_gpsalt; break;
    case 8: break;
    default: str_gpsalt = "???.?"; break;
  }
  String str_gpssat(GPSSat);
  switch (str_gpssat.length()) {
    case 1: str_gpssat = "0" + str_gpssat; break;
    case 2: break;
    default: str_gpssat = "??"; break;
  }

  String feed = "<7893," + str_packageno + "," + year + "." + str_month + "."
  + str_day + "/" + str_hour + ":" + str_min + ":" + str_sec + "," + str_airpres
  + "," + str_airalt + "," + str_desrate + "," + str_airtemp + "," + str_battv
  + "," + str_gpslat + "," + str_gpslon + "," + str_gpsalt + "," + str_gpssat + ">";
  //EXAMPLE: <7893,00001,2018.09.17/16:50:07,100919.67,432.1,07.2,29.3,7.4,43.12345,21.12345,430.9,08,1>

  packageNo ++;
  prevAlt = airAlt;
  time2 = millis();
  return feed;
}

void Telemetry () //collects data, packages and sends every .5 sec
{
  input = XbeeSerial.read();
  unsigned long telemetryTimer = millis();
  CollectSetData();
  XbeeSerial.print(PrepareFeed());

  do {
    CollectSetData();
  } while((millis()-telemetryTimer)<=500);
  XbeeSerial.print(PrepareFeed());
  telemetryTimer = millis();
}
