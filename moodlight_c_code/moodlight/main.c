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
#include "Headerfiles/UART.h"


ISR(TIMER2_COMPA_vect)
{
	TIMER2_IRQ();
}
ISR(USART1_RX_vect)
{
	RX_IRQ();
}

int main(void)
{
	alarm_init();
	LED_initPWM();
	UART_init();
	
	setAlarmDuration(10);		//set duration of the alarm in seconds

    while (1) 
    {
		//wait for event (interrupts)
	}
}




