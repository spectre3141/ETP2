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
	
	//Reset timer register
	TCCR0A = 0;
	TCCR0B = 0;
	
	TCCR0A |= (1<<COM0A1) | (1<<COM0B1);
	TCCR0A |= (1<<WGM00) | (1<<WGM01);			//Fast PWM
	TCCR0B |= (1<<CS00) | (1<<CS01);			//Prescaler = 64; f = 488Hz
	
	
}