
package ejb;

import java.io.*;
import java.util.LinkedList;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.jms.*;
import javax.naming.InitialContext;

/**
 *
 * @author Francisco Martínez Esteso, Julián Morales Núñez
 */
@Singleton
public class EjBSessionBean implements EjBSessionBeanLocal  {
    
    private int count_msg = -1;
    private LinkedList<String> list_msg= new LinkedList<String>();

    @Override
    public void addMsg(String mensaje) {
        
        try {
            // Obtenemos el contexto JNDI actual
            InitialContext iniCtx = new InitialContext();
            
            // Buscamos los recursos sobre él.
            TopicConnectionFactory tcf = (TopicConnectionFactory) iniCtx.lookup("jms/FactoriaConexiones");
            Topic t = (Topic) iniCtx.lookup("jms/Noticias");
            
            // Creamos la conexión sobre la factoria de conexiones.
            TopicConnection conexion = tcf.createTopicConnection();
            TopicSession sesion = conexion.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
            conexion.start();
            
            // Creamos el publisher sobre la sesión.
            TopicPublisher publisher = sesion.createPublisher(t);
            
            // Creamos y publicamos el mensaje.
            TextMessage msj = sesion.createTextMessage();
            msj.setText(mensaje);
            publisher.publish(msj);
            publisher.close();
            
        } catch (Exception ex) {
            System.out.printf("Error");
        }
            
    }
    
    @Override
    public int getNumber() {
        return count_msg;
    }

    @Override
    public LinkedList<String> getList() {
        return list_msg;
    }

    @Override
    public void addToList(String m) {
        this.list_msg.add(m);
        this.count_msg++;
    }
    
    
    @PostConstruct
    public void inicializacion(){
      
        File archivo = null;
        FileReader fr = null;
        BufferedReader br = null;

        try {
            // Apertura del fichero y creacion de BufferedReader para poder 
            // hacer una lectura comoda (disponer del metodo readLine()).

            // https://docs.oracle.com/javase/7/docs/api/java/io/FileReader.html
            // https://docs.oracle.com/javase/7/docs/api/java/io/BufferedReader.html

            archivo = new File ("C:\\Users\\JULIÁN\\Desktop\\mensajes.txt");
            fr = new FileReader (archivo);
            br = new BufferedReader(fr);

            // Leemos el fichero
            String linea;
            while((linea = br.readLine()) != null)
               addToList(linea);
        }
        catch(Exception e){
            e.printStackTrace();
        }finally{
        try{                    
           if( null != fr ){   
              fr.close();     
           }                  
        }catch (Exception e2){ 
            e2.printStackTrace();
        }
        }
    }
    
}
