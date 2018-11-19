/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package centralizedgroups;

import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

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
        this.groupOwner = new GroupMember(memberAlias, hostname, idMember++, idGroup);
        
        // Añadimos el propietario a la lista de miembros 'groupMembers'
        this.groupMembers.add(this.groupOwner);        
    }
    
    
    public GroupMember isMember(String memberAlias)
    {
        try{
            this.lock.lock(); 
            if(!this.groupMembers.isEmpty()){
                for (GroupMember member : this.groupMembers) 
                {
                    if(member.alias.equals(memberAlias))
                        return member;
                }
            }
            return null;
        }finally { 
            this.lock.unlock();
        }
    }
    
    
    public GroupMember addMember(String memberAlias, String memberHostname)
    {
        if (this.membersAllowed){
            try{
                this.lock.lock(); 
                
                if(!this.groupMembers.isEmpty()){
                    for (GroupMember member : this.groupMembers) 
                    {
                        if (member.alias.equals(memberAlias)){
                            System.out.println("Ya existe un miembro con este alias " + member.alias);
                            return null;
                        }
                    }
                }
                GroupMember newMember = new GroupMember(memberAlias, memberHostname, this.idMember++, this.idGroup);
                this.groupMembers.add(newMember);
                return newMember;
            }finally { 
                this.lock.unlock();
            }
        }
        else{
            System.out.println("No es posible registrar un nuevo miembro es este momento");
            return null;
        }
    }
    
    
    public boolean removeMember(String memberAlias)
    {
        if (this.membersAllowed){
            try{
                this.lock.lock();
                
                if(this.groupOwner.alias != null){
                    if (this.groupOwner.alias.equals(memberAlias)){
                        System.out.println("No se puede eliminar el propietario del grupo " + memberAlias);
                        return false;
                    }
                    else{
                        if(!this.groupMembers.isEmpty()){
                            for (GroupMember member : this.groupMembers){
                                if (member.alias.equals(memberAlias)){
                                    this.groupMembers.remove(member);
                                    return true;
                                }
                            }
                        }
                            System.out.println("No se puede eliminar el miembro del grupo " + 
                                    memberAlias + " porque no es miembro del grupo" + this.aliasGroup + "");
                            return false;
                    }
                }
                System.out.println("No se puede eliminar el miembro porque este grupo no tiene propietario.");
                return false;
            }finally { 
                this.lock.unlock();
            }
        }
        else{
            System.out.println("No es posible eliminar un miembro es este momento.");
            return false;              
        }            
    }
    

    public void AllowMembers()
    {
        try{
            this.lock.lock();
            this.membersAllowed = true;
        }finally{
            this.lock.unlock();
        }
    }
    
    
    public void StopMembers()
    {
        try{
            this.lock.lock();
            this.membersAllowed = false;
        }finally{
            this.lock.unlock();
        }
    }
    
    
    public LinkedList<String> ListMembers(){
        
        LinkedList<String> membersList = new LinkedList<>();
        
        try{
            this.lock.lock();
        
            if(!this.groupMembers.isEmpty()){
                for(GroupMember member: this.groupMembers) 
                {
                      membersList.add(member.alias);
                }
            }
            return membersList;
        }finally{
            this.lock.unlock();
        }
        
    }
}
    
