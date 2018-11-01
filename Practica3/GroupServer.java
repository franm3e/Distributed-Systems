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
    int groupId = 0;
    
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
            System.setProperty("java.security.policy", centralizedgroups.Constants.CLIENT_POLICY);
            if (System.getSecurityManager() == null){
                System.setSecurityManager(new SecurityManager());
            }
            LocateRegistry.createRegistry(1099);            
            Naming.rebind("//localhost:1099/GroupServer", obj);
            System.out.println("El servidor GroupServer está listo.");
        } 
        catch (MalformedURLException ex)
        {
            System.out.println("Error creando el servidor de grupos GroupServer.");
            System.out.println(ex);
        }
    }

    
    @Override
    public int createGroup(String galias, String oalias, String ohostname) throws RemoteException
    {
        if (findGroup(galias) == -1)
        {
            ObjectGroup group = new ObjectGroup(this.groupId++, galias, oalias, ohostname);
            this.groupList.add(group);
            return group.idGroup;
        }
        else
            System.out.println("El grupo ya existe");
            return -1;
    }

    @Override
    public int findGroup(String galias) throws RemoteException
    {
        if(!this.groupList.isEmpty()){
            for (ObjectGroup group : this.groupList){
                if (group.aliasGroup.equals(galias)){
                    System.out.println("Ya existe un grupo llamado " + galias);
                    return group.idGroup;
                }
            }
        }
        System.out.println("El grupo no existe todavía en el servidor.");
        return -1;
    }
    
    
    @Override
    public String findGroup(int gid)
    {
        if(!this.groupList.isEmpty()){
            for (ObjectGroup group : this.groupList){
                if (group.idGroup == gid){
                    System.out.println("Ya existe un grupo llamado " + gid);
                    return group.aliasGroup;
                }
            }
        }
        System.out.println("El grupo no existe todavía en el servidor.");
        return null;
    }

    // Función auxiliar de ayuda para obtener la posición de un grupo dentro de la lista de grupos.
    public int getPosition(int gid)
    {
        for(int i = 0; i < this.groupList.size(); i++)
        {
            if (this.groupList.get(i).idGroup == gid)
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
            if (gID != -1 && this.groupList.get(pos).groupOwner.alias.equals(oalias)){
                this.groupList.remove(pos);
                System.out.println("Usuario borrado con exito.");
                return true;
            }
            System.out.println("No ha sido posible borrar el usuario " + oalias + " del grupo " + galias);
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
            this.lock.lock();
            if (gID != -1 && isMember(galias,alias) == null)
            {
                GroupMember newMember = this.groupList.get(gID).addMember(alias, hostname);
                return newMember;
            }
            return null;
        }
        finally
        {
            this.lock.unlock();
        }
    }

        
    @Override
    public boolean removeMember(String galias, String alias) throws RemoteException 
    {
        if(!this.groupList.isEmpty()){
            for (ObjectGroup group : this.groupList)
            {   
                if (group.aliasGroup.equals(galias))
                {
                    return this.groupList.get(group.idGroup).removeMember(alias);
                }
            }
        }
        return false;
    }
    
    
    @Override
    public GroupMember isMember(String galias, String alias) throws RemoteException
    {
        int gID = findGroup(galias);
        if (gID != -1)
            return this.groupList.get(gID).isMember(alias);
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
                this.lock.lock();
                this.groupList.get(gID).StopMembers();
                return true;
            }
            else
                return false;
        } 
        finally
        {
            this.lock.unlock();
        }
    }

    
    @Override
    public boolean AllowMembers(int gid) throws RemoteException
    {
        try{
            String galias = findGroup(gid);
            int gID = findGroup(galias);  
            
            if (gID != -1){
                this.lock.lock();
                this.groupList.get(gID).AllowMembers();
                return true;
            }
            else
                return false;
        } 
        finally{
            this.lock.unlock();
        }
    }   
    
    
    @Override
    public LinkedList<ObjectGroup> ListGroups() throws RemoteException
    {
        return this.groupList;
    }

    
    @Override
    public LinkedList<String> ListMembers(String galias) throws RemoteException
    {
        LinkedList<String> list = new LinkedList<String>();
        if(!this.groupList.isEmpty()){
            for(ObjectGroup group : this.groupList)
            {
                list.add("Group " + group.idGroup + " - " + group.aliasGroup + "\n");
                for(GroupMember member : group.groupMembers)
                {
                    list.add("\t");
                    if(member.alias != null && group.groupOwner.alias != null){
                        if(member.alias.equals(group.groupOwner.alias)){
                            list.add("(Owner) ");
                        }
                    list.add(member.alias + "\n ");
                    }
                }
            }
        }
    return list;
    }
}