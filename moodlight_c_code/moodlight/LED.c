/*
 * LED.c
 *
 * Created: 06.04.2018 09:34:35
 *  Author: jonas
 */ 

#include <avr/io.h>
#include "Headerfiles/LED.h"

#define DAC_FREQ (0x1000 - 0x01)					// counter TOP value for PWM frequency of ~2kHz

void LED_initPWM(void)
{
	/* Output Configuration */
	DDRD |= (1<<DDRD0) | (1<<DDRD1) | (1<<DDRD2);	// START (T3.A) & DAC_3 (T4.A) & DAC_2 (T4.B)
	DDRB |= (1<<DDRB1) | (1<<DDRB2);				// DAC_0 (T1.A) & DAC_1 (T1.B)
	PORTD |= (1<<2);								// to fix a bug on the chip
	/* START (T3.A) */
	// reset registers	
	TCCR3A = 0;
	TCCR3B = 0;
	TCCR3C = 0;
	// configure control registers
	TCCR3A |= (1<<COM3A1);							// clear on Compare Match, set at BOTTOM
	TCCR3A |= (1<<WGM31);							// select Fast PWM Mode with ICR3 as TOP value
	TCCR3B |= (1<<WGM33) | (1<<WGM32);				// select Fast PWM Mode with ICR3 as TOP value
	TCCR3B |= (1<<CS30);							// select Prescaler = 1
	// set TOP and Output Compare values
	ICR3 = (uint16_t) (160-1);						// set TOP to 160 for a frequency of 50kHz
	OCR3A = (uint16_t) (1);							// set Outut Compare to 1 for a one clock long pulse
	
	/* DAC_0/1 (T1.A/B) */
	// reset registers
	TCCR1A = 0;
	TCCR1B = 0;
	TCCR1C = 0;
	// configure control registers
	TCCR1A |= (1<<COM1A1) | (1<<COM1B1);			// set at Bottom, clear on Compare Match
	TCCR1A |= (1<<WGM11);							// select Fast PWM Mode with ICR3 as TOP value
	TCCR1B |= (1<<WGM13) | (1<<WGM12);				// select Fast PWM Mode with ICR3 as TOP value
	TCCR1B |= (1<<CS10);							// select Prescaler = 1
	// set TOP value
	ICR1 = (uint16_t) DAC_FREQ;
	// set Output Compare values
	OCR1A = (uint16_t) INIT_VALUE;
	OCR1B = (uint16_t) INIT_VALUE;
	
	/* DAC_2/3 (T4.A/B) */
	// reset registers
	TCCR4A = 0;
	TCCR4B = 0;
	TCCR4C = 0;
	// configure control registers
	TCCR4A |= (1<<COM4A1) | (1<<COM4B1);			// set at Bottom, clear on Compare Match
	TCCR4A |= (1<<WGM41);							// select Fast PWM Mode with ICR3 as TOP value
	TCCR4B |= (1<<WGM43) | (1<<WGM42);				// select Fast PWM Mode with ICR3 as TOP value
	TCCR4B |= (1<<CS10);							// select Prescaler = 1
	// set TOP value
	ICR4 = (uint16_t) DAC_FREQ;
	// set Output Compare values
	OCR4A = (uint16_t) INIT_VALUE;
	OCR4B = (uint16_t) INIT_VALUE;
}

/*
* setValue
* 
* set duty cycle of the PWM
* input: uint8_t as percentage from 0 to 255
*/
void LED_setValue(pwm_channel select, uint8_t value)
{
	switch(select)
	{
		case CH1:
			OCR1A = (((uint16_t) value) * 20);
			break;
		case CH2:
			OCR1B = (((uint16_t) value) * 20);
			break;
		case CH3:
			OCR4B = (((uint16_t) value) * 20);
			break;
		case CH4:
			OCR4A = (((uint16_t) value) * 20);
			break;
		default:
			break;
	}
}

uint8_t LED_readValue(pwm_channel select)
{
	int value = 0;
	
	switch(select)
	{
		case CH1:
			value = OCR1A;
			break;
		case CH2:
			value = OCR1B;
			break;
		case CH3:
			value = OCR4B;
			break;
		case CH4:
			value = OCR4A;
			break;
		default:
			break;
	}
	
	return value;
}
