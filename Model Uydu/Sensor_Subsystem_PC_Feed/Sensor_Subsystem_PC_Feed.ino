#include <TinyGPS++.h>
#include <SoftwareSerial.h>
#include <Wire.h>
#include <SPI.h>
#include <Adafruit_Sensor.h>
#include <Adafruit_BMP280.h>

#define BMP_SCK 13
#define BMP_MISO 12
#define BMP_MOSI 11 
#define BMP_CS 10

static const int RXPin = 3, TXPin = 4;
static const uint32_t GPSBaud = 9600;

const int battCurrentReadPin = A0;
const int battVoltageReadPin = A1;

int rawCurrentValue = 0;
float battCurrent = 0;
int rawVoltageValue = 0;
float battVoltage = 0;

// The TinyGPS++ object
TinyGPSPlus gps;

// The Adafruit BMP object
Adafruit_BMP280 bme(BMP_CS); // hardware SPI

// The serial connection to the GPS device
SoftwareSerial ss(RXPin, TXPin);

void setup(){
  pinMode(battCurrentReadPin, INPUT);
  Serial.begin(9600);
  }
  ss.begin(GPSBaud);
   if (!bme.begin()) {  
    Serial.println("Could not find a valid BMP280 sensor, check wiring!");
    while (1);
  }
}

void loop(){
  rawCurrentValue = analogRead(battCurrentReadPin);
  battCurrent = (rawCurrentValue / 1023.0)*5; //ADC reading scaled
  rawVoltageValue = analogRead(battVoltageReadPin);
  battVoltage = (rawVoltageValue / 1023.0)*4.1*2; //ADC reading scaled
  
  // This sketch displays information every time a new sentence is correctly encoded.
  while (ss.available() > 0){
    gps.encode(ss.read());
    if (gps.location.isUpdated()){
      Serial.print("Latitude= "); 
      Serial.print(gps.location.lat(), 6);
      Serial.print(" Longitude= "); 
      Serial.println(gps.location.lng(), 6);
      
      // Altitude in meters (double)
      Serial.print("Altitude in meters = "); 
      Serial.println(gps.altitude.meters()); 
      
      // Number of satellites in use (u32)
      Serial.print("Number os satellites in use = "); 
      Serial.println(gps.satellites.value()); 

      Serial.print("Temperature = ");
      Serial.print(bme.readTemperature());
      Serial.println(" *C");
    
      Serial.print("Pressure = ");
      Serial.print(bme.readPressure());
      Serial.println(" Pa");

      Serial.print("Approx altitude = ");
      Serial.print(bme.readAltitude(1013.25)); // this should be adjusted to your local forcase
      Serial.println(" m");

      
      Serial.print("Current = ");
      Serial.print(battCurrent, 3);
      Serial.println(" amps DC");

      Serial.print("Voltage = ");
      Serial.print(battVoltage, 3);
      Serial.println(" voltage DC");
    }
  }
}
