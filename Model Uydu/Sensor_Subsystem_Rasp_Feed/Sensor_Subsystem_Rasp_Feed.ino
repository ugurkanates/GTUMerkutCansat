/*This code is used to send the following data from Arduino to Raspberry.
 * GPSLat
 * GPSLon
 * GPSAlt
 * GPSDateTime
 * GPSSat
 * Air Pressure
 * Air Temperature
 * Air Altitude
 * Battery Volt
 * Battery Amper
 */
#include <TinyGPS++.h>
#include <SoftwareSerial.h>
#include <Wire.h>
#include <SPI.h>
#include <Adafruit_Sensor.h>
#include <Adafruit_BMP280.h>

//BMP Connections
// List: SCL ; CSB ; SDA ; SDO ; 5V ; 3.3V ; GND
#define BMP_SCK 13 //Connected to SCL
#define BMP_MISO 12 //Connected to SDO via regulator
#define BMP_MOSI 11 //Connected to SDA via regulator
#define BMP_CS 10 //Connected to CSB
//GPS connections
static const int RXPin = 3, TXPin = 4;
static const uint32_t GPSBaud = 9600;
//MAX connections
const int battCPin = A0;
const int battVPin = A1;

int battCRaw = 0;
float battC = 0.0;
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

// The TinyGPS++ object
TinyGPSPlus gps;

// The BMP object
Adafruit_BMP280 bme(BMP_CS); // hardware SPI

// The serial connection to the GPS device
SoftwareSerial GPSSerial(RXPin, TXPin);

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

void setup(){
  pinMode(battCPin, INPUT);
  Serial.begin(9600);
  GPSSerial.begin(GPSBaud);
   if (!bme.begin()) {  
    Serial.println("Could not find a valid GPS sensor, check wiring!");
    while (1);
  }
}

void loop(){
  unsigned long start = millis();
  
  battCRaw = analogRead(battCPin);
  battC = (battCRaw / 1023.0)*5; //ADC reading scaled
  battVRaw = analogRead(battVPin);
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
  airAlt = bme.readAltitude(1004); //ADJUST TO THE LOCAL FORECAST!

  feed ="<" + GPSLat + "," + GPSLon + "," + GPSAlt + "," + GPSTime + "," + GPSSat +
  "," + airPres + "," + airTemp + "," + airAlt + "," + battV + "," + battC +">";
  
  Serial.println(feed);
  Clear();
  
  do{}
  while(millis() - start < 200);
}

