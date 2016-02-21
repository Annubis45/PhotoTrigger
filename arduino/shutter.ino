/*
  Photo trigger and timelapse
 */
#include <SoftwareSerial.h>

SoftwareSerial mySerial(10, 9); // RX, TX

int pinoptoFocus=13;
int pinoptoShutter=12;

boolean timelapsmode;
boolean refocus;
long timelapstempo;
long last_millis;
int enteredValue;

void setup() {
  Serial.begin(9600);
  Serial.println("Debut setup;"); 
SetupBT();
  pinMode(pinoptoFocus, OUTPUT);
  pinMode(pinoptoShutter, OUTPUT);
  timelapsmode=false;
  refocus=false;
  Serial.println("Fin setup;"); 
}

void loop() {
  delay(500);
  String Commande="";
  while (mySerial.available() > 0)//if (mySerial.available())
  {
    char received = mySerial.read();
    Commande+=received;
    Serial.write(received);
    
  } 
  if(Commande!="")
  {
    Serial.println();
    TreatCommand(Commande);
  }
    
  if(timelapsmode)
  {
    if(millis()>=last_millis+timelapstempo)
    {
        if(OutputValue(pinoptoFocus)==0)
        {
          digitalWrite(pinoptoFocus, HIGH);
          delay(1000);
        }
          
        digitalWrite(pinoptoShutter, HIGH);
        last_millis=millis();
        delay(100);
        digitalWrite(pinoptoShutter, LOW);
        
        if(refocus)
           digitalWrite(pinoptoFocus, LOW);
    }
  }
  
}

void SetupBT()
{
  mySerial.begin(9600);
  delay(500);
  mySerial.print("AT+PIN1342"); // Set pin to 1342
  delay(500);
  mySerial.print("AT+NAMEPhotoTriggerBT"); // Set the name
  delay(500);
  //mySerial.print("AT+BAUD8"); // Set baudrate to 115200
  mySerial.print("AT+BAUD4"); // Set baudrate to 9600
  delay(1000);
}

void TreatCommand(String Commande)
{
  if(Commande=="S")
    digitalWrite(pinoptoShutter, HIGH);
  
   if(Commande=="s")
    digitalWrite(pinoptoShutter, LOW); 
    
   if(Commande=="F")
    digitalWrite(pinoptoFocus, HIGH);
    
   if(Commande=="P")
   {
      TakeAPhoto(1000);
   }
   
   if(Commande!="P" && Commande.startsWith("P"))
   {
      TakeAPhoto((Commande.substring(1)).toInt());
   }
  
   if(Commande=="f")
    digitalWrite(pinoptoFocus, LOW);
    
   if(Commande=="fs"||Commande=="sf")
   {
    digitalWrite(pinoptoFocus, LOW);
    digitalWrite(pinoptoShutter, LOW);
   }
    
   if(Commande=="R")
   {
    digitalWrite(pinoptoFocus, LOW);
    digitalWrite(pinoptoShutter, LOW);
    timelapsmode=false;
   }
   
   if(Commande!="A" && Commande.startsWith("A"))
   {
      timelapstempo=(Commande.substring(1)).toInt()*1000;
      timelapsmode=true;
      refocus=true;
      last_millis=0;
   }
   
   if(Commande!="P" && Commande.startsWith("P"))
   {
      timelapstempo=(Commande.substring(1)).toInt()*1000;
      timelapsmode=true;
      refocus=false;
      last_millis=0;
   }
}

void TakeAPhoto(int tempoFocus) {
  digitalWrite(pinoptoFocus, HIGH);
    delay(tempoFocus);
    digitalWrite(pinoptoShutter, HIGH);
    delay(100);
    digitalWrite(pinoptoShutter, LOW);
    digitalWrite(pinoptoFocus, LOW);
}

int OutputValue(int pin)
{
  if (pin <= 7) 
  {
    return bitRead(PORTD, pin);
  }
  else
  {
    return bitRead(PORTB, pin-8);
  }
}


void LaunchTimeLaps(boolean inMinute)
{
  digitalWrite(pinoptoFocus, HIGH);
  timelapsmode=true;
  timelapstempo=enteredValue*1000;
  if(inMinute)
    timelapstempo=timelapstempo*60;
  delay(1000);
}

