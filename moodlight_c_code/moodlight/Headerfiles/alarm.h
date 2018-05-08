/*
 * alarm.h
 *
 * Created: 30.04.2018 08:36:10
 *  Author: Felix Baumann
 */ 

#ifndef ALARM_H_
#define ALARM_H_

/* function bodies*/

void alarm_initPWM(void);
void resetAlarm(void);
uint32_t getAlarmTime(void);
void setAlarmTime(uint32_t time);
void TIMER2_IRQ(void);

#endif


