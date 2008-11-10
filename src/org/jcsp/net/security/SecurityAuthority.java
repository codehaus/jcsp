//////////////////////////////////////////////////////////////////////
//                                                                  //
//  JCSP ("CSP for Java") Libraries                                 //
//  Copyright (C) 1996-2008 Peter Welch and Paul Austin.            //
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
//  Author contact: P.H.Welch@kent.ac.uk                             //
//                                                                  //
//                                                                  //
//////////////////////////////////////////////////////////////////////

package org.jcsp.net.security;

import org.jcsp.net.*;

/**
 * <p>Defines the service for dealing with authenticating users via a challenge/response scheme. Currently
 * only one user may be logged onto the security authority at any one time. The currently logged on user
 * will be used for creating the responses to challenges. Any number of users may be regarded as 'permitted'
 * and any response from one of these will be considered valid.</p>
 *
 * <p>An instance of the security authority service can be used to generate concrete user IDs and tokens
 * when users log on. This might be a purely internally resolved scheme or perhaps be linked to information
 * from a system level domain (eg the user's logon account).</p>
 *
 * <p>To negotiate starting a link, the security authorities at each end should create challenges to send.
 * The peer nodes will create responses from these challenges which indicate the log in of the user at that
 * node. The security authority creating the challenge can then be used to validate the response determining
 * whether the user generating the response is permitted to connect to this node.</p>
 *
 * <p>For example:</p>
 *
 * <pre>
 *        // Node 1                                           // Node 2
 * SecurityAuthority sa = ...;                         SecurityAuthority sa = ...;
 *
 * Challenge c = sa.createChallenge ();                // receive a challenge 'c' and send the response
 * // send 'c' to the other node and receive 'r'       Challenge c = ...;
 * Response r = ...;                                   Response r = sa.createResponse (c);
 *
 * if (sa.validateResponse (c, r)) {
 *   // access is permitted
 * } else {
 *   // access is denied
 * }
 * </pre>
 *
 * <p>To set the current user, ie the one which will create the response, use the <code>logonUser</code>
 * method. Obtaining a concrete user token is the responsibility of the concrete implementation. Similarly
 * creating the user IDs is the responsibility of the concrete implementation. No methods are defined in
 * this interface for these purposes because the number of parameters may vary depending on how users
 * authenticate. For example they may supply a username/password pair, just a username string
 * in a weaker system, or perhaps other, non-string credentials.</p>
 *
 * <p>To set the users which are currently permitted, ie will be considered to have generated a valid
 * response the <code>permitUserAccess</code> method must be used. To remove a user from this set the
 * <code>denyUserAccess</code> method should be used.</p>
 *
 * @author Quickstone Technologies Limited
 */
public interface SecurityAuthority extends ServiceUserObject, java.io.Serializable
{
   /**
    * <p>Creates and returns a new challenge object. The challenge should be used as soon as possible and
    * only once as it may be logged by the authority, timestamped or protected in some other way. The
    * caller should retain a copy for use in the <Code>validateResponse</code> method.</p>
    *
    * @return the challenge object.
    */
   public Challenge createChallenge();
   
   /**
    * <p>Determines if a response is valid for the given challenge. The challenge must have been generated
    * by a call to <code>createChallenge</code>. This should be called as soon as the response is
    * available and only once as there may be timestamping or other protection schemes in place.</p>
    *
    * @param c the challenge as returned by <code>createChallenge</code> and as passed to <code>createResponse</code>.
    * @param r the response from <code>createResponse</code>.
    * @return true if the response is valid and the user permitted. False otherwise.
    */
   public boolean validateResponse(Challenge c, Response r);
   
   /**
    * Create a response for the given challenge coded with the currently logged on user.
    *
    * @param c the challenge created by <code>createChallenge</code>.
    * @return the response to be returned to the originator authority.
    */
   public Response createResponse(Challenge c);
   
   /**
    * Sets the currently logged on user.
    *
    * @param u the token identifying an authenticated user.
    * @throws AccessDeniedException if the user token is not valid for this authority.
    */
   public void logonUser(UserToken u) throws AccessDeniedException;
   
   /**
    * Clears the currently logged on user.
    */
   public void logoffUser();
   
   /**
    * Adds a user ID to the set of users considered by this authority to create valid responses to
    * challenges.
    *
    * @param u the user ID to add.
    * @throws AccessDeniedException if the user ID is not valid for this authority.
    */
   public void permitUserAccess(UserID u) throws AccessDeniedException;
   
   /**
    * Removes a user ID from the set of users considered by this authority to create valid responses to
    * challenges.
    *
    * @param u the user ID to remove.
    * @throws AccessDeniedException if the user ID is not valid for this authority.
    */
   public void denyUserAccess(UserID u) throws AccessDeniedException;
}