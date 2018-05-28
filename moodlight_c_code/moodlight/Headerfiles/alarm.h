/*
 * alarm.h
 *
 * Created: 30.04.2018 08:36:10
 *  Author: Felix Baumann
 */ 

#ifndef ALARM_H_
#define ALARM_H_

/* function bodies*/

/* initializes timers, and interrupts used for the alarm feature of the moodlight */
void alarm_init(void);

/*interrupt handler for Timer2 interrupts*/
void TIMER2_IRQ(void);

void alarm_setVol(uint8_t value);

/*resets alarm sound and time*/
void resetAlarm(void);

/*returns the set alarm time in seconds*/
uint32_t getAlarmTime(void);

/*sets alarm time in seconds (max ~136 years)*/
void setAlarmTime(uint32_t time);

/*Sets how long the alarm sound is played in seconds*/
void setAlarmDuration(uint16_t alarmTime);

#endif


