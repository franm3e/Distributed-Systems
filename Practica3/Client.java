/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package centralizedgroups;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

/**
 *
 * @author Julián Morales, Francisco Martínez
 */
public class Client extends UnicastRemoteObject
{
   
    public static void main(String[] args) throws RemoteException, IOException 
    {
        try 
        {
            System.out.println("**************");
            System.out.println("*** CENTRALIZED GROUPS *****");
            System.out.println("***** CLIENT *******");
            System.out.println("**************");
            System.out.println("\n\n");
            
            System.out.print("Introduce la IP del servidor: ");
            Scanner scanner = new Scanner(System.in);
            String ip = scanner.next();
            GroupServerInterface stub = (GroupServerInterface) Naming.lookup("rmi://"+ip+":1099/GroupServer");
            System.setProperty("java.security.policy", centralizedgroups.Constants.CLIENT_POLICY);
            if (System.getSecurityManager() == null){
                System.setSecurityManager(new SecurityManager());
            }
            String clientHostname = java.net.Inet4Address.getLocalHost().getHostName();
            
            String alias, galias;             
            System.out.print("Introduce tu alias: ");
            alias = scanner.next();

            while (true) {
                System.out.println("Opciones:");
                System.out.println("\t1 - Crear grupo");
                System.out.println("\t2 - Eliminar grupo");
                System.out.println("\t3 - Añadir miembro");
                System.out.println("\t4 - Eliminar miembro");
                System.out.println("\t5 - Bloquear altas y bajas");
                System.out.println("\t6 - Desbloquear altas y bajas");
                System.out.println("\t7 - Mostrar grupos");
                System.out.println("\t8 - Mostrar miembros");
                System.out.println("\t9 - Terminar ejecución");
                System.out.print("Selecciona una opcion: ");

                switch (scanner.nextInt()) 
                {
                    case 1:
                        System.out.println("----------------------------------------------");
                        System.out.println("1. Crear Grupo");
                        System.out.println("----------------------------------------------");
                        System.out.println("Introduce el alias del grupo: \t");
                        galias = scanner.next();
                        if (stub.createGroup(galias, alias, clientHostname) != -1) 
                            System.out.println("El grupo ha sido creado");
                        else
                            System.out.println("El grupo ya existe");
                        break;
                    case 2:
                        System.out.println("----------------------------------------------");
                        System.out.println("2. Eliminar Grupo");
                        System.out.println("----------------------------------------------");
                        System.out.println("Introduzca el alias del grupo a eliminar\t");
                        if (stub.removeGroup(scanner.next(), alias))
                            System.out.println("Grupo eliminado correctamente");
                        else
                            System.out.println("Error al eliminar el grupo");
                        break;
                        
                    case 3:
                        System.out.println("----------------------------------------------");
                        System.out.println("3. Añadir Miembro");
                        System.out.println("--------------------------------------------");
                        System.out.println("Introduce el alias del grupo en el que desee añadir el miembro\t");
                        galias = scanner.next();
                        int groupID = stub.findGroup(galias);
                        if (groupID == -1)
                            System.out.println("Grupo no encontrado");
                        else 
                        {
                            System.out.println("Introduce el alias del miembro");
                            if (stub.addMember(galias, scanner.next(), clientHostname) == null)
                                System.out.println("Error, el usuario existe o el grupo está bloqueado");
                            else
                                System.out.println("Miembro añadido correctamente");
                        }
                        break;
                        
                    case 4:
                        System.out.println("--------------------------------------------");
                        System.out.println("4. Eliminar Miembro");
                        System.out.println("--------------------------------------------");
                        System.out.println("Introduce el alias del grupo en el que deseas eliminar miembro\t");
                        galias = scanner.next();
                        groupID = stub.findGroup(galias);
                        if (groupID == -1){
                            System.out.println("Grupo no encontrado\t");
                        }
                        else{
                            System.out.println("Introduce el Alias del miembro a eliminar\t");
                            if (stub.removeMember(galias , scanner.next()))
                                System.out.println("Miembro eliminado correctamente\t");
                            else
                                System.out.println("Error\t");
                        }
                        break;

                    case 5:
                        System.out.println("--------------------------------------------");
                        System.out.println("5. Bloquear altas y bajas.");
                        System.out.println("--------------------------------------------");
                         System.out.println("Alias del grupo que deseas bloquear: \t");
                        galias = scanner.next();
                        groupID = stub.findGroup(galias);
                        if (groupID == -1){
                            System.out.println("Grupo no encontrado");
                        }
                        else{
                            if(stub.StopMembers(galias))
                                System.out.println("Grupo " + galias + " bloqueado");
                            else
                                System.out.println("Error en el bloqueo");
                        }
                        break;
                        
                    case 6:
                        System.out.println("--------------------------------------------");
                        System.out.println("6. Desbloquear altas y bajas.");
                        System.out.println("--------------------------------------------");
                        System.out.println("Alias del grupo que deseas desbloquear: \t");
                        galias = scanner.next();
                        groupID = stub.findGroup(galias);
                        if (groupID == -1)
                            System.out.println("Grupo no encontrado. No existe");
                        else 
                        {
                            if(stub.AllowMembers(groupID))
                                System.out.println("Desbloqueo hecho");
                            else
                                System.out.println("Error en el bloqueo");                            
                        }
                        break;

                    case 7:
                        System.out.println("--------------------------------------------");
                        System.out.println("7.  Mostrar grupos");
                        System.out.println("--------------------------------------------");
                        System.out.println("Grupos creados: \t");
                        if (!stub.ListGroups().isEmpty()){
                            for (ObjectGroup group : stub.ListGroups()) 
                            {
                                System.out.println(group.idGroup + " - " + group.aliasGroup);
                            }
                        }else{
                            System.out.println("No existen grupos");
                        }
                        break;

                    case 8:
                        System.out.println("--------------------------------------------");
                        System.out.println("8. Mostrar miembros");
                        System.out.println("--------------------------------------------");
                        System.out.println("Alias del grupo que deseas ver los miembros: \t");
                        galias = scanner.next();
                        if (!stub.ListMembers(galias).isEmpty()){
                            for (String member : stub.ListMembers(galias)) 
                            {
                                System.out.println(member);
                            }
                            System.out.println("\n");
                        }else{
                            System.out.println("No existen miembros en el grupo " + galias);
                        }
                        break;

                    case 9:
                        System.out.println("Usuario desconectado con éxito.");
                        System.exit(0);
                        break;
                        
                    default:
                        System.out.println("Opción no válida, pruebe de nuevo.");
                        break;
                }
            }
        } 
        catch (RemoteException | NotBoundException | MalformedURLException e)
        {
            System.out.println("Se ha producido una excepción en el cliente.");
            System.err.println("Client exception: " + e.toString());
        }
    }

    public Client() throws RemoteException 
    {
        super();
    }
}

