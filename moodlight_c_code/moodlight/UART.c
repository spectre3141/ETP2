/*
 * UART.c
 *
 * Created: 20.04.2018 10:27:15
 *  Author: jonas
 */ 

#include <avr/io.h>
#include <avr/interrupt.h>
#include "Headerfiles/UART.h"
#include "Headerfiles/LED.h"

#define MAX_MESSAGE_LEN 6		// defines the maximum length of the frames (without frame delimiter)
#define CODE_LED 0x30
#define CODE_ALARM 0x31
#define CODE_RECEIVE 0x3E		// 0x3E = ">": data received contains values to be set in the moodlight
#define CODE_REQUEST 0x3C		// 0x3C = "<": request for the moodlight to send the according data

static volatile uint8_t buffer[BUFFER_SIZE];
static volatile uint8_t receiveData[BUFFER_SIZE];
static volatile uint8_t counter = 0;
volatile uint8_t character;

void UART_init()
{
	// disable interrupts
	cli();
	// configure RX as input and TX as output
	DDRB &= ~(1<<DDRB4);
	DDRB |= (1<<DDRB3);
	// set baudrate
	UBRR1H = (uint8_t) (UART_BAUD>>8);
	UBRR1L = (uint8_t) (UART_BAUD);
	// reset control register A
	UCSR1A = 0x0;
	// enable transmitter and receiver
	UCSR1B = 0x0;
	UCSR1B |= (1<<TXEN1) | (1<<RXEN1);
	// enable receive complete interrupt
	UCSR1B |= (1<<RXCIE1);
	// configure frame format
	// 8-bit frames, no parity, 1 stopbit
	UCSR1C = 0x0;
	UCSR1C = (1<<UCSZ11) | (1<<UCSZ10);
	// enable interrupts
	sei();
}

void RX_IRQ(void)
{
		character = UDR1;
		if ((character != FRAME_DELIMITER) || (counter < MAX_MESSAGE_LEN))
		{
			buffer[counter] = character;
			counter++;
		}
		else
		{
			int i;
			for (i = 0; i < BUFFER_SIZE; i++)
			{
				receiveData[i] = buffer[i];
			}
			counter = 0;
			UART_deliverData();
		}
}

void UART_sendByte(uint8_t data)
{
	while (!(UCSR1A & (1<<UDRE1)));
	UDR1 = data;
}

void UART_sendBuffer(uint8_t * pointer, uint8_t length)
{
	uint8_t i = 0;
	
	// enter for loop to send the buffer
	for (i = 0; i < length; i++)
	{
		UART_sendByte(*(pointer + i));
	}
	UART_sendByte(FRAME_DELIMITER);
}

/*
* reads the read data's first character, and decides which
* module to pass the data to.
*/
void UART_deliverData(void)
{	
	switch(receiveData[0])
	{
		case CODE_LED:
			// send to LED.c
			if (receiveData[1] == CODE_RECEIVE)
			{
				int i;
				for (i = 0; i < 4; i++)
				{
					LED_setValue(receiveData[i + 1], receiveData[i + 2]);
				}
			}
			else if (receiveData[1] == CODE_REQUEST)
			{
				// possibility to insert a function to send the current values of the PWM's
			}
			break;
			
		case CODE_ALARM:
			// send to Alarm.c
			if (receiveData[1] == CODE_RECEIVE)
			{
				// set Alarm time
			}
			else if (receiveData[1] == CODE_REQUEST)
			{
				// send current alarm time
			}
			break;
			
		default:
			// do nothing
			break;			
	}
}

uint8_t * UART_getData()
{
	return receiveData;
}

/* (un-)comment structure) */
