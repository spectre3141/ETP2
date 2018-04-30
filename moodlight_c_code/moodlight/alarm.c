/*
 * alarm.c
 *
 * Created: 30.04.2018 08:35:55
 *  Author: Felix Baumann
 */ 

#include <avr/io.h>
#include "Headerfiles/alarm.h"

void alarm_initPWM(void)
{
	//Configure pins as output
	DDRD |= (1<<DDRD6) | (1<<DDRD5);	//PD5 and PD6 => output
	//Timer 0 is used to generate the alarm sound
	//Reset timer register
	TCCR0A = 0;
	TCCR0B = 0;
	
	TCCR0A |= (1<<COM0A1) | (1<<COM0B1);
	TCCR0A |= (1<<WGM00);						//Phase correct PWM
	TCCR0B |= (1<<WGM02);
	TCCR0B |= (1<<CS00) | (1<<CS01);			//Prescaler = 64;
	OCR0A = 0x80;
	OCR0B = 0x80;
	
	//Timer 2 config
	
}
void resetAlarm(void)
{
	
}
uint16_t getAlarmTime(void)
{
	return 0;
}
void setAlarmTime(uint16_t time)
{
	
}
void startAlarmSound(void)
{
	
}
void stopAlarmSound(void)
{
	
}