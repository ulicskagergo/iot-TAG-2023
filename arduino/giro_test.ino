#include <Wire.h>


const int MPU = 0x68;
int16_t AcX, AcY, AcZ, Tmp, GyX, GyY, GyZ;


void setup()
{
 Wire.begin();
 Wire.beginTransmission(MPU);
 Wire.write(0x6B);
 Wire.write(0);
 Wire.endTransmission(true);
 Serial.begin(9600);
}
void loop()
{
 Wire.beginTransmission(MPU);
 Wire.write(0x3B);
 Wire.endTransmission(false);
 Wire.requestFrom(MPU, 12, true);
 AcX = Wire.read() << 8 | Wire.read();
 AcY = Wire.read() << 8 | Wire.read();
 AcZ = Wire.read() << 8 | Wire.read();
 GyX = Wire.read() << 8 | Wire.read();
 GyY = Wire.read() << 8 | Wire.read();
 GyZ = Wire.read() << 8 | Wire.read();


 //Accelero meter
 Serial.print("acX:");
 Serial.print(AcX);
 Serial.print(",");
 Serial.print("acY:");
 Serial.print(AcY);
 Serial.print(",");
 Serial.print("acZ:");
 Serial.println(AcZ);


 //Serial.print("Gyroscope: ");
 Serial.print("X:");
 Serial.print(GyX);
 Serial.print(",");
 Serial.print("Y:");
 Serial.print(GyY);
 Serial.print(",");
 Serial.print("Z:");
 Serial.println(GyZ);
 delay(333);
}

