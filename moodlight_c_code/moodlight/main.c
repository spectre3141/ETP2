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
	
	// alarm_init();
	LED_initPWM();
	UART_init();
	/*setAlarmTime(2000);
	setAlarmDuration(1500);*/

    while (1) 
    {
		// do nothing
	}
}
ISR(USART1_RX_vect)
{
	RX_IRQ();
}



