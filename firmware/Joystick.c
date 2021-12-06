#include <LUFA/Drivers/Peripheral/Serial.h>
#include "Joystick.h"

Virtual_Controller virtual_controller;

int main(void) {
  Serial_Init(9600, false);
  init();
  GlobalInterruptEnable();
	for (;;)
	{
	  receiveCommand();
		HID_Task();
		USB_USBTask();
	}
}

void init(void) {
	MCUSR &= ~(1 << WDRF);
	wdt_disable();
	clock_prescale_set(clock_div_1);
	USB_Init();
	initVirtualController();
}

void EVENT_USB_Device_Connect(void) {
}

void EVENT_USB_Device_Disconnect(void) {
}

void EVENT_USB_Device_ConfigurationChanged(void) {
	bool ConfigSuccess = true;
	ConfigSuccess &= Endpoint_ConfigureEndpoint(JOYSTICK_OUT_EPADDR, EP_TYPE_INTERRUPT, JOYSTICK_EPSIZE, 1);
	ConfigSuccess &= Endpoint_ConfigureEndpoint(JOYSTICK_IN_EPADDR, EP_TYPE_INTERRUPT, JOYSTICK_EPSIZE, 1);
}

void EVENT_USB_Device_ControlRequest(void) {
	switch (USB_ControlRequest.bRequest)
	{
		case HID_REQ_GetReport:
			if (USB_ControlRequest.bmRequestType == (REQDIR_DEVICETOHOST | REQTYPE_CLASS | REQREC_INTERFACE))
			{
			  USB_JoystickReport_Input_t joystickReport_Input;
			  memcpy(&joystickReport_Input,&virtual_controller,sizeof(virtual_controller));
				Endpoint_ClearSETUP();
				Endpoint_Write_Control_Stream_LE(&joystickReport_Input,sizeof(joystickReport_Input));
				Endpoint_ClearOUT();
			}

			break;
		case HID_REQ_SetReport:
			if (USB_ControlRequest.bmRequestType == (REQDIR_HOSTTODEVICE | REQTYPE_CLASS | REQREC_INTERFACE))
			{
				USB_JoystickReport_Output_t JoystickOutputData;
				Endpoint_ClearSETUP();
				Endpoint_Read_Control_Stream_LE(&JoystickOutputData, sizeof(JoystickOutputData));
				Endpoint_ClearIN();
			}
			break;
	}
}

void HID_Task(void) {
	if (USB_DeviceState != DEVICE_STATE_Configured)
	  return;
	Endpoint_SelectEndpoint(JOYSTICK_OUT_EPADDR);
	if (Endpoint_IsOUTReceived())
	{
		if (Endpoint_IsReadWriteAllowed())
		{
			USB_JoystickReport_Output_t JoystickOutputData;
			Endpoint_Read_Stream_LE(&JoystickOutputData, sizeof(JoystickOutputData), NULL);
		}
		Endpoint_ClearOUT();
	}
	Endpoint_SelectEndpoint(JOYSTICK_IN_EPADDR);
	if (Endpoint_IsINReady())
	{
		USB_JoystickReport_Input_t joystickReport_Input;
	  memcpy(&joystickReport_Input,&virtual_controller,sizeof(virtual_controller));
		Endpoint_Write_Stream_LE(&joystickReport_Input, sizeof(joystickReport_Input), NULL);
		Endpoint_ClearIN();
	}
}

void initVirtualController(void)
{
  Virtual_Controller* vp = &virtual_controller;
  memset(vp,0, sizeof(Virtual_Controller));
  vp->LX = STICK_CENTER;
  vp->LY = STICK_CENTER;
  vp->RX = STICK_CENTER;
  vp->RY = STICK_CENTER;
  vp->HAT = HAT_CENTER;
  vp->Button = SWITCH_RELEASE;
}


void receiveCommand(void){
  if(Serial_IsCharReceived()){
    do{}while(executeCommand(Serial_ReceiveByte()));
  }
}

bool executeCommand(int16_t c)
{
  if(c<0){
    return false;
  }
  Virtual_Controller* vp = &virtual_controller;
  switch(c){
    case 0:
      Serial_SendString("I'm Ready!");
      break;
    case 1:
      vp -> Button |= SWITCH_A;
      break;
    case 2:
      vp -> Button &= ~(1UL << 2 );
      break;
    case 3:
      vp -> Button |= SWITCH_B;
      break;
    case 4:
      vp -> Button &= ~(1UL << 1 );
      break;
    case 5:
      vp -> Button |= SWITCH_X;
      break;
    case 6:
      vp -> Button &= ~(1UL << 3 );
      break;
    case 7:
      vp -> Button |= SWITCH_Y;
      break;
    case 8:
      vp -> Button &= ~(1UL << 0 );
      break;
    case 9:
      vp -> Button |= SWITCH_L;
      break;
    case 10:
      vp -> Button &= ~(1UL << 4 );
      break;
    case 11:
      vp -> Button |= SWITCH_R;
      break;
    case 12:
      vp -> Button &= ~(1UL << 5 );
      break;
    case 13:
      vp -> Button |= SWITCH_ZL;
      break;
    case 14:
      vp -> Button &= ~(1UL << 6 );
      break;
    case 15:
      vp -> Button |= SWITCH_ZR;
      break;
    case 16:
      vp -> Button &= ~(1UL << 7 );
      break;
    case 17:
      vp -> Button |= SWITCH_LCLICK;
      break;
    case 18:
      vp -> Button &= ~(1UL << 10 );
      break;
    case 19:
      vp -> Button |= SWITCH_RCLICK;
      break;
    case 20:
      vp -> Button &= ~(1UL << 11 );
      break;
    case 21:
      vp -> HAT = HAT_TOP;
      break;
    case 22:
      vp -> HAT = HAT_CENTER;
      break;
    case 23:
      vp -> HAT = HAT_BOTTOM;
      break;
    case 24:
      vp -> HAT = HAT_CENTER;
      break;
    case 25:
      vp -> HAT = HAT_LEFT;
      break;
    case 26:
      vp -> HAT = HAT_CENTER;
      break;
    case 27:
      vp -> HAT = HAT_RIGHT;
      break;
    case 28:
      vp -> HAT = HAT_CENTER;
      break;
    case 29:
      vp -> Button |= SWITCH_START;
      break;
    case 30:
      vp -> Button &= ~(1UL << 9 );
      break;
    case 31:
      vp -> Button |= SWITCH_SELECT;
      break;
    case 32:
      vp -> Button &= ~(1UL << 8 );
      break;
    case 33:
      vp -> Button |= SWITCH_HOME;
      break;
    case 34:
      vp -> Button &= ~(1UL << 12 );
      break;
    case 35:
      vp -> Button |= SWITCH_CAPTURE;
      break;
    case 36:
      vp -> Button &= ~(1UL << 13 );
      break;
    case 37:
      vp -> LX = STICK_MIN;
      break;
    case 38:
      vp -> LX = STICK_MAX;
      break;
    case 39:
      vp -> LX = STICK_CENTER;
      break;
    case 40:
      vp -> LY = STICK_MIN;
      break;
    case 41:
      vp -> LY = STICK_MAX;
      break;
    case 42:
      vp -> LY = STICK_CENTER;
      break;
    case 43:
      vp -> RX = STICK_MIN;
      break;
    case 44:
      vp -> RX = STICK_MAX;
      break;
    case 45:
      vp -> RX = STICK_CENTER;
      break;
    case 46:
      vp -> LY = STICK_MIN;
      break;
    case 47:
      vp -> LY = STICK_MAX;
      break;
    case 48:
      vp -> LY = STICK_CENTER;
      break;
    case 49:
      initVirtualController();
      break;
    default:
      Serial_SendString("?");
      break;
  };
  return true;
}
