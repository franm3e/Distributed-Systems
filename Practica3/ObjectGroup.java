/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package centralizedgroups;

import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Julián Morales, Francisco Martínez
 */
public class ObjectGroup extends ReentrantLock {
    
    int idGroup;
    String aliasGroup;
    LinkedList<GroupMember> groupMembers;
    GroupMember groupOwner;
    int idMember = 0;
    ReentrantLock lock;
    Condition condition;
    boolean membersAllowed;
    
    
    public ObjectGroup(int idGroup, String aliasGroup, String memberAlias, String hostname)
    {
        this.groupMembers = new LinkedList<GroupMember>();
        this.membersAllowed = true;
        this.lock = new ReentrantLock(); 
        this.condition = lock.newCondition();

        // Establecemos groupID y groupAlias
        this.idGroup = idGroup;
        this.aliasGroup = aliasGroup;
        
        // Se crea un nuevo GroupMember que será el propietario del grupo e incrementamos contador.
        groupOwner = new GroupMember(memberAlias, hostname, idMember++, idGroup);
        
        // Añadimos el propietario a la lista de miembros 'groupMembers'
        groupMembers.add(groupOwner);        
    }
    
    
    public GroupMember isMember(String memberAlias)
    {
        if(!groupMembers.isEmpty()){
            for (GroupMember member : groupMembers) 
            {
                if (member.alias.equals(memberAlias))
                    return member;
            }
        }
            return null;
    }
    
    
    public GroupMember addMember(String memberAlias, String memberHostname)
    {
        if (membersAllowed){
            try{
                if (lock.isLocked()){
                    try {
                        condition.await();
                    }
                    catch (InterruptedException ex) {
                        System.out.println("El método addMember() fue interrumpido mientras esperaba una condición.\n");
                        System.out.println("El miembro " + memberAlias + " no pudo ser introducido en el grupo " + aliasGroup + "\n");
                        System.out.println(ex);
                    }
                }
                lock.lock(); 
                
                if(!this.groupMembers.isEmpty()){
                    for (GroupMember member : this.groupMembers) 
                    {
                        if (member.alias.equals(memberAlias)){
                            System.out.println("Ya existe un miembro con este alias " + member.alias);
                            return null;
                        }
                    }
                }
                GroupMember newMember = new GroupMember(memberAlias,memberHostname,idMember++,idGroup);
                this.groupMembers.add(newMember);
                return newMember;
            }
            finally
            { 
                if (lock.hasWaiters(condition)) {
                    condition.signal();
                }
                lock.unlock();
            }
        }
        else{
            System.out.println("No es posible registrar un nuevo miembro es este momento");
            return null;
        }
    }
    
    
    public boolean removeMember(String memberAlias)
    {
        if (membersAllowed){
            try{
                if (lock.isLocked()){
                    try{
                        condition.await();
                    }
                    catch (InterruptedException ex){
                        System.out.println("El método removeMember() fue interrumpido mientras esperaba una condición.\n");
                        System.out.println("El miembro " + memberAlias + " no pudo ser eliminado del grupo " + aliasGroup + "\n");
                        System.out.println(ex);
                    }
                }
                lock.lock();
                
                if(this.groupOwner.alias != null){
                    if (this.groupOwner.alias.equals(memberAlias)){
                        System.out.println("No se puede eliminar el propietario del grupo " + memberAlias + "\n");
                        return false;
                    }
                    else{
                        if(!this.groupMembers.isEmpty()){
                            for (GroupMember member : groupMembers){
                                if (member.alias.equals(memberAlias)){
                                    groupMembers.remove(member);
                                    return true;
                                }
                            }
                        }
                            System.out.println("No se puede eliminar el miembro del grupo " + 
                                    memberAlias + " porque no es miembro del grupo" + this.aliasGroup + "\n");
                            return false;
                    }
                }
                System.out.println("No se puede eliminar el miembro porque este grupo no tiene propietario.\n");
                return false;
            }
            finally
            { 
                if (lock.hasWaiters(condition)) {
                    condition.signal();
                }
                lock.unlock();
            }
        }
        else{
            System.out.println("No es posible eliminar un miembro es este momento");
            return false;              
        }            
    }
    
    /**
     * Permite la modificación de miembros del grupo.
     */
    public void AllowMembers()
    {
        try 
        {
            if (lock.isLocked()) 
            {
                try
                {
                    condition.await();
                } 
                catch (InterruptedException ex) 
                {
                    Logger.getLogger(ObjectGroup.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            lock.lock();
            membersAllowed = true;
        } 
        finally 
        {
            condition.signal();
            lock.unlock();
        }
    }
    
    /**
     * Impide la modificación de miembros del grupo.
     */
    public void StopMembers()
    {
         try 
         {
            if (lock.isLocked()) 
            {
                try 
                {
                    condition.await();
                } 
                catch (InterruptedException ex)
                {
                    Logger.getLogger(ObjectGroup.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            lock.lock();
            membersAllowed = false;
        } 
        finally 
        {
            condition.signal();
            lock.unlock();
        }
    }
    
    //Devuelve la lista de nombres de los miembros del grupo, mostrando los alias.
    LinkedList<String> ListMembers(){
        
        LinkedList<String> membersList = new LinkedList<>();
        
        for (GroupMember member : groupMembers) 
        {
              membersList.add(member.alias);
        }
        
        return membersList;
      
    }
}
    
