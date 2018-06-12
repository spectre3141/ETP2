/*
 * alarm.c
 *
 * Configures Timer 0 for the sound generation and Timer 2 as Real Time Counter.
 * Handels Interrupts from Timer 2.
 * Implements functions to start/stop the alarm sound, set/reset the alarm Time, increase and decrease the sound volume.
 * 
 *
 * Created: 30.04.2018 08:35:55
 *  Author: Felix Baumann
 */ 

#include <avr/io.h>
#include <avr/interrupt.h>
#include "Headerfiles/alarm.h"

#define INIT_CH1 ((uint8_t)(0x6F))				//f = ioclk/(Prescaler * 510)
#define INIT_CH2 ((uint8_t)(0xFF-INIT_CH1))
#define FREQ_1HZ 76

uint8_t	counter1Hz = 0;
uint32_t alarmTimeSet = 0;
uint32_t alarmTimeCounter = 0;
uint16_t alarmDuration = 0;
uint16_t alarmActiveCounter = 0;

void alarm_init(void)
{
	cli();								// disable interrupts
	
	// Configure pins as output
	DDRD |= (1<<DDRD6) | (1<<DDRD5);	// PD5 and PD6 => output
	DDRD |= (1<<DDRD4);					// PD4: output, controls amplifier standby
	PORTD |= (1<<PIND4);				// PD4 = 1, Alarm is off
	
	// Timer 0 is used to generate the alarm sound
	// Reset timer register
	TCCR0A = 0;
	TCCR0B = 0;
	
	TCCR0A |= (1<<COM0A1) | (1<<COM0B1);			// Clear OC0x on compare match when up-counting. Set OC0x on compare match when down-counting
	TCCR0A |= (1<<WGM00);							// Phase correct PWM
	TCCR0B |= (1<<CS00) | (1<<CS01);				// Prescaler = 64;
	
	OCR0A = INIT_CH1;
	OCR0B = INIT_CH2;
	
	// Timer 2 config, Real time counter (RTC)
	TCCR2A = 0;										// clear timer register
	TCCR2B = 0;
	ASSR = 0;
	TIMSK2 = 0;
			
	TCCR2A |= (1<<WGM21);							// Clear Timer on Compare Match
	TCCR2B |= (1<<CS22) | (1<<CS21) | (1<<CS20);	// Prescaler = 1024
	// ASSR |= (1 << AS2);							// Enable external clock	(32.768 kHz)
	OCR2A = 102;
	// OCR2A = 31;									// Interrupt every 1 second
	TIMSK2 |= (1 << OCIE2A);						// Enable interrupt for overflow

	sei();											// enable interrupts
}
/*Set volume of the alarm sound*/
void alarm_setVol(uint8_t value)
{
	if ((value <= 0x7F) && (value >= 0))
	{
		OCR0A = value - 1;
		OCR0B = 0xFF - value - 1;
	}
}

/* returns the set alarm time */
uint32_t getAlarmTime(void)
{
	return alarmTimeSet;
}

/* Sets how long the alarm is played */
void setAlarmDuration(uint16_t duration)
{
	alarmDuration = duration;
}

/* sets the alarm time, saves the alarm time */
void setAlarmTime(uint32_t time)
{
	alarmTimeSet = time;
	alarmTimeCounter = alarmTimeSet;
}

/* stops the alarm sound */
void alarmSoundOff(void)
{
	PORTD |= (1<<PIND4);			// put amplifier into standby mode
}

/* starts the alarm sound */
void alarmSoundOn(void)
{
	PORTD &= ~(1 << PIND4);			// wake amplifier from standby mode
}

/* Turns the alarm sound off */
void resetAlarm(void)
{
	alarmTimeSet = 0;
	alarmTimeCounter = 0;
	alarmActiveCounter = 0;
	alarmSoundOff();
}

void TIMER2_IRQ(void)
{
	if(counter1Hz < FREQ_1HZ)	// devide interrupt frequenzcy by 76 so that the alarm routine is triggert every ~1 second. (not needed when external crystal is used)
	{
		counter1Hz++;
	}
	else
	{
		counter1Hz = 0;
		
		if(alarmTimeSet > 0)	//alarm time set?
		{
			if(alarmTimeCounter > 0)	//decrease alarm counter every ~1 Second
			{
				alarmTimeCounter--;
			}
			else
			{
				alarmTimeSet = 0;
				alarmActiveCounter = alarmDuration;	//set duration of the alarm
			}
		}
		
		if(alarmActiveCounter > 0)	//Toggle alarm sound on and off every ~1 second
		{
			if(alarmActiveCounter % 2)
			{
				alarmSoundOn();
			}
			else
			{
				alarmSoundOff();
			}
			alarmActiveCounter--;
		}
		else
		{
			alarmSoundOff();
		}
	}
}