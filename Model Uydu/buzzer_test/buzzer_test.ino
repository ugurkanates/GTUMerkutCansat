//A sketch to demonstrate the tone() function

//Specify digital pin on the Arduino that the positive lead of piezo buzzer is attached.
#define LEDPIN 2
int piezoPin = 8;

void setup() {

}//close setup

void loop() {

  /*Tone needs 2 arguments, but can take three
    1) Pin#
    2) Frequency - this is in hertz (cycles per second) which determines the pitch of the noise made
    3) Duration - how long teh tone plays
  */

  tone(piezoPin, 480, 500);
  digitalWrite(LEDPIN, HIGH);
  delay(500);
  tone(piezoPin, 1000, 500);
  digitalWrite(LEDPIN, LOW);
  delay(500);

  //tone(piezoPin, 1000, 500);
  //delay(1000);

}
