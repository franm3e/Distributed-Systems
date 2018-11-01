/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package centralizedgroups;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author Julián Morales, Francisco Martínez
 */
public class GroupServer extends UnicastRemoteObject implements GroupServerInterface
{
    LinkedList<ObjectGroup> groupList;
    ReentrantLock lock;
    int groupID = 0;
    
    public GroupServer() throws RemoteException 
    {
        super();  // Para invocar al constructor de la superclase desde en constructor de la subclase
        groupList = new LinkedList<ObjectGroup>();
        lock = new ReentrantLock();
    }
    
    public static void main(String[] args) throws RemoteException {

        try
        {
            System.out.println("****************************************");
            System.out.println("********* CENTRALIZED GROUPS ***********");
            System.out.println("*************** SERVER *****************");
            System.out.println("****************************************");
            System.out.println("\n\n");
            
            GroupServer obj = new GroupServer();
            System.setProperty("java.security.policy", "C:\\Users\\Fran\\Desktop\\CentralizedGroups\\server.policy.txt");
            if (System.getSecurityManager() == null)
                System.setSecurityManager(new SecurityManager());
            LocateRegistry.createRegistry(1099);            
            Naming.rebind("//localhost:1099/GroupServer", obj);
            System.out.println("GroupServer is Ready");
        } 
        catch (MalformedURLException ex)
        {
            System.out.println("Error creating the GroupServer registry");
        }
    }

    
    @Override
    public int createGroup(String galias, String oalias, String ohostname) throws RemoteException
    {
        if (findGroup(galias) == -1)
        {
            ObjectGroup group = new ObjectGroup(groupID,galias,oalias,ohostname);
            groupID++;
            groupList.add(group);
            return group.idGroup;
        }
        else
            return -1;
    }

    @Override
    public int findGroup(String galias) throws RemoteException
    {
        for (ObjectGroup group : groupList)
        {
            if (group.aliasGroup.equals(galias))
                return group.idGroup;
        }
        return -1;
    }
    
    
    @Override
    public String findGroup(int gid)
    {
        for (ObjectGroup group : groupList)
        {
            if (group.idGroup == gid) 
            {
                return group.aliasGroup;
            }
        }
        return null;
    }


    public int getPosition(int gid)
    {
        for(int i = 0; i < groupList.size(); i++)
        {
            if (groupList.get(i).idGroup == gid)
                return i;
        }
        return -1;
    }

    
    @Override
    public boolean removeGroup(String galias, String oalias) throws RemoteException
    {
        try 
        {
            int gID = findGroup(galias);
            int pos = getPosition(gID);
            lock.lock();
            if (gID != -1 && groupList.get(pos).groupOwner.alias == oalias)
            {
                groupList.remove(pos);
                return true;
            }
            return false;
        } 
        finally 
        {
            lock.unlock();
        }
    }
    

    @Override
    public GroupMember addMember(String galias, String alias, String hostname) throws RemoteException
    {
        try
        {
            int gID = findGroup(galias);            
            lock.lock();
            GroupMember temp = isMember(galias,alias);
            if (gID != -1 && isMember(galias,alias) == null)
            {
                GroupMember newMember = groupList.get(gID).addMember(alias, hostname);
                return newMember;
            }
            return null;
        }
        finally
        {
            lock.unlock();
        }
    }

    @Override
    public GroupMember isMember(String galias, String alias) throws RemoteException
    {
        int gID = findGroup(galias);
        if (gID != -1)
            return groupList.get(gID).isMember(alias);
        else
            return null;
    }

    @Override
    public boolean StopMembers(String galias) throws RemoteException
    {
        try 
        {
            int gID = findGroup(galias);
            if (gID != -1)
            {
                lock.lock();
                groupList.get(gID).StopMembers();
                return true;
            }
            else
                return false;
        } 
        finally
        {
            lock.unlock();
        }
    }

    @Override
    public boolean AllowMembers(int gid) throws RemoteException
    {
        try 
        {
            String galias = findGroup(gid);
            int gID = findGroup(galias);  
            
            if (gID != -1)
            {
                lock.lock();
                groupList.get(gID).AllowMembers();
                return true;
            }
            else
                return false;
        } 
        finally
        {
            lock.unlock();
        }
    }   
    
    @Override
    public LinkedList<ObjectGroup> showGroups() throws RemoteException
    {
        return groupList;
    }

    @Override
    public boolean removeMember(String galias, String alias) throws RemoteException 
    {
        for (ObjectGroup group : groupList)
        {   
            if (group.aliasGroup == galias)
            {
                if (groupList.get(group.idGroup).removeMember(alias))
                    return true;
                else
                    return false;
            }
        }
        return false;
    }

    @Override
    public String showMembers() throws RemoteException
    {
        String list = "";
        for (ObjectGroup group: groupList)
        {
            list += "Group " + group.idGroup + " - " + group.aliasGroup + "\n";
            for (GroupMember member: group.groupMembers)
            {
                list += "\t";
                if(member.alias != null && group.groupOwner.alias != null){
                if (member.alias.equals(group.groupOwner.alias)){
                    list += "(Owner) ";
                }
                list += member.alias + "\n ";
                }
            }
        }
        return list;
    }

    @Override
    public LinkedList<String> ListMembers(String galias) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public LinkedList<String> ListGroup() throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}