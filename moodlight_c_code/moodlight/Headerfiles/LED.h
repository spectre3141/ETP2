/*
 * LED.h
 *
 * Created: 06.04.2018 09:35:41
 *  Author: jonas
 */ 


#ifndef LED_H_
#define LED_H_

#define INIT_VALUE 0x00;

// constants
typedef enum
{
	CH_RED = 1, 
	CH_GREEN = 2, 
	CH_BLUE = 3, 
	CH_WHITE = 4
} pwm_channel;

// function bodies
void LED_initPWM(void);
void LED_setValue(pwm_channel select, uint8_t value);
uint8_t LED_readValue(pwm_channel select);

#endif /* LED_H_ */