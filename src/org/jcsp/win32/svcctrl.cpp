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
#include <string.h>

void _tmain (int argc, TCHAR **argv) {
	CServiceManager mgr(NULL);
	if (!mgr.ok()) {
		_ftprintf(stderr, TEXT ("Couldn't open service manager\n"));
		return;
	}
	if ((argc > 1) && (!_tcsicmp(argv[1], TEXT ("install")))) {
		if (argc != 4) {
			_ftprintf (stderr, TEXT ("%s install name path\n"), argv[0]);
			return;
		}
		CService svc(&mgr, argv[2]);
		svc.install(argv[2], argv[2], argv[3]);
		svc.start ();
		if (!svc.ok()) {
			_ftprintf(stderr, TEXT ("Couldn't install service\n"));
			return;
		}
	} else if ((argc > 1) && (!_tcsicmp(argv[1], TEXT ("remove")))) {
		if (argc != 3) {
			_ftprintf (stderr, TEXT ("%s remove name\n"), argv[0]);
			return;
		}
		CService svc(&mgr, argv[2]);
		svc.destroy();
	} else {
		_ftprintf(stderr, TEXT ("   %s install\nor %s remove\n"), argv[0], argv[0]);
		return;
	}
}
