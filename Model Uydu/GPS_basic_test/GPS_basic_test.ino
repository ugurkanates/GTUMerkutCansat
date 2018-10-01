#include <SoftwareSerial.h>

SoftwareSerial ss(2,3); //RX TX

void setup() {
  Serial.begin(9600);
  ss.begin(9600);
}

void loop() {
  while (ss.available() > 0)
  {
    byte gpsData = ss.read();
    Serial.write(gpsData);
  }

}
