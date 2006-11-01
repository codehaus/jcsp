    //////////////////////////////////////////////////////////////////////
    //                                                                  //
    //  JCSP ("CSP for Java") Libraries                                 //
    //  Copyright (C) 1996-2006 Peter Welch and Paul Austin.            //
    //                2001-2004 Quickstone Technologies Limited.        //
    //                                                                  //
    //  This library is free software; you can redistribute it and/or   //
    //  modify it under the terms of the GNU Lesser General Public      //
    //  License as published by the Free Software Foundation; either    //
    //  version 2.1 of the License, or (at your option) any later       //
    //  version.                                                        //
    //                                                                  //
    //  This library is distributed in the hope that it will be         //
    //  useful, but WITHOUT ANY WARRANTY; without even the implied      //
    //  warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR         //
    //  PURPOSE. See the GNU Lesser General Public License for more     //
    //  details.                                                        //
    //                                                                  //
    //  You should have received a copy of the GNU Lesser General       //
    //  Public License along with this library; if not, write to the    //
    //  Free Software Foundation, Inc., 59 Temple Place, Suite 330,     //
    //  Boston, MA 02111-1307, USA.                                     //
    //                                                                  //
    //  Author contact: P.H.Welch@ukc.ac.uk                             //
    //                                                                  //
    //                                                                  //
    //////////////////////////////////////////////////////////////////////

#include <windows.h>
#include <winsvc.h>
#include "NTService.h"
#include <tchar.h>

#ifdef _UNICODE
#define GetStringTChars				GetStringChars
#define ReleaseStringTChars			ReleaseStringChars
#else /* ifdef _UNICODE */
#define GetStringTChars				GetStringUTFChars
#define ReleaseStringTChars			ReleaseStringUTFChars
#endif /* ifdef _UNICODE */

static SERVICE_STATUS sta;
static SERVICE_STATUS_HANDLE hsta;
static TCHAR *serviceName = NULL;
static HANDLE hStartSemaphore, hStopSemaphore, hAckSemaphore;

static void setstate(int s) {
	if ((s == SERVICE_RUNNING) || (s == SERVICE_STOPPED)) {
		sta.dwCheckPoint = 0;
	} else {
		sta.dwCheckPoint++;
	}
	sta.dwCurrentState = s;
	SetServiceStatus(hsta, &sta);
}

static void WINAPI ctrlproc(DWORD ctrl) {
    if (ctrl == SERVICE_CONTROL_STOP) {
	    setstate(SERVICE_STOP_PENDING);
        ReleaseSemaphore (hStopSemaphore, 1, NULL);
    } else {
	    setstate(sta.dwCurrentState);
    }
}

static void WINAPI startproc(int argc, TCHAR **argv) {
    hsta = RegisterServiceCtrlHandler(serviceName, ctrlproc);
    sta.dwServiceType = SERVICE_WIN32_OWN_PROCESS;
    sta.dwServiceSpecificExitCode = 0;
    sta.dwControlsAccepted = SERVICE_ACCEPT_STOP;
    sta.dwWin32ExitCode = 0;
    sta.dwWaitHint = 3000;
    sta.dwCheckPoint = 0;
    setstate(SERVICE_RUNNING);
    ReleaseSemaphore (hStartSemaphore, 1, NULL);
    WaitForSingleObject (hAckSemaphore, INFINITE);
    setstate(SERVICE_STOPPED);
}

static SERVICE_TABLE_ENTRY dispatch[2] = {
	{ NULL, (LPSERVICE_MAIN_FUNCTION)startproc },
	{ NULL, NULL }
};

JNIEXPORT jint JNICALL Java_com_quickstone_win32_NTService_startDispatcher
	(JNIEnv *env, jobject obj) {

  jclass cls;
  jfieldID fid;
  jstring jstr;
  const TCHAR *str;
	
  if (serviceName) {
  	// This method has already been called (it shouldn't have been).
  	return -1;
  }
  
  // Lookup the service name
  cls = (*env)->GetObjectClass (env, obj);
  fid = (*env)->GetFieldID (env, cls, "serviceName", "Ljava/lang/String;");
  jstr = (*env)->GetObjectField (env, obj, fid);
  str = (*env)->GetStringTChars (env, jstr, 0);
  
  // Store the service name in a global buffer
  serviceName = strdup(str);
  dispatch[0].lpServiceName = serviceName;
  
  // Release the service name buffer
  (*env)->ReleaseStringTChars (env, jstr, str);

  // Try and start
  if (StartServiceCtrlDispatcher (dispatch)) return 0;
  
  // Return OK
  return GetLastError ();
}

JNIEXPORT void JNICALL Java_com_quickstone_win32_NTService_waitForStart
  (JNIEnv *env, jobject obj) {
	WaitForSingleObject (hStartSemaphore, INFINITE);
}

JNIEXPORT void JNICALL Java_com_quickstone_win32_NTService_waitForStop
  (JNIEnv *env, jobject obj) {
	WaitForSingleObject (hStopSemaphore, INFINITE);
}

JNIEXPORT void JNICALL Java_com_quickstone_win32_NTService_acknowledgeStop
  (JNIEnv *env, jobject obj) {
	ReleaseSemaphore (hAckSemaphore, 1, NULL);
}

JNIEXPORT void JNICALL Java_com_quickstone_win32_NTService_prepareSemaphores
  (JNIEnv *env, jobject obj) {
	hStartSemaphore = CreateSemaphore (NULL, 0, 1, NULL);
	hStopSemaphore = CreateSemaphore (NULL, 0, 1, NULL);
	hAckSemaphore = CreateSemaphore (NULL, 0, 1, NULL);
}
