#ifndef _JOYSTICK_H_
#define _JOYSTICK_H_
#include <avr/io.h>
#include <avr/wdt.h>
#include <avr/power.h>
#include <avr/interrupt.h>
#include <string.h>
#include <LUFA/Drivers/USB/USB.h>
#include <LUFA/Drivers/Board/Joystick.h>
#include <LUFA/Drivers/Board/LEDs.h>
#include <LUFA/Drivers/Board/Buttons.h>
#include <LUFA/Platform/Platform.h>
#include "Descriptors.h"
typedef enum {
	SWITCH_Y       = 0x01,
	SWITCH_B       = 0x02,
	SWITCH_A       = 0x04,
	SWITCH_X       = 0x08,
	SWITCH_L       = 0x10,
	SWITCH_R       = 0x20,
	SWITCH_ZL      = 0x40,
	SWITCH_ZR      = 0x80,
	SWITCH_SELECT  = 0x100,
	SWITCH_START   = 0x200,
	SWITCH_LCLICK  = 0x400,
	SWITCH_RCLICK  = 0x800,
	SWITCH_HOME    = 0x1000,
	SWITCH_CAPTURE = 0x2000,
  SWITCH_RELEASE = 0x00,
} JoystickButtons_t;
#define HAT_TOP          0x00
#define HAT_TOP_RIGHT    0x01
#define HAT_RIGHT        0x02
#define HAT_BOTTOM_RIGHT 0x03
#define HAT_BOTTOM       0x04
#define HAT_BOTTOM_LEFT  0x05
#define HAT_LEFT         0x06
#define HAT_TOP_LEFT     0x07
#define HAT_CENTER       0x08
#define STICK_MIN      0
#define STICK_CENTER 128
#define STICK_MAX 255
typedef struct {
	uint16_t Button;
	uint8_t  HAT;
	uint8_t  LX;
	uint8_t  LY;
	uint8_t  RX;
	uint8_t  RY;
	uint8_t  VendorSpec;
} USB_JoystickReport_Input_t,Virtual_Controller;
typedef struct {
	uint16_t Button;
	uint8_t  HAT;
	uint8_t  LX;
	uint8_t  LY;
	uint8_t  RX;
	uint8_t  RY;
} USB_JoystickReport_Output_t;
void init(void);
void HID_Task(void);
void EVENT_USB_Device_Connect(void);
void EVENT_USB_Device_Disconnect(void);
void EVENT_USB_Device_ConfigurationChanged(void);
void EVENT_USB_Device_ControlRequest(void);
void GetNextReport(USB_JoystickReport_Input_t* const ReportData);
void receiveCommand(void);
bool executeCommand(int16_t c);
void initVirtualController(void);
#endif

