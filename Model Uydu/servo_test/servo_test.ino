#include <Servo.h>

Servo servo1;
Servo servo2;

int servoNo = 0;

void setup() {
  servo1.attach(11);
  servo2.attach(12);
  Serial.begin(9600);
}

void loop() {
  servo1.write(130); //108 açar, 130 kilit
  servo2.write(150); //140 açar , 90 kilit (sabitsiz)
  /*
  Serial.println("Enter servo number: ");
  while(!Serial.available())
  {servoNo = Serial.read();}

  switch(servoNo)
  {
    case 1:
      Serial.println("Enter value between 0 and 180: ");
      while(!Serial.available())
      {servo1.write(Serial.read());}
      break;
    case 2:
      Serial.println("Enter value between 0 and 180: ");
      servo2.write(Serial.read());
      delay(1000);
      break;
    }
    */
}
