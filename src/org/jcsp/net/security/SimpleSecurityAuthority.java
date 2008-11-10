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
import java.util.*;
import java.util.prefs.*;

/**
 * <p>Provides a basic security authority based on unique names. No passwords are used - a user is
 * identified by a name which is guarded by a minimal protection scheme. This class is supplied as
 * an example of implementing a security authority and not a robust implementation suitable for long
 * term use.</p>
 *
 * @author Quickstone Technologies Limited
 */
public class SimpleSecurityAuthority implements SecurityService
{
   // Constants //////////////////////////////////////////////////////////////////////////////
   
   /**
    * Sets the length of the challenge packet.
    */
   private static final int CHALLENGE_LENGTH = 80;
   
   // Local data /////////////////////////////////////////////////////////////////////////////
   
   /** The token of the current user. */
   private UserToken currentUser = null;
   
   /** A random number generater for creating challenges. */
   private static final Random rnd = new Random();
   
   /** Set of permitted users. */
   private Vector allowedUsers = new Vector();
   
   /** Flag indicating if the service is running or not. */
   private boolean serviceRunning = false;
   
   /** The user control object that avoids giving out the full administrative interface. */
   private SecurityAuthority userObject = null;
   
   // Constructors ///////////////////////////////////////////////////////////////////////////
   
   /**
    * Creates a new simple authority. The current username will be found from the preferences if
    * available under the "user" variable. The system property "org.jcsp.net.security.user"
    * will be checked first and take preference. If no user is found the name "default_user" is assumed.
    */
   public SimpleSecurityAuthority()
   {
      try
      {
         String userName = System.getProperty("org.jcsp.net.security.user");
         if (userName == null)
         {
            try
            {
               if (Class.forName("java.util.prefs.Preferences") != null)
                  userName = getUserFromPrefs();
            }
            catch (ClassNotFoundException e)
            {
            }
         }
         if (userName == null) 
            userName = "default_user";
         UserID uID = createUserID(userName);
         UserToken uTk = createUserToken(uID);
         logonUser(uTk);
      }
      catch (AccessDeniedException e)
      {
      }
   }
   
   // Methods from SecurityAuthority /////////////////////////////////////////////////////////
   
   /**
    * Creates a new challenge packet containing a timestamp and some random data. The response must be
    * returned within around 1 minute for the response to be considered valid so don't keep it too long.
    *
    * @return the new challenge packet.
    */
   public synchronized Challenge createChallenge()
   {
      SimpleChallenge c = new SimpleChallenge();
      c.fillRandom();
      Node.info.log(this, "creating challenge " + c.hashCode());
      return c;
   }
   
   /**
    * Checks if the generated response corresponds to one expected from a permitted user. The response must
    * have come within a minute of the challenge being generated.
    *
    * @param c the challenge returned by <code>createChallenge</code>.
    * @param r the response generated by the other authority.
    * @return true if the response is permitted.
    */
   public synchronized boolean validateResponse(Challenge c, Response r)
   {
      Node.info.log(this, "validating response to " + c.hashCode());
      if ((c == null) || (r == null) || (!(c instanceof SimpleChallenge)) || (!(r instanceof SimpleResponse)))
      {
         Node.info.log(this, "not a valid response/challenge object (null)");
         return false;
      }
      SimpleChallenge _c = (SimpleChallenge)c;
      SimpleResponse _r = (SimpleResponse)r;
      // Check the length is correct
      if ((_c.data.length != CHALLENGE_LENGTH) || (_r.data.length != CHALLENGE_LENGTH))
      {
         Node.info.log(this, "not a valid response/challenge object (length invalid)");
         return false;
      }
      // Check it is recent (allow 1 minute earlier; no future time)
      long tNow = System.currentTimeMillis();
      if ((_c.timestamp < tNow - 60000) || (_c.timestamp > tNow))
      {
         Node.info.log(this, "timestamp invalid");
         return false;
      }
      // Look up the users permitted to access this node (ie connect to it)
      byte[] data = new byte[CHALLENGE_LENGTH];
      for (Enumeration e = allowedUsers.elements(); e.hasMoreElements(); )
      {
         SimpleUserID uID = (SimpleUserID)e.nextElement();
         createResponse(_c, uID, data);
         boolean match = true;
         for (int i = 0; i < CHALLENGE_LENGTH; i++)
         {
            if (data[i] != _r.data[i])
            {
               match = false;
               break;
            }
         }
         if (match)
            return true;
      }
      return false;
   }
   
   /**
    * Creates a response to the challenge based on the currently logged in user.
    *
    * @param c the challenge to respond to.
    * @return the response.
    */
   public synchronized Response createResponse(Challenge c)
   {
      Node.info.log(this, "creating response to challenge " + c.hashCode());
      if (currentUser == null)
         throw new RuntimeException("No user is currently logged in");
      if ((c == null) || (!(c instanceof SimpleChallenge)))
         throw new RuntimeException("Invalid challenge for this security authority");
      SimpleChallenge _c = (SimpleChallenge)c;
      if (_c.data.length != CHALLENGE_LENGTH)
         throw new RuntimeException("Invalid challenge for this security authority");
      byte[] data = new byte[CHALLENGE_LENGTH];
      createResponse(_c, (SimpleUserID)currentUser.getUserID(), data);
      return new SimpleResponse(data);
   }
   
   /**
    * Sets the currently logged on user. If there is already a user logged in, they are logged off.
    *
    * @param u the user's token.
    */
   public synchronized void logonUser(UserToken u) throws AccessDeniedException
   {
      if (currentUser != null)
         logoffUser();
      if ((u == null) || (!(u instanceof SimpleUserToken)))
         throw accessDenied("invalid user token");
      currentUser = u;
      permitUserAccess(u.getUserID());
   }
   
   /**
    * Clears the currently logged on user. After this call the <code>createResponse</code> method will
    * fail until another user is logged in.
    */
   public synchronized void logoffUser()
   {
      if (currentUser == null)
         throw new RuntimeException("There is no current user");
      try
      {
         denyUserAccess(currentUser.getUserID());
      }
      catch (AccessDeniedException e)
      {
         // no action
      }
      currentUser = null;
   }
   
   /**
    * Adds a user to the set of permitted users.
    *
    * @param u the user ID to add.
    * @throws AccessDeniedException if the ID was not allocated by this authority.
    */
   public synchronized void permitUserAccess(UserID u) throws AccessDeniedException
   {
      if ((u == null) || (!(u instanceof SimpleUserID))) 
         throw accessDenied("invalid user ID");
      allowedUsers.addElement(u);
   }
   
   /**
    * Removes a user from the set of permitted users.
    *
    * @param u the user ID to remove.
    * @throws AccessDeniedException if the ID was not allocated by this authority.
    */
   public synchronized void denyUserAccess(UserID u) throws AccessDeniedException
   {
      if ((u == null) || (!(u instanceof SimpleUserID))) 
         throw accessDenied("invalid user ID");
      allowedUsers.removeElement(u);
   }
   
   // Methods from Service ///////////////////////////////////////////////////////////////////
   
   /**
    * Sets the service running.
    *
    * @return true - this service can always start.
    */
   public boolean start()
   {
      Node.info.log(this, "service starting");
      serviceRunning = true;
      return true;
   }
   
   /**
    * Stops the service.
    *
    * @return true - this service can always stop.
    */
   public boolean stop()
   {
      Node.info.log(this, "service stopping");
      serviceRunning = false;
      return true;
   }
   
   /**
    * Returns true iff the service is running.
    */
   public boolean isRunning()
   {
      return serviceRunning;
   }
   
   /**
    * <p>Initializes the service, setting a current user and the list of permitted users from the XML
    * configuration file. For example:</p>
    *
    * <pre>
    *        &lt;SERVICE ...&gt;
    *           &lt;SETTING name="logonUser" value="foo@bar.com"/&gt;
    *           &lt;SETTING name="permitUser0" value="a"/&gt;
    *           &lt;SETTING name="permitUser1" value="b"/&gt;
    *        &lt;/SETTING&gt;
    * </pre>
    *
    * <p>This sets the current user to be "foo@bar.com" but will allow responses from users "a" and "b".</p>
    */
   public boolean init(ServiceSettings s)
   {
      if (s != null)
      {
         String user = s.getSetting("logonUser");
         if (user != null)
         {
            try
            {
               logonUser(createUserToken(createUserID(user)));
            }
            catch (AccessDeniedException e)
            {
               return false;
            }
         }
         for (int i = 0; ; i++)
         {
            user = (String)s.getSetting("permitUser" + i);
            if (user == null) 
               break;
            try
            {
               permitUserAccess(createUserID(user));
            }
            catch (AccessDeniedException e)
            {
               return false;
            }
         }
      }
      return true;
   }
   
   /**
    * Returns the authority interface for this service. A seperate user object is returned to avoid
    * giving away the service control interface also.
    */
   public ServiceUserObject getUserObject()
   {
      if (userObject == null)
      {
         final SimpleSecurityAuthority lsa = this;
         userObject = new SecurityAuthority()
                      {
                         public Challenge createChallenge()
                         {
                            return lsa.createChallenge();
                         }

                         public boolean validateResponse(Challenge c, Response r)
                         {
                            return lsa.validateResponse(c, r);
                         }

                         public Response createResponse(Challenge c)
                         {
                            return lsa.createResponse(c);
                         }

                         public void logonUser(UserToken u) throws AccessDeniedException
                         {
                            lsa.logonUser(u);
                         }

                         public void logoffUser()
                         {
                            lsa.logoffUser();
                         }

                         public void permitUserAccess(UserID u) throws AccessDeniedException
                         {
                            lsa.permitUserAccess(u);
                         }

                         public void denyUserAccess(UserID u) throws AccessDeniedException
                         {
                            lsa.denyUserAccess(u);
                         }
                      };
      }
      return userObject;
   }
   
   // Other methods //////////////////////////////////////////////////////////////////////////
   
   /**
    * Creates and returns a user ID valid for this authority that represents the given user name.
    *
    * @param username the unique user name.
    * @return the user ID.
    */
   public UserID createUserID(String username)
   {
      return new SimpleUserID(username);
   }
   
   /**
    * Creates and returns an authentication token valid for this authority that represents the given
    * user name. Note that no additional credentials are supplied because this authority does not support
    * passwords or anything more secure (hence the word 'Simple' in its name :).
    *
    * @param user the user ID to authenticate.
    * @return the authentication token.
    * @throws AccessDeniedException if the user ID is not valid for this authority.
    */
   public UserToken createUserToken(UserID user) throws AccessDeniedException
   {
      // Discard a user ID not from this security authority
      if ((user == null) || (!(user instanceof SimpleUserID)))
         throw accessDenied("invalid user token");
      // Assume user ID is permitted to use this node
      return new SimpleUserToken(user);
   }
   
   /**
    * Returns a string description of this authority.
    */
   public String toString()
   {
      return getClass().getName();
   }
   
   /**
    * Creates a response for the given challenge using a given user ID. This is used to create an actual
    * response and also to create the expected response for a given user.
    *
    * @param c the challenge request.
    * @param u the user ID to create a response for.
    * @param b the array to place the response in.
    */
   private void createResponse(SimpleChallenge c, SimpleUserID u, byte[] b)
   {
      long tScroll = c.timestamp;
      int nx = 0;
      for (int i = 0; i < b.length; i++)
      {
         byte m = 0;
         if (tScroll > 0)
         {
            m = (byte)(tScroll & 0xff);
            tScroll = (tScroll >> 1);
         }
         m += (byte)u.name.charAt(nx);
         if (m == 0) 
            m = 1;
         b[i] = (byte)(c.data[i] % m);
         nx = (nx + 1) % u.name.length();
      }
   }
   
   /**
    * Creates and returns an exception associated with this authority.
    *
    * @param reason the reason field of the exception.
    */
   private AccessDeniedException accessDenied(String reason)
   {
      return new AccessDeniedException(this, reason);
   }
   
   /**
    * Returns the initial username if one is specified in the preferences. The user preferences take
    * precedence over the system preferences.
    *
    * @return the user name or null if none is found.
    */
   ////#1.4+
   private String getUserFromPrefs()
   {
      String user = Preferences.userNodeForPackage(getClass()).get("user", null);
      if (user == null)
         user = Preferences.systemNodeForPackage(getClass()).get("user", null);
      return user;
   }
   ////#+
   
   // Private classes ////////////////////////////////////////////////////////////////////////
   
   /**
    * Represents the user ID for this authority.
    *
    * @author Quickstone Technologies Limited
    */
   private static final class SimpleUserID implements UserID
   {
      public final String name;
      
      public SimpleUserID(String name)
      {
         this.name = name;
      }
      
      public String toString()
      { 
         return "UserID:" + name; 
      }
   }
   
   /**
    * Represents the authenticated user token for this authority.
    *
    * @author Quickstone Technologies Limited
    */
   private static final class SimpleUserToken implements UserToken
   {
      private UserID uID;
      
      public SimpleUserToken(UserID uID)
      {
         this.uID = uID;
      }
      
      public UserID getUserID()
      {
         return uID;
      }
      
      public String toString()
      { 
         return "UserToken:" + ((SimpleUserID)uID).name; 
      }
   }
   
   /**
    * The challenge for this authority containing a timestamp and block of random data.
    *
    * @author Quickstone Technologies Limited
    */
   private static final class SimpleChallenge implements Challenge
   {
      
      public long timestamp;
      public byte[] data;
      
      public SimpleChallenge()
      {
      }
      
      public void fillRandom()
      {
         timestamp = System.currentTimeMillis();
         data = new byte[CHALLENGE_LENGTH];
         rnd.nextBytes(data);
      }
      
   }
   
   /**
    * The response for this authority, consisting of a block of data which has been mangled based on the
    * timestamp and ID of the user creating the response.
    *
    * @author Quickstone Technologies Limited
    */
   private static final class SimpleResponse implements Response
   {
      public final byte[] data;
      
      public SimpleResponse(byte[] data)
      {
         this.data = data;
      }
   }
}