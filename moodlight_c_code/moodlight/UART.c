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

#define CHAR_0 0x30
#define CHAR_1 0x31

static volatile uint8_t buffer[BUFFER_SIZE];
static volatile uint8_t receiveData[BUFFER_SIZE];
uint8_t counter = 0;
uint8_t character;

void UART_init()
{
	// disable interrupts
	cli();
	// configure RX as input and TX as output
	DDRB &= ~(1<<DDRB4);
	DDRB |= (1<<DDRB3);
	// set baudrate
	UBRR1H = (uint8_t) (UART_BAUD>>8);
	UBRR1L = (uint8_t) (UART_BAUD & 0xFF);
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
		if (character != FRAME_DELIMITER)
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
	while (!(UCSR0A & (1<<UDRE1)));
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
void UART_deliverData()
{
	switch(receiveData[0])
	{
		case CHAR_0:
			// send to LED.c
			if ((receiveData[1] >= 1) && (receiveData[1] <= 4))
			{
				LED_setValue(receiveData[1], receiveData[2]);
			}
			break;
			
		case CHAR_1:
			// send to Alarm.c
			
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
