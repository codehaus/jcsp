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

#include "svcclasses.h"
#include <stdio.h>
#include <conio.h>

CServiceManager::CServiceManager(TCHAR *machine) {
	schSCManager = OpenSCManager(machine, NULL, SC_MANAGER_ALL_ACCESS);
}

CServiceManager::~CServiceManager(void) {
	if (schSCManager) CloseServiceHandle(schSCManager);
}

int CServiceManager::ok(void) {
	return (schSCManager != NULL);
}
 
CService::CService(CServiceManager *mgr, TCHAR *name) {
	schService = OpenService(schSCManager = mgr->schSCManager, name, SERVICE_ALL_ACCESS);
}

CService::~CService(void) {
	if (schService) CloseServiceHandle(schService);
}

int CService::ok(void) {
	return (schService != NULL);
}

void CService::install(TCHAR *name, TCHAR *disp, TCHAR *path) {
	if (schService) CloseServiceHandle(schService);
	schService = CreateService(
		schSCManager,               // SCManager database
		name,                       // name of service
		disp,                       // name to display
		SERVICE_ALL_ACCESS,         // desired access
		SERVICE_WIN32_OWN_PROCESS,  // service type
		SERVICE_AUTO_START,         // start type
		SERVICE_ERROR_NORMAL,       // error control type
		path,                       // service's binary
		NULL,                       // no load ordering group
		NULL,                       // no tag identifier
		NULL,                       // dependencies
		NULL,                       // LocalSystem account
		NULL);                      // no password
}

int CService::destroy(void) {
	return DeleteService(schService);
}

int CService::getState(void) {
	SERVICE_STATUS sta;
	if (!QueryServiceStatus(schService, &sta)) return 0;
	return sta.dwCurrentState;
}

int CService::isStarted(void) { return (getState() == SERVICE_RUNNING); }

int CService::isStopped(void) { return (getState() == SERVICE_STOPPED); }

void CService::start(void) {
	ChangeServiceConfig(
		schService,
		SERVICE_NO_CHANGE,
		SERVICE_AUTO_START,
		SERVICE_NO_CHANGE,
		NULL,
		NULL,
		NULL,
		NULL,
		NULL,
		NULL,
		NULL
	);
	StartService(schService, 0, NULL);
}

void CService::stop(void) {
	SERVICE_STATUS sta;
	ControlService(schService, SERVICE_CONTROL_STOP, &sta);
}

static SERVICE_STATUS          sta;
static SERVICE_STATUS_HANDLE   hsta;
static CMyService *service = NULL;
static TCHAR *service_name = NULL;

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
        service->stop();
    } else {
	    setstate(sta.dwCurrentState);
    }
}

static void WINAPI startproc(int argc, TCHAR **argv) {
    hsta = RegisterServiceCtrlHandler(service_name, ctrlproc);
    sta.dwServiceType = SERVICE_WIN32_OWN_PROCESS;
    sta.dwServiceSpecificExitCode = 0;
    sta.dwControlsAccepted = SERVICE_ACCEPT_STOP;
    sta.dwWin32ExitCode = 0;
    sta.dwWaitHint = 3000;
    sta.dwCheckPoint = 0;
    setstate(SERVICE_RUNNING);
    service->start(argc, argv);
    setstate(SERVICE_STOPPED);
}

static SERVICE_TABLE_ENTRY dispatch[2] = { { NULL, (LPSERVICE_MAIN_FUNCTION)startproc }, { NULL, NULL } };

void ntservice_schedule (TCHAR *name, CMyService *svc) {
	service_name = dispatch[0].lpServiceName = strdup(name);
	service = svc;
	StartServiceCtrlDispatcher (dispatch);
}
