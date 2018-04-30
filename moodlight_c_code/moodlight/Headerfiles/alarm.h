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
uint16_t getAlarmTime(void);
void setAlarmTime(uint16_t time);
void startAlarmSound(void);
void stopAlarmSound(void);

#endif


