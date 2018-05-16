/*
 * moodlight.c
 *
 * Created: 06.04.2018 09:32:30
 * Author : jonas
 */ 

#include <util/delay.h>
#include <avr/io.h>
#include <avr/interrupt.h>

#include "Headerfiles/LED.h"
#include "Headerfiles/alarm.h"
#include "Headerfiles/UART.h"

#define F_CPU 8000000UL

/*ISR(TIMER2_COMPA_vect)
{
	TIMER2_IRQ();
}*/
int main(void)
{
	int counter = 0;
	int dir = 1;
	uint8_t buffer[3];
	
	alarm_init();
	LED_initPWM();
	UART_init();
	/*setAlarmTime(2000);
	setAlarmDuration(1500);*/
	
    while (1) 
    {
		/*
			UART_sendByte((uint8_t) (0x30));
			UART_sendByte((uint8_t) (0x1));
			UART_sendByte((uint8_t) (counter));
			*/
			
		

		
    }
}
ISR(USART1_RX_vect)
{
	RX_IRQ();
}



