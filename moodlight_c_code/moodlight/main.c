/*
 * moodlight.c
 *
 * Created: 06.04.2018 09:32:30
 * Author : jonas
 */ 

#include <avr/io.h>
#include <avr/interrupt.h>
#include "Headerfiles/LED.h"
#include "Headerfiles/alarm.h"
#include <util/delay.h>
#include "Headerfiles/UART.h"

#define F_CPU 8000000UL

int main(void)
{
	int counter = 0;
	int dir = 1;
	
	LED_initPWM();
	alarm_initPWM();
	UART_init();
	
    while (1) 
    {
		if ((counter <= 0xFF) && (counter >= 0x00))
		{
			UART_sendByte((uint8_t) (0x30));
			UART_sendByte((uint8_t) (0x1));
			UART_sendByte((uint8_t) (counter));
			
			/*LED_setValue(CH1, counter);
			LED_setValue(CH2, counter);
			LED_setValue(CH3, counter);
			LED_setValue(CH4, counter);*/
		}
		counter = counter + dir;
		if (counter >= 200)
		{
			dir = -1;
		}
		else if (counter <= 0x00)
		{
			dir = 1;
		}
		//_delay_ms(100);
    }
}

ISR(USART0_RX_vect)
{
	RX_IRQ();
}

