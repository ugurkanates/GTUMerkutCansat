const int battCurrentReadPin = A0;

int rawCurrentValue = 0;
float battCurrent = 0;

void setup() {
  pinMode(battCurrentReadPin, INPUT);
  Serial.begin(9600);
}

void loop() {
  rawCurrentValue = analogRead(battCurrentReadPin);
  battCurrent = (rawCurrentValue / 1023.0)*5; //ADC reading scaled

  Serial.println("Current = ");
  Serial.print(battCurrent, 3);
  Serial.println(" amps DC");
  
  delay(1500);
}
