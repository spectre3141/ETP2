/*
 * alarm.c
 *
 * Created: 30.04.2018 08:35:55
 *  Author: Felix Baumann
 */ 

#include <avr/io.h>
#include "Headerfiles/alarm.h"

#define INIT_CH1 ((uint8_t)(0x3F))
#define INIT_CH2 ((uint8_t)(0xFF-INIT_CH1))

uint32_t sec = 0;
uint8_t	onoff = 0;

void alarm_init(void)
{
	//Configure pins as output
	DDRD |= (1<<DDRD6) | (1<<DDRD5);	//PD5 and PD6 => output
	DDRD |= (1<<DDRD4);					//PD4: output, controls amplifier standby
	PORTD.PIN6 = 1;							//Alarm is off
	//Timer 0 is used to generate the alarm sound
	//Reset timer register
	TCCR0A = 0;
	TCCR0B = 0;
	
	TCCR0A |= (1<<COM0A1) | (1<<COM0B1);
	TCCR0A |= (1<<WGM00);						//Phase correct PWM
	TCCR0B |= (1<<CS00) | (1<<CS01);			//Prescaler = 64;
	
	OCR0A = INIT_CH1;
	OCR0B = INIT_CH2;
	
	//Timer 2 config
	TCCR2A = 0;									//clear timer register
	TCCR2B = 0;
	ASSR = 0;
	TIMSK2 = 0;
			
	TCCR2A |= (1<<WGM21);						// Clear Timer on Compare Match
	TCCR2B |= (1<<CS22) | (1<<CS21) | (1<<CS20)	//Prescaler = 1024
	ASSR|= (1 << AS2);							//Enable external clock
	OCR2A = 31;									//Interrupt every 1 second
	TIMSK2 |= (1 << OCIE2A);					//Enable interrupt for overflow
	
	
	
}
void init_alarmTimer(void)
{
	
}
void resetAlarm(void)
{
	stopAlarmSound();
}
uint32_t getAlarmTime(void)
{
	return sec;
}
void setAlarmTime(uint32_t time)
{
	sec = time;
}
void startAlarmSound(void)
{
	PORTD ^= (1<<PORTD6);
	
}
void stopAlarmSound(void)
{
	
}
void TIMER2_IRQ(void)
{
	sec--;						//decrement seconds by 1
	if(sec == 0)				
	{
		init_alarmTimer();
		if(onoff == 1)			//turn alarm sound on and off every 1 second
		{
			startAlarmSound();
			onoff = 0;
		}
		else
		{
			stopAlarmSound();
			onoff = 1;
		}
		
	}
}