/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package centralizedgroups;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.LinkedList;

/**
 *
 * @author Julián Morales, Francisco Martínez
 */
public interface GroupServerInterface extends Remote {
    
    int createGroup(String galias, String oalias, String ohostname) throws RemoteException;

    int findGroup(String galias) throws RemoteException;
    
    String findGroup(int gid) throws RemoteException;

    boolean removeGroup(String galias, String oalias) throws RemoteException; 
    
    GroupMember addMember(String galias, String alias, String hostname) throws RemoteException;
 
    boolean removeMember(String galias, String alias) throws RemoteException;

    GroupMember isMember(String galias, String alias) throws RemoteException;

    boolean StopMembers(String galias) throws RemoteException;
 
    boolean AllowMembers(int gid) throws RemoteException;

    LinkedList<String> ListMembers(String galias) throws RemoteException;

    LinkedList<String> ListGroup() throws RemoteException;
    
    LinkedList<ObjectGroup> showGroups() throws RemoteException;

    String showMembers() throws RemoteException;
    
}
