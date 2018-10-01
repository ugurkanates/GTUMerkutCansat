#include <String.h>

void setup()
{
  Serial.begin(9600);
}

void loop()
{
  float flt1 = 12.345;
  String str1 = String(flt1, 1);
  String str2 = String(flt1, 2);
  String str3 = String(flt1, 3);
  String str4 = String(flt1, 4);
  Serial.println(str1.length());
  Serial.println(str2.length());
  Serial.println(str3.length());
  Serial.println(str4.length());
  delay (1000);
}
