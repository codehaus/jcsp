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

#ifndef __inc_svcclasses_h
#define __inc_svcclasses_h

#ifndef __cplusplus
#error "This file contains C++ classes"
#endif /* ifndef __cplusplus */

#include <windows.h>
#include <tchar.h>

class CServiceManager {
private:
	SC_HANDLE schSCManager;
	friend class CService;
public:
	CServiceManager(TCHAR *machine);
	~CServiceManager(void);
	int ok(void);
};
 
class CService {
private:
	SC_HANDLE schService, schSCManager;
public:
	CService(CServiceManager *mgr, TCHAR *name);
	~CService(void);
	int ok(void);
	void install(TCHAR *name, TCHAR *disp, TCHAR *path);
	int destroy(void);
	int getState(void);
	int isStarted(void);
	int isStopped(void);
	void start(void);
	void stop(void);
};

class CMyService {
public:
	virtual void start (int argc, TCHAR **argv) = 0;
	virtual void stop (void) = 0;
};

void ntservice_schedule (TCHAR *name, CMyService *svc);

#endif /* ifndef __inc_svcclasses_h */
