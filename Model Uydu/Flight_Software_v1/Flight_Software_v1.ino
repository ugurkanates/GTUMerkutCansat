/*This code is used to send the following data to GCS
 * Team no
 * Packet no
 * GPSDateTime
 * Air Pressure
 * Altitude
 * Descend speed
 * Air Temperature
 * Battery Volt
 * GPSLat
 * GPSLon
 * GPSAlt
 * GPSSat
 */
#include <TinyGPS++.h>
#include <SoftwareSerial.h>
#include <Wire.h>
#include <SPI.h>
#include <Adafruit_Sensor.h>
#include <Adafruit_BMP280.h>

//BMP Connections
// Sensor board connections list in series: SCL ; CSB ; SDA ; SDO ; 5V ; 3.3V ; GND
#define BMP_SCK 13 //Connected to SCL
#define BMP_MISO 12 //Connected to SDO via regulator
#define BMP_MOSI 11 //Connected to SDA via regulator
#define BMP_CS 10 //Connected to CSB
//GPS connections
static const int GPS_RX = 3, GPS_TX = 4;
static const uint32_t GPSBaud = 9600;
//Batt read connection
const int BATT_V = A1;
//Xbee Connections
static const int XBEE_RX = 5, XBEE_TX = 6;

int battVRaw = 0;
float battV = 0.0;

float airPres = 0.0;
float airTemp = 0.0;
float airAlt = 0.0;

String GPSLat = "";
String GPSLon = "";
String GPSAlt = "";
String GPSTime = "";
String GPSSat = "";

String feed = "";
int teamNo = "7893";
int packetNo = 100000;
float desSpeed = 0.0;
float prevAlt = 0.0;
long prevTime = 0;
float firstPres = 0.0;

// The TinyGPS++ object
TinyGPSPlus gps;

// The BMP object
Adafruit_BMP280 bme(BMP_CS); // hardware SPI

// The serial connection to the GPS device
SoftwareSerial GPSSerial(GPS_RX, GPS_TX);
// The serial connection to the Xbee
SoftwareSerial XbeeSerial(XBEE_RX, XBEE_TX);

/*
 * 
 *  CLEAR FUNCTION
 *
 */
 
void Clear(void) //Clears strings' values for upcoming values
{
  GPSLat = "";
  GPSLon = "";
  GPSAlt = "";
  GPSTime = "";
  GPSSat = "";
  feed = "";
  return 0;
}

/*
 * 
 * SETUP
 * 
 */

void setup(){
  Serial.begin(9600);
  GPSSerial.begin(GPSBaud);
  XbeeSerial.begin(57600);
  if (!bme.begin()) {  
   Serial.println("Could not find a valid GPS sensor, check wiring!");
   while (1);
  }
  if (!bme.begin()) {
    Serial.println("Could not start Xbee Serial. Restart system.");
    while (1);
  }
  firstPres = bme.readAltitude(1004);
}

/*
 * 
 *  LOOP
 * 
 */

void loop(){
  unsigned long start = millis();
  
  battVRaw = analogRead(BATT_V);
  battV = (battVRaw / 1023.0)*4.1*2; //ADC reading scaled

  while (GPSSerial.available())
  {
    gps.encode(GPSSerial.read());
    if (gps.location.isUpdated())
    {
      GPSLat = GPSLat + gps.location.lat();
      GPSLon = GPSLon + gps.location.lng();
      GPSAlt = GPSAlt + gps.altitude.meters();
      GPSSat = GPSSat + gps.satellites.value();
      GPSTime = GPSTime + gps.date.year()+"."+gps.date.month()+"."+gps.date.day()+
        "/"+gps.time.hour()+"."+gps.time.minute()+"."+gps.time.second();
    }
  }

  airPres = bme.readPressure(); //pressure in Pa
  airTemp = bme.readTemperature(); //temperature in C
  airAlt = bme.readAltitude(firstPres); //ADJUST TO THE LOCAL FORECAST!

  desSpeed = prevAlt - airAlt;
  desSpeed = desSpeed / prevTime - millis();
  prevTime = millis();
  prevAlt = airAlt;

  feed ="<" + teamNo + "," + packetNo  + "," + GPSTime + "," 
  + airPres + "," + airAlt "," + desSpeed + "," + airTemp + "," + battV + "," + GPSLat + "," 
  + GPSLon + "," + GPSAlt + "," + GPSSat + ">";

  packetNo = packetNo + 1;
  
  Serial.println(feed);
  XbeeSerial.println(feed);
  Clear();
  
  do{}
  while(millis() - start < 200);
}

