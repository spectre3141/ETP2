/*
 * alarm.c
 *
 * Created: 30.04.2018 08:35:55
 *  Author: Felix Baumann
 */ 

#include <avr/io.h>
#include <avr/interrupt.h>
#include "Headerfiles/alarm.h"

#define INIT_CH1 ((uint8_t)(0x3F))
#define INIT_CH2 ((uint8_t)(0xFF-INIT_CH1))

uint32_t seconds = 0;
uint8_t	onoff = 0;
uint32_t timeset = 0;
uint8_t alarm_off_time = 0;
void alarm_init(void)
{
	//Configure pins as output
	DDRD |= (1<<DDRD6) | (1<<DDRD5);	//PD5 and PD6 => output
	DDRD |= (1<<DDRD4);					//PD4: output, controls amplifier standby
	PORTD |= (1<<PIND4);				//PD4 = 1, Alarm is off
	
	//Timer 0 is used to generate the alarm sound
	//Reset timer register
	TCCR0A = 0;
	TCCR0B = 0;
	
	TCCR0A |= (1<<COM0A1) | (1<<COM0B1);		//Clear OC0x on compare match when up-counting. Set OC0x on compare match when down-counting
	TCCR0A |= (1<<WGM00);						//Phase correct PWM
	TCCR0B |= (1<<CS00) | (1<<CS01);			//Prescaler = 64;
	
	OCR0A = INIT_CH1;
	OCR0B = INIT_CH2;
	
	//Timer 2 config
	cli();										// disable interrupts
		
	TCCR2A = 0;									//clear timer register
	TCCR2B = 0;
	ASSR = 0;
	TIMSK2 = 0;
			
	TCCR2A |= (1<<WGM21);						// Clear Timer on Compare Match
	TCCR2B |= (1<<CS22) | (1<<CS21) | (1<<CS20);//Prescaler = 1024
	ASSR|= (1 << AS2);							//Enable external clock
	OCR2A = 31;									//Interrupt every 1 second
	TIMSK2 |= (1 << OCIE2A);					//Enable interrupt for overflow

	sei();										// enable interrupts
}
/* returns the set alarm time*/
uint32_t getAlarmTime(void)
{
	return timeset;
}

/*sets the alarm time*/
void setAlarmTime(uint32_t time)
{
	seconds = time;
	timeset = seconds;
}

/*starts the alarm sound*/
void startAlarmSound(void)
{
		PORTD ^= (1<<PIND4);
		alarm_off_time--;
}
/*stops the alarm sound*/
void stopAlarmSound(void)
{
	PORTD |= (1<<PIND4);
}
/* Turns the alarm sound off*/
void resetAlarm(void)
{
	stopAlarmSound();
}
void TIMER2_IRQ(void)
{
	seconds--;						//decrement seconds by 1
	
	if(seconds == 0)				
	{
		alarm_off_time = 60;		//duration off the alarm in seconds
		startAlarmSound();
	}
	if(alarm_off_time >= 0)			//alarm is on for the set amount of time
	{
		startAlarmSound();
	}
}