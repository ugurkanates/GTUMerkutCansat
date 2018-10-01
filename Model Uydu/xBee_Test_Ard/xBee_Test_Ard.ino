#include <SoftwareSerial.h>

const int RXPin = 3;
const int TXPin = 4;
String tele = "7893,";
String tele1 = "";
int packNo = 0;
String date = "04.09.2018/06.11.00";
float pres = 100922.24;
float height = 123.4;
float descend = 12.3;
float temp = 12.3;
float battVoltage = 7.81;
float GPSLat = 40.812278;
float GPSLon = 29.362087;
float GPSAlt = 123.4;
bool Sat = 1;

unsigned long time = 0;
unsigned long lastTime = 0;
int interv = 0;

SoftwareSerial xBee (RXPin, TXPin);

void setup() {
  xBee.begin(57600);
  Serial.begin(9600);
}

void loop() {
  //Reading from sensors and getting data:
  pres = random(100000.00, 120000.00);
  height = random(0.0, 500.0);
  descend = random(0.0, 40.0);
  temp = random(20.0,40.0);
  battVoltage = random(7.00, 8.00);
  GPSLat = random (40.000000, 41.000000);
  GPSLon = random (28.800000, 29.800000);
  GPSAlt = random (0.0, 500.0);

  //Creating new packet
  packNo = packNo + 1;
  tele1 = "<" + tele + "," + packNo + "," + date + "," + pres + "," + height + ","
  + descend + "," + temp + "," + battVoltage + "," + GPSLat + "," + GPSLon + ","
  + GPSAlt + "," + Sat + ">";
  interv = millis() - lastTime;
  if (interv > 200)
  {
    Serial.println(tele1);
    xBee.print(tele1);
    tele = "7893";
    tele1 = "";
    lastTime = millis();
  }

}
