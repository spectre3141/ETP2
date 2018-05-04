/*
 * UART.h
 *
 * Created: 20.04.2018 10:27:41
 *  Author: jonas
 */ 

// /*
#ifndef F_CPU
#define F_CPU 8000000UL
#endif

#ifndef UART_H_
#define UART_H_

// define baudrate and prescaler for the uart-module
#define BAUDRATE 9600UL
#define UART_BAUD (F_CPU/(16*BAUDRATE)-1)

// other defines
#define FRAME_DELIMITER 0xD
#define BUFFER_SIZE 16

// function prototypes
void UART_init(void);
void RX_IRQ(void);
void UART_sendByte(uint8_t byte);
void UART_sendBuffer(uint8_t * data, uint8_t length);
uint8_t * UART_getData(void);

#endif /* UART_H_ */
