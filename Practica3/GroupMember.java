/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package centralizedgroups;

/**
 *
 * @author Julián Morales, Francisco Martínez
 */
public class GroupMember implements java.io.Serializable {

    String alias;
    String hostname;
    int idMember;
    int idGroup;

    public GroupMember(String alias, String hostname, int idMember, int idGroup){
        this.alias= alias;
        this.hostname= hostname;
        this.idMember= idMember;
        this.idGroup= idGroup;
    }
}
